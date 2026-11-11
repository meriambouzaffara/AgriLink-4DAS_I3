package com.agrilink.cooperative.repository;

import com.agrilink.cooperative.entity.Cooperative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CooperativeRepository extends JpaRepository<Cooperative, Long> {

    Optional<Cooperative> findByName(String name);

    boolean existsByName(String name);
}
