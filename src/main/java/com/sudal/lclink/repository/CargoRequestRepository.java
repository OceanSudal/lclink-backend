package com.sudal.lclink.repository;

import com.sudal.lclink.entity.CargoItem;
import com.sudal.lclink.entity.CargoRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CargoRequestRepository extends JpaRepository<CargoRequest, Integer> {
    Optional<CargoRequest> findByRequestId(Integer requestId);
    Page<CargoRequest> findByUser_UserId(String userId, Pageable pageable);

    default Page<CargoRequest> findByUserId(String userId, Pageable pageable) {
        return findByUser_UserId(userId, pageable);
    }
}
