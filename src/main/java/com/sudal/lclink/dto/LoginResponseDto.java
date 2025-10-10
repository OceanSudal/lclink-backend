package com.sudal.lclink.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String accessToken;
    private String userId;
    private String name;
    private Integer companyId;
}
