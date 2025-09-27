package com.sudal.lclink.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {

    private String userId;
    private Integer companyId;
    private String userCountry;
    private String email;
    private String password;
    private String name;
    private String userRole;
    private LocalDateTime createdAt;
}
