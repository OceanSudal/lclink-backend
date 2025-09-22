package com.sudal.lclink.service;

import com.sudal.lclink.dto.UserDto;
import com.sudal.lclink.entity.Company;
import com.sudal.lclink.entity.User;
import com.sudal.lclink.repository.CompanyRepository;
import com.sudal.lclink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public String register(UserDto userDto) {
        Optional<User> findUser = userRepository.findByUserId(userDto.getUserId());

        if (userDto.getUserId() != null && findUser.isPresent()){
            throw new IllegalStateException("이미 존재하는 ID입니다.");
        }

        if (userDto.getCompanyId() == null) {
            throw new IllegalArgumentException("companyId는 필수입니다.");
        }

        Company company = companyRepository.findByCompanyId(userDto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 company입니다."));

        User user = User.builder()
                .userId(userDto.getUserId())
                .company(company)
                .userCountry(userDto.getUserCountry())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .name(userDto.getName())
                .userRole(userDto.getUserRole())
                .createdAt(userDto.getCreatedAt())
                .build();

        User saved = userRepository.save(user);
        return saved.getUserId();

    }

}




//package com.sudal.lclink.service;
//
//import com.sudal.lclink.dto.UserDto;
//import com.sudal.lclink.entity.Company;
//import com.sudal.lclink.entity.User;
//import com.sudal.lclink.exception.AlreadyExistElementException;
//import com.sudal.lclink.repository.CompanyRepository;
//import com.sudal.lclink.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class UserService {
//    private final UserRepository userRepository;
//    private final CompanyRepository companyRepository;
//    private final PasswordEncoder passwordEncoder; // ⬅︎ Security 설정에 등록돼 있어야 함
//
//    public Integer register(UserDto userDto) {     // ⬅︎ 저장된 userId 반환이 자연스러움
//        // (1) companyId 필수
//        if (userDto.getCompanyId() == null) {
//            throw new IllegalArgumentException("companyId는 필수입니다.");
//        }
//
//        // (2) 회사 존재 확인 (즉시 검증)
//        Company company = companyRepository.findById(userDto.getCompanyId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 companyId입니다: " + userDto.getCompanyId()));
//
//        // (3) 이메일 정규화 + 중복 검사
//        String email = userDto.getEmail() == null ? null : userDto.getEmail().trim().toLowerCase();
//        if (email == null || email.isBlank()) {
//            throw new IllegalArgumentException("email은 필수입니다.");
//        }
//        if (userRepository.existsByEmail(email)) {
//            throw new AlreadyExistElementException("이미 등록된 이메일입니다: " + email);
//        }
//
//        // (4) 수동 PK 전략을 쓰는 경우에만 활성화
//        // if (userDto.getUserId() == null) {
//        //     throw new IllegalArgumentException("userId는 필수입니다. (수동 PK 사용 중)");
//        // }
//        // if (userRepository.findByUserId(userDto.getUserId()).isPresent()) {
//        //     throw new AlreadyExistElementException("이미 존재하는 userId입니다: " + userDto.getUserId());
//        // }
//
//        // (5) User 생성 (createdAt은 @PrePersist에서 자동 세팅 권장)
//        User user = User.builder()
//                // .userId(userDto.getUserId())           // ⬅︎ 자동 생성이면 제거
//                .company(company)
//                .userCountry(userDto.getUserCountry())
//                .email(email)
//                .password(passwordEncoder.encode(userDto.getPassword()))
//                .name(userDto.getName())
//                .userRole(userDto.getUserRole())
//                // .createdAt(userDto.getCreatedAt())     // ⬅︎ 엔티티 @PrePersist가 더 안전
//                .build();
//
//        return userRepository.save(user).getUserId();
//    }
//}
