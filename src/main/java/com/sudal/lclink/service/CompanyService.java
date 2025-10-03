package com.sudal.lclink.service;

import com.sudal.lclink.dto.CompanyDto;
import com.sudal.lclink.entity.Company;
import com.sudal.lclink.exception.AlreadyExistElementException; // ← 없다면 아래 예외 클래스 참고
import com.sudal.lclink.repository.CompanyRepository;
import com.sudal.lclink.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    // CREATE
    public Integer register(CompanyDto companyDto) {
        if (companyDto.getBusinessNum() == null || companyDto.getBusinessNum().trim().isEmpty()) {
            throw new IllegalArgumentException("사업자번호(businessNum)는 필수입니다.");
        }
        final String bn = normalizeBusinessNum(companyDto.getBusinessNum());

        if (companyRepository.existsByBusinessNum(bn)) {
            throw new AlreadyExistElementException("이미 등록된 사업자번호입니다: " + bn);
        }

        Company company = Company.builder()
                .companyName(companyDto.getCompanyName())
                .businessNum(bn)
                .address(companyDto.getAddress())
                .companyType(companyDto.getCompanyType())
                .build();

        try {
            return companyRepository.save(company).getCompanyId();
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistElementException("이미 등록된 사업자번호입니다: " + bn, e);
        }
    }


    // READ
    @Transactional(readOnly = true)
    public CompanyDto get(Integer companyId) {
        Company c = companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 companyId입니다: " + companyId));
        return toDto(c);
    }

    // READ - List
    @Transactional(readOnly = true)
    public Page<CompanyDto> list(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return companyRepository
                    .findByCompanyNameContainingIgnoreCaseOrBusinessNumContainingIgnoreCase(keyword.trim(), keyword.trim(), pageable)
                    .map(this::toDto);
        }
        return companyRepository.findAll(pageable).map(this::toDto);
    }

    // UPDATE
    public CompanyDto update(Integer companyId, CompanyDto dto) {
        Company c = companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 companyId입니다: " + companyId));

        if (dto.getCompanyName() != null) c.setCompanyName(dto.getCompanyName());

        if (dto.getAddress() != null) c.setAddress(dto.getAddress());

        if (dto.getCompanyType() != null) c.setCompanyType(dto.getCompanyType());

        final String bn = normalizeBusinessNum(dto.getBusinessNum());
        if (dto.getBusinessNum() != null && companyRepository.existsByBusinessNum(bn)) {
            throw new AlreadyExistElementException("이미 등록된 사업자번호입니다: " + bn);
        }
        if (dto.getBusinessNum() != null) c.setBusinessNum(dto.getBusinessNum());

        return toDto(c);
    }

    // DELETE
    public void delete(Integer companyId) {
        Company c = companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 companyId입니다: " + companyId));

        if (userRepository.existsByCompany_CompanyId(companyId)) {
            throw new IllegalArgumentException("해당 회사에 소속된 사용자들이 있어 삭제할 수 없습니다.");
        }
        companyRepository.delete(c);
    }

    private String normalizeBusinessNum(String raw) {
        return raw.trim();
    }

    private CompanyDto toDto(Company c) {
        CompanyDto dto = new CompanyDto();
        dto.setCompanyId(c.getCompanyId());
        dto.setCompanyName(c.getCompanyName());
        dto.setBusinessNum(c.getBusinessNum());
        dto.setAddress(c.getAddress());
        dto.setCompanyType(c.getCompanyType());
        return dto;
    }
}
