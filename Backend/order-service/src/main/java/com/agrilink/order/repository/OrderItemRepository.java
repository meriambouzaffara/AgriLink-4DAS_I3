package com.agrilink.order.repository;

import com.agrilink.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByCommandeId(Long commandeId);

    List<OrderItem> findByProduitId(Long produitId);
}
