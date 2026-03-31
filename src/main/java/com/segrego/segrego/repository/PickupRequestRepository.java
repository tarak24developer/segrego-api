package com.segrego.segrego.repository;

import com.segrego.segrego.model.PickupRequest;
import com.segrego.segrego.model.PickupStatus;
import com.segrego.segrego.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickupRequestRepository extends JpaRepository<PickupRequest, Long> {
    List<PickupRequest> findByUserOrderByCreatedAtDesc(User user);

    List<PickupRequest> findByStatusOrderByCreatedAtAsc(PickupStatus status);

    List<PickupRequest> findByAssignedVendorOrderByCreatedAtDesc(User assignedVendor);

    long countByStatus(PickupStatus status);
}
