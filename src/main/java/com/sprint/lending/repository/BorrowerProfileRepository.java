package com.sprint.lending.repository;

import com.sprint.lending.entity.BorrowerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BorrowerProfileRepository extends JpaRepository<BorrowerProfile, Long> {
    Optional<BorrowerProfile> findByUserEmail(String email);
    boolean existsByPan(String pan);
}