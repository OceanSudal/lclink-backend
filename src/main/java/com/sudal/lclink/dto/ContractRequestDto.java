package com.sudal.lclink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractRequestDto {
    private String onchainContractId;
    private String shipperCompanyId;
    private String forwarderCompanyId;
    private Double totalPrice;
    private String currency;
    private String originPortCode;
    private String destinationPortCode;
    private String incotermsCode;
}