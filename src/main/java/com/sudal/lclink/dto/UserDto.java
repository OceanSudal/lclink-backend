package com.sudal.lclink.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {

    private String userId;
    private Integer companyId;
    private String email;
    private String password;
    private String name;
    private LocalDateTime createdAt;

    @JsonIgnore
    private MultipartFile certificateFile;

    private String certificateFilename;
    private Boolean hasCertificate;
}
