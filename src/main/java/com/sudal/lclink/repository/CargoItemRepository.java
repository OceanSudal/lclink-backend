package com.sudal.lclink.repository;

import com.sudal.lclink.entity.CargoItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CargoItemRepository extends JpaRepository<CargoItem, Integer> {
    Optional<CargoItem> findByItemId(Integer itemId);
    Page<CargoItem> findByUser_UserId(String userId, Pageable pageable);

    default Page<CargoItem> findByUserId(String userId, Pageable pageable) {
        return findByUser_UserId(userId, pageable);
    }
}
