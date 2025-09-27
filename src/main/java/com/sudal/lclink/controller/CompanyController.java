package com.sudal.lclink.controller;

import com.sudal.lclink.dto.CompanyDto;
import com.sudal.lclink.dto.UserDto;
import com.sudal.lclink.entity.Company;
import com.sudal.lclink.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    // CREATE
    @PostMapping
    public Integer register(@RequestBody CompanyDto dto) {
        return companyService.register(dto);
    }

    // READ
    @GetMapping("/{companyId}")
    public CompanyDto get(@PathVariable("companyId") Integer companyId) {
        return companyService.get(companyId);
    }

    // READ - List
    @GetMapping
    public Page<CompanyDto> list(@RequestParam(required = false, name = "q") String keyword,
                                 @PageableDefault(size = 10, sort = "companyId") Pageable pageable) {
        return companyService.list(keyword, pageable);
    }

    // UPDATE
    @PutMapping("/{companyId}")
    public CompanyDto update(@PathVariable("companyId") Integer companyId,
                             @RequestBody CompanyDto dto) {
        return companyService.update(companyId, dto);
    }

    // DELETE
    @DeleteMapping("/{companyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") Integer companyId) {
        companyService.delete(companyId);
    }
}
