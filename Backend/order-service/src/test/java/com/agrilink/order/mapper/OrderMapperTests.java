package com.agrilink.order.mapper;

import com.agrilink.order.entity.Order;
import com.agrilink.order.entity.OrderItem;
import com.agrilink.order.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class OrderMapperTests {
    private final OrderMapper mapper = new OrderMapper();

    @Test
    void mapsOrderAndNestedItemsWithoutParentReference() {
        var order = new Order();
        order.setId(7L);
        order.setClientId(12L);
        order.setMontantTotal(new BigDecimal("25.000"));
        order.setStatut(OrderStatus.CONFIRMED);

        var item = new OrderItem();
        item.setId(9L);
        item.setProduitId(101L);
        item.setQuantite(new BigDecimal("5.000"));
        item.setPrixUnitaire(new BigDecimal("5.000"));
        item.setSousTotal(new BigDecimal("25.000"));
        item.setCommande(order);
        order.setLignes(List.of(item));

        var response = mapper.toResponse(order);
        assertEquals(7L, response.id());
        assertEquals(12L, response.clientId());
        assertEquals(order.getDateCommande(), response.dateCommande());
        assertEquals(new BigDecimal("25.000"), response.montantTotal());
        assertEquals(OrderStatus.CONFIRMED, response.statut());
        assertEquals(1, response.lignes().size());
        assertEquals(101L, response.lignes().get(0).produitId());
        assertEquals(new BigDecimal("5.000"), response.lignes().get(0).prixUnitaire());
        assertEquals(new BigDecimal("25.000"), response.lignes().get(0).sousTotal());
        assertFalse(Arrays.stream(response.lignes().get(0).getClass().getRecordComponents())
                .anyMatch(component -> component.getName().equals("commande")));
    }

    @Test
    void mapsListOfOrders() {
        var first = new Order();
        first.setId(1L);
        var second = new Order();
        second.setId(2L);
        var responses = mapper.toResponses(List.of(first, second));
        assertEquals(List.of(1L, 2L), responses.stream().map(r -> r.id()).toList());
    }
}
