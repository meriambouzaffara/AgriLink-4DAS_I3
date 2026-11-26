package com.agrilink.order.service.impl;

import com.agrilink.order.client.ProductClient;
import com.agrilink.order.client.ProductInfo;
import com.agrilink.order.client.StockItem;
import com.agrilink.order.dto.OrderItemRequest;
import com.agrilink.order.dto.OrderRequest;
import com.agrilink.order.dto.OrderResponse;
import com.agrilink.order.entity.Order;
import com.agrilink.order.entity.OrderItem;
import com.agrilink.order.entity.OrderStatus;
import com.agrilink.order.exception.BusinessException;
import com.agrilink.order.exception.InvalidOrderException;
import com.agrilink.order.exception.ResourceNotFoundException;
import com.agrilink.order.mapper.OrderMapper;
import com.agrilink.order.repository.OrderRepository;
import com.agrilink.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    private static final int SCALE = 3;
    private static final int MAX_INTEGER_DIGITS = 12;

    private final OrderRepository orders;
    private final ProductClient products;
    private final OrderMapper mapper;

    public OrderServiceImpl(OrderRepository orders, ProductClient products, OrderMapper mapper) {
        this.orders = orders;
        this.products = products;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(Long customerId, OrderRequest request) {
        requireId(customerId, "Customer ID");
        if (request == null || request.lignes() == null || request.lignes().isEmpty())
            throw new InvalidOrderException("Order must contain at least one line");

        Map<Long, BigDecimal> requested = new LinkedHashMap<>();
        for (OrderItemRequest line : request.lignes()) {
            if (line == null) throw new InvalidOrderException("Order line is required");
            requireId(line.produitId(), "Product ID");
            BigDecimal quantity = checkedQuantity(line.quantite());
            requested.merge(line.produitId(), quantity, (left, right) ->
                    checkedQuantity(left.add(right)));
        }

        Map<Long, ProductInfo> info = new LinkedHashMap<>();
        for (var entry : requested.entrySet()) {
            ProductInfo product = products.getProduct(entry.getKey());
            if (product == null || product.id() == null || !product.id().equals(entry.getKey())
                    || product.name() == null || product.name().isBlank()
                    || product.availableQuantity() == null || product.availableQuantity().signum() < 0)
                throw new BusinessException("Product information is unavailable for " + entry.getKey());
            checkedPrice(product.unitPrice());
            if (!products.isAvailable(entry.getKey(), entry.getValue()))
                throw new BusinessException("Insufficient stock for product " + entry.getKey());
            info.put(entry.getKey(), product);
        }

        Order order = new Order();
        order.setClientId(customerId);
        order.setDateCommande(LocalDateTime.now());
        order.setStatut(OrderStatus.PENDING);
        BigDecimal total = BigDecimal.ZERO.setScale(SCALE);
        for (OrderItemRequest requestLine : request.lignes()) {
            BigDecimal quantity = checkedQuantity(requestLine.quantite()).setScale(SCALE);
            BigDecimal price = checkedPrice(info.get(requestLine.produitId()).unitPrice());
            BigDecimal subtotal = checkedMoney(quantity.multiply(price).setScale(SCALE, RoundingMode.HALF_UP));
            OrderItem item = new OrderItem();
            item.setProduitId(requestLine.produitId());
            item.setQuantite(quantity);
            item.setPrixUnitaire(price);
            item.setSousTotal(subtotal);
            item.setCommande(order);
            order.getLignes().add(item);
            total = checkedMoney(total.add(subtotal));
        }
        order.setMontantTotal(total);
        return mapper.toResponse(orders.saveAndFlush(order));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId, Long customerId) {
        requireId(orderId, "Order ID");
        requireId(customerId, "Customer ID");
        Order order = orders.findWithLignesById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + orderId + " not found"));
        verifyOwner(order, customerId);
        return mapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrders(Long customerId) {
        requireId(customerId, "Customer ID");
        return mapper.toResponses(orders.findByClientIdOrderByDateCommandeDescIdDesc(customerId));
    }

    @Override
    @Transactional
    public OrderResponse confirmOrder(Long orderId, Long customerId) {
        Order order = lockedOrder(orderId, customerId);
        if (order.getStatut() == OrderStatus.CONFIRMED) return mapper.toResponse(order);
        if (order.getStatut() != OrderStatus.PENDING)
            throw new BusinessException("Only a pending order can be confirmed");
        List<StockItem> items = stockItems(order);
        products.reserveStock(orderId, items);
        onRollback(() -> products.releaseStock(orderId));
        try {
            order.setStatut(OrderStatus.CONFIRMED);
            return mapper.toResponse(orders.saveAndFlush(order));
        } catch (RuntimeException failure) {
            order.setStatut(OrderStatus.PENDING);
            products.releaseStock(orderId);
            throw failure;
        }
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long customerId) {
        Order order = lockedOrder(orderId, customerId);
        if (order.getStatut() == OrderStatus.CANCELLED) return mapper.toResponse(order);
        if (order.getStatut() == OrderStatus.PENDING) {
            order.setStatut(OrderStatus.CANCELLED);
            return mapper.toResponse(orders.saveAndFlush(order));
        }
        if (order.getStatut() != OrderStatus.CONFIRMED)
            throw new BusinessException("Order can no longer be cancelled");
        List<StockItem> items = stockItems(order);
        if (!products.releaseStock(orderId))
            throw new BusinessException("No matching live stock reservation for order " + orderId);
        boolean[] restored = {false};
        Runnable restore = () -> {
            if (!restored[0]) {
                products.reserveStock(orderId, items);
                restored[0] = true;
            }
        };
        onRollback(restore);
        try {
            order.setStatut(OrderStatus.CANCELLED);
            return mapper.toResponse(orders.saveAndFlush(order));
        } catch (RuntimeException failure) {
            order.setStatut(OrderStatus.CONFIRMED);
            try {
                restore.run();
            } catch (RuntimeException compensationFailure) {
                failure.addSuppressed(compensationFailure);
            }
            throw failure;
        }
    }

    @Override
    @Transactional
    public OrderResponse updateDeliveryStatus(Long orderId, OrderStatus newStatus) {
        Order order = lockedOrder(orderId, null);
        if (newStatus == null) throw new InvalidOrderException("Delivery status is required");
        if (order.getStatut() == newStatus) return mapper.toResponse(order);
        boolean allowed = switch (order.getStatut()) {
            case CONFIRMED -> newStatus == OrderStatus.IN_PREPARATION;
            case IN_PREPARATION -> newStatus == OrderStatus.SHIPPED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            default -> false;
        };
        if (!allowed) throw new BusinessException("Invalid order status transition");
        order.setStatut(newStatus);
        return mapper.toResponse(orders.saveAndFlush(order));
    }

    private Order lockedOrder(Long orderId, Long customerId) {
        requireId(orderId, "Order ID");
        if (customerId != null) requireId(customerId, "Customer ID");
        Order order = orders.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + orderId + " not found"));
        if (customerId != null) verifyOwner(order, customerId);
        return order;
    }

    private void verifyOwner(Order order, Long customerId) {
        if (!customerId.equals(order.getClientId()))
            throw new ResourceNotFoundException("Order " + order.getId() + " not found");
    }

    private List<StockItem> stockItems(Order order) {
        Map<Long, BigDecimal> totals = new LinkedHashMap<>();
        order.getLignes().forEach(item -> totals.merge(item.getProduitId(), item.getQuantite(), BigDecimal::add));
        return totals.entrySet().stream().map(entry -> new StockItem(entry.getKey(), entry.getValue())).toList();
    }

    private void requireId(Long id, String name) {
        if (id == null || id <= 0) throw new InvalidOrderException(name + " must be positive");
    }

    private BigDecimal checkedQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.signum() <= 0 || quantity.scale() > SCALE ||
                quantity.precision() - quantity.scale() > MAX_INTEGER_DIGITS)
            throw new InvalidOrderException("Quantity must be positive and fit DECIMAL(15,3)");
        return quantity;
    }

    private BigDecimal checkedPrice(BigDecimal price) {
        if (price == null || price.signum() <= 0 || price.scale() > SCALE)
            throw new BusinessException("Product unit price is invalid");
        try {
            return checkedMoney(price.setScale(SCALE));
        } catch (InvalidOrderException exception) {
            throw new BusinessException("Product unit price exceeds storage precision");
        }
    }

    private BigDecimal checkedMoney(BigDecimal amount) {
        if (amount.precision() - amount.scale() > MAX_INTEGER_DIGITS)
            throw new InvalidOrderException("Order amount exceeds DECIMAL(15,3)");
        return amount;
    }

    private void onRollback(Runnable compensation) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status != STATUS_COMMITTED) compensation.run();
                }
            });
        }
    }
}
