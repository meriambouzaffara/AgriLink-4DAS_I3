package com.agrilink.order.repository;

import com.agrilink.order.entity.Order;
import com.agrilink.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientId(Long clientId);

    List<Order> findByStatut(OrderStatus statut);

    List<Order> findByClientIdAndStatut(Long clientId, OrderStatus statut);

    @EntityGraph(attributePaths = "lignes")
    Optional<Order> findWithLignesById(Long id);

    @EntityGraph(attributePaths = "lignes")
    List<Order> findByClientIdOrderByDateCommandeDescIdDesc(Long clientId);

    // MariaDB 10.4 accepts FOR UPDATE; the current MySQL dialect renders
    // FOR UPDATE OF alias, which this server rejects.
    @Query(value = "select * from commande where id = :id for update", nativeQuery = true)
    Optional<Order> findByIdForUpdate(@Param("id") Long id);
}
