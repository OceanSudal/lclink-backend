package com.sudal.lclink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponseDto {
    private boolean success;
    private String message;
    private Object data;

    public static ContractResponseDto success(Object data) {
        return new ContractResponseDto(true, "Success", data);
    }

    public static ContractResponseDto error(String message) {
        return new ContractResponseDto(false, message, null);
    }
}