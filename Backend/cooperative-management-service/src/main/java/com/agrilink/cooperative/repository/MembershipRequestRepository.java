package com.agrilink.cooperative.repository;

import com.agrilink.cooperative.entity.MembershipRequest;
import com.agrilink.cooperative.entity.MembershipRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRequestRepository extends JpaRepository<MembershipRequest, Long> {

    List<MembershipRequest> findByCooperativeId(Long cooperativeId);

    List<MembershipRequest> findByCooperativeIdAndStatus(Long cooperativeId, MembershipRequestStatus status);

    Optional<MembershipRequest> findByCooperativeIdAndFarmerUserId(Long cooperativeId, Long farmerUserId);

    boolean existsByCooperativeIdAndFarmerUserIdAndStatus(Long cooperativeId, Long farmerUserId, MembershipRequestStatus status);
}
