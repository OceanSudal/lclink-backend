package com.sudal.lclink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractRequestDto {
    private String onchainContractId;
    private Integer shipperCompanyId;
    private Integer forwarderCompanyId;
    private Double totalPrice;
    private String currency;
    private String originPortCode;
    private String destinationPortCode;
    private String incotermsCode;
}

