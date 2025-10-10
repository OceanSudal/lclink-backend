package com.sudal.lclink.service;

import com.sudal.lclink.dto.CompanyDto;
import com.sudal.lclink.dto.LoginRequestDto;
import com.sudal.lclink.dto.LoginResponseDto;
import com.sudal.lclink.dto.SignupRequestDto;
import com.sudal.lclink.dto.UserDto;
import com.sudal.lclink.entity.Company;
import com.sudal.lclink.entity.User;
import com.sudal.lclink.repository.CompanyRepository;
import com.sudal.lclink.repository.UserRepository;
import com.sudal.lclink.sercurity.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String signup(SignupRequestDto request) {
        CompanyDto companyDto = request.getCompany();
        UserDto userDto = request.getUser();

        // 1. 유효성 검증
        if (companyDto == null) {
            throw new IllegalArgumentException("회사 정보는 필수입니다.");
        }
        if (userDto == null) {
            throw new IllegalArgumentException("사용자 정보는 필수입니다.");
        }
        if (userDto.getUserId() == null || userDto.getUserId().isBlank()) {
            throw new IllegalArgumentException("아이디는 필수입니다.");
        }
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }

        // 2. 중복 체크
        if (userRepository.findByUserId(userDto.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (companyDto.getBusinessNum() != null &&
                companyRepository.existsByBusinessNum(companyDto.getBusinessNum().trim())) {
            throw new IllegalArgumentException("이미 등록된 사업자번호입니다.");
        }

        // 3. Company 등록
        Company company = Company.builder()
                .companyName(companyDto.getCompanyName())
                .businessNum(companyDto.getBusinessNum() != null ? companyDto.getBusinessNum().trim() : null)
                .address(companyDto.getAddress())
                .companyType(companyDto.getCompanyType())
                .companyCountry(companyDto.getCompanyCountry())
                .build();
        Company savedCompany = companyRepository.save(company);

        // 4. User 등록 (비밀번호 암호화)
        User user = User.builder()
                .userId(userDto.getUserId())
                .company(savedCompany)
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .name(userDto.getName())
                .build();

        userRepository.save(user);
        return user.getUserId();
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {
        // 유효성 검증
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("아이디를 입력해주세요.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        // 사용자 조회
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.getUserId());

        // 응답 생성
        LoginResponseDto response = new LoginResponseDto();
        response.setAccessToken(token);
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setCompanyId(user.getCompany().getCompanyId());

        return response;
    }
}