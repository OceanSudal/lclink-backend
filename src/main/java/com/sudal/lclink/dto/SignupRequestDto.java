package com.sudal.lclink.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    private CompanyDto company;
    private UserDto user;
}
