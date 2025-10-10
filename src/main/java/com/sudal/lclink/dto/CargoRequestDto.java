package com.sudal.lclink.dto;

import com.sudal.lclink.entity.CargoRequest;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CargoRequestDto {

    private Integer requestId;
    private Integer itemId;
    private Integer shipperCompanyId;
    private String originPortCode;
    private String destinationPortCode;
    private LocalDate readyToLoadDate;
    private String incotermsCode;
    private String requestStatus;
    private LocalDateTime createdAt;

    public static CargoRequestDto from(CargoRequest entity) {
        return CargoRequestDto.builder()
                .requestId(entity.getRequestId())
                .itemId(entity.getCargoItem().getItemId())
                .shipperCompanyId(entity.getShipperCompany().getCompanyId())
                .originPortCode(entity.getOriginPortCode())
                .destinationPortCode(entity.getDestinationPortCode())
                .readyToLoadDate(entity.getReadyToLoadDate())
                .incotermsCode(entity.getIncotermsCode())
                .requestStatus(entity.getRequestStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
