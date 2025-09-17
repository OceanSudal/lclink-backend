package com.sudal.lclink.service;

import com.sudal.lclink.dto.CargoItemDto;
import com.sudal.lclink.entity.CargoItem;
import com.sudal.lclink.repository.CargoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CargoItemService {
    private final CargoItemRepository cargoItemRepository;

    public String register(CargoItemDto cargoItemDto){
        Optional<CargoItem> findCargoItem = cargoItemRepository.findByItemId(cargoItemDto.getItemId());

        CargoItem cargoItem = CargoItem.builder()
                .itemId(cargoItemDto.getItemId())
                .itemName(cargoItemDto.getItemName())
                .pol(cargoItemDto.getPol())
                .pod(cargoItemDto.getPod())
                .hsCode(cargoItemDto.getHsCode())
                .quantity(cargoItemDto.getQuantity())
                .width_cm(cargoItemDto.getWidth_cm())
                .length_cm(cargoItemDto.getLength_cm())
                .height_cm(cargoItemDto.getHeight_cm())
                .cbm(cargoItemDto.getCbm())
                .weight_kg(cargoItemDto.getWeight_kg())
                .etd(cargoItemDto.getEtd())
                .build();

        cargoItemRepository.save(cargoItem);
        return "등록되었습니다.";
    }

}
