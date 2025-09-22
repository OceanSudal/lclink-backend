package com.sudal.lclink.controller;

import com.sudal.lclink.dto.CompanyDto;
import com.sudal.lclink.dto.UserDto;
import com.sudal.lclink.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping("/register")
    public Integer register(@RequestBody CompanyDto companyDto) {
        return companyService.register(companyDto);
    }
}
