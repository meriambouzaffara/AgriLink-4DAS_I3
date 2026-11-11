package com.agrilink.cooperative.repository;

import com.agrilink.cooperative.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByCooperativeId(Long cooperativeId);

    Optional<Membership> findByCooperativeIdAndFarmerUserId(Long cooperativeId, Long farmerUserId);

    boolean existsByCooperativeIdAndFarmerUserId(Long cooperativeId, Long farmerUserId);
}
