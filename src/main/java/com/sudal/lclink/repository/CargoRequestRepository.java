package com.sudal.lclink.repository;

import com.sudal.lclink.entity.CargoItem;
import com.sudal.lclink.entity.CargoRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CargoRequestRepository extends JpaRepository<CargoRequest, Integer> {
    Optional<CargoRequest> findByRequestId(Integer requestId);
    List<CargoRequest> findByShipperUser_UserId(String userId);


}
