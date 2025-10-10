package com.sudal.lclink.service;

import com.sudal.lclink.dto.CargoItemDto;
import com.sudal.lclink.entity.CargoItem;
import com.sudal.lclink.entity.Company;
import com.sudal.lclink.entity.User;
import com.sudal.lclink.repository.CargoItemRepository;
import com.sudal.lclink.repository.CompanyRepository;
import com.sudal.lclink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CargoItemService {
    private final CargoItemRepository cargoItemRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    // CREATE
    public CargoItemDto register(CargoItemDto dto) {
        User owner = userRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + dto.getUserId()));


        CargoItem entity = CargoItem.create(
                owner,
                dto.getItemName(),
                dto.getPol(),
                dto.getPod(),
                dto.getIncoterms(),
                dto.getHsCode(),
                dto.getQuantity(),
                dto.getWidthCm(),
                dto.getLengthCm(),
                dto.getHeightCm(),
                dto.getWeightKg(),
                dto.getEtd(),
                dto.getPackagingType(),
                dto.getItemDescription()
        );

        CargoItem saved = cargoItemRepository.save(entity);
        return CargoItemDto.from(saved);
    }

    // READ (by id)
    @Transactional(readOnly = true)
    public CargoItemDto getCargoItem(Integer itemId) {
        CargoItem found = cargoItemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("화물을 찾을 수 없습니다. id=" + itemId));
        return CargoItemDto.from(found);
    }

    // LIST (전체)
    @Transactional(readOnly = true)
    public Page<CargoItemDto> list(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "itemId"));
        Page<CargoItem> pageData = (userId == null || userId.isBlank())
                ? cargoItemRepository.findAll(pageable)
                : cargoItemRepository.findByUserId(userId, pageable);

        return pageData.map(CargoItemDto::from);
    }

    // LIST (userId)
    @Transactional(readOnly = true)
    public Page<CargoItemDto> listByUserId(String userId, int page, int size) {
        if (userId == null || userId.isBlank()) {
            return Page.empty(PageRequest.of(page, size));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "itemId"));
        return cargoItemRepository.findByUser_UserId(userId, pageable)
                .map(CargoItemDto::from);
    }

    // UPDATE
    public CargoItemDto update(Integer itemId, CargoItemDto dto) {
        CargoItem target = cargoItemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("화물이 존재하지 않습니다. id=" + itemId));

        target.updateDetails(
                dto.getItemName(),
                dto.getPol(),
                dto.getPod(),
                dto.getIncoterms(),
                dto.getHsCode(),
                dto.getQuantity(),
                dto.getWidthCm(),
                dto.getLengthCm(),
                dto.getHeightCm(),
                dto.getWeightKg(),
                dto.getEtd(),
                dto.getPackagingType(),
                dto.getItemDescription()
        );

        return CargoItemDto.from(target);
    }

    // DELETE
    public void delete(Integer itemId) {
        CargoItem target = cargoItemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 화물입니다. itemId=" + itemId));
        cargoItemRepository.deleteById(itemId);
    }
}