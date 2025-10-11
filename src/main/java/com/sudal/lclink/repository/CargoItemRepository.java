package com.sudal.lclink.repository;

import com.sudal.lclink.entity.CargoItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CargoItemRepository extends JpaRepository<CargoItem, Integer> {
    Optional<CargoItem> findByItemId(Integer itemId);
    Page<CargoItem> findByUser_UserId(String userId, Pageable pageable);

    // 1. 특정 회사 유형(e.g., "SHIPPER")을 가진 사용자의 모든 화물을 조회
    List<CargoItem> findAllByUser_Company_CompanyType(String companyType);

    // 2. 추천된 ID 목록에 해당하는 모든 화물을 한 번에 조회
    List<CargoItem> findAllByItemIdIn(List<Integer> itemIds);

    default Page<CargoItem> findByUserId(String userId, Pageable pageable) {
        return findByUser_UserId(userId, pageable);
    }
}
