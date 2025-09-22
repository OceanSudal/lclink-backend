package com.sudal.lclink.repository;

import com.sudal.lclink.entity.CargoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CargoItemRepository extends JpaRepository<CargoItem, Long> {
    Optional<CargoItem> findByItemId(Integer itemId);

}
