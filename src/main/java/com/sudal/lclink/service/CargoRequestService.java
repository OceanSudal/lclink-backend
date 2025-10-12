package com.sudal.lclink.service;

import com.sudal.lclink.dto.CargoItemDto;
import com.sudal.lclink.dto.CargoRequestDto;
import com.sudal.lclink.dto.CompanyDto;
import com.sudal.lclink.entity.CargoItem;
import com.sudal.lclink.entity.CargoRequest;
import com.sudal.lclink.entity.Company;
import com.sudal.lclink.entity.User;
import com.sudal.lclink.exception.AlreadyExistElementException;
import com.sudal.lclink.repository.CargoItemRepository;
import com.sudal.lclink.repository.CargoRequestRepository;
import com.sudal.lclink.repository.CompanyRepository;
import com.sudal.lclink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CargoRequestService {

    private final CargoRequestRepository cargoRequestRepository;
    private final CargoItemRepository cargoItemRepository;
    private final UserRepository userRepository;

    // CREATE
    public CargoRequestDto register(CargoRequestDto dto) {
        CargoItem cargoItem = cargoItemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("화물 항목을 찾을 수 없습니다. itemId=" + dto.getItemId()));

        User shipperUser = userRepository.findById(dto.getShipperUserId())
                .orElseThrow(() -> new IllegalArgumentException("화주 사용자를 찾을 수 없습니다. userId=" + dto.getShipperUserId()));

        CargoRequest entity = CargoRequest.builder()
                .cargoItem(cargoItem)
                .shipperUser(shipperUser)
                .originPortCode(dto.getOriginPortCode())
                .destinationPortCode(dto.getDestinationPortCode())
                .readyToLoadDate(dto.getReadyToLoadDate())
                .incotermsCode(dto.getIncotermsCode())
                .requestStatus(dto.getRequestStatus() != null ? dto.getRequestStatus() : "OPEN")
                .build();

        CargoRequest saved = cargoRequestRepository.save(entity);
        return CargoRequestDto.from(saved);
    }

    // READ (단건)
    @Transactional(readOnly = true)
    public CargoRequestDto get(Integer id) {
        CargoRequest found = cargoRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다. id=" + id));
        return CargoRequestDto.from(found);
    }

    // READ (userId)
    @Transactional(readOnly = true)
    public List<CargoRequestDto> getUserRequest(String userId) {
        List<CargoRequest> requests = cargoRequestRepository.findByShipperUser_UserId(userId);

        if (requests.isEmpty()){
                throw new IllegalArgumentException("사용자을 찾을 수 없습니다. userId=" + userId);
        }

        return requests.stream()
                .map(CargoRequestDto::from)
                .toList();
    }

    // READ (전체)
    @Transactional(readOnly = true)
    public List<CargoRequestDto> list() {
        return cargoRequestRepository.findAll().stream()
                .map(CargoRequestDto::from)
                .toList();
    }

    // UPDATE
    public CargoRequestDto update(Integer requestId, CargoRequestDto dto) {
        CargoRequest c = cargoRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 requestId입니다: " + requestId));


        if (dto.getOriginPortCode() != null) c.setOriginPortCode(dto.getOriginPortCode());
        if (dto.getDestinationPortCode() != null) c.setDestinationPortCode(dto.getDestinationPortCode());
        if (dto.getReadyToLoadDate() != null) c.setReadyToLoadDate(dto.getReadyToLoadDate());
        if (dto.getIncotermsCode() != null) c.setIncotermsCode(dto.getIncotermsCode());
        if (dto.getRequestStatus() != null) c.setRequestStatus(dto.getRequestStatus());

        CargoRequest updated = cargoRequestRepository.save(c);
        return CargoRequestDto.from(updated);
    }


    // DELETE
    public void delete(Integer id) {
        if (!cargoRequestRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 요청입니다. id=" + id);
        }
        cargoRequestRepository.deleteById(id);
    }

    private CargoRequestDto toDto(CargoRequest c) {
        CargoRequestDto dto = new CargoRequestDto();
        dto.setOriginPortCode(c.getOriginPortCode());
        dto.setDestinationPortCode(c.getDestinationPortCode());
        dto.setReadyToLoadDate(c.getReadyToLoadDate());
        dto.setIncotermsCode(c.getIncotermsCode());
        dto.setRequestStatus(c.getRequestStatus());
        return dto;
    }
}
