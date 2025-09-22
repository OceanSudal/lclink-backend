//package com.sudal.lclink.service;
//
//import com.sudal.lclink.dto.CompanyDto;
//import com.sudal.lclink.entity.Company;
//import com.sudal.lclink.entity.User;
//import com.sudal.lclink.repository.CompanyRepository;
//import com.sudal.lclink.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class CompanyService {
//    private final CompanyRepository companyRepository;
//
//    public Integer register(CompanyDto companyDto) {
//        Optional<Company> findCompany = companyRepository.findByCompanyId(companyDto.getCompanyId());
//
//        Company company = Company.builder()
//                .companyName(companyDto.getCompanyName())
//                .businessNum(companyDto.getBusinessNum())
//                .address(companyDto.getAddress())
//                .companyType(companyDto.getCompanyType())
//                .build();
//
//        Company saved = companyRepository.save(company);
//        return saved.getCompanyId();
//    }
//}

package com.sudal.lclink.service;

import com.sudal.lclink.dto.CompanyDto;
import com.sudal.lclink.entity.Company;
import com.sudal.lclink.exception.AlreadyExistElementException; // ← 없다면 아래 예외 클래스 참고
import com.sudal.lclink.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public Integer register(CompanyDto companyDto) {
        // 1) 필수값 간단 검증 (컨트롤러 @Valid로도 보강 권장)
        if (companyDto.getBusinessNum() == null || companyDto.getBusinessNum().trim().isEmpty()) {
            throw new IllegalArgumentException("사업자번호(businessNum)는 필수입니다.");
        }

        // 2) 포맷 정규화 규칙(정책 선택)
        final String bn = normalizeBusinessNum(companyDto.getBusinessNum());

        // 3) 사전 중복 체크 → 있으면 409 (GlobalExceptionHandler가 매핑)
        if (companyRepository.existsByBusinessNum(bn)) {
            throw new AlreadyExistElementException("이미 등록된 사업자번호입니다: " + bn);
        }

        // 4) 저장 (동시요청 경합 시 DB 유니크키에 걸릴 수 있으니 캐치해서 409로 매핑)
        Company company = Company.builder()
                .companyName(companyDto.getCompanyName())
                .businessNum(bn)
                .address(companyDto.getAddress())
                .companyType(companyDto.getCompanyType())
                .build();

        try {
            return companyRepository.save(company).getCompanyId();
        } catch (DataIntegrityViolationException e) {
            // 거의 동시에 같은 번호가 들어온 경우 대비
            throw new AlreadyExistElementException("이미 등록된 사업자번호입니다: " + bn, e);
        }
    }


    // ⬇︎ 하나의 규칙으로 '저장'과 '조회' 모두에 동일 적용하세요.
    private String normalizeBusinessNum(String raw) {
        // [옵션 A] 하이픈 포함 저장: 입력만 trim
        return raw.trim();

        // [옵션 B] 하이픈 제거 저장(선택):
        // return raw.replaceAll("\\D", "");
    }
}
