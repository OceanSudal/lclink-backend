package com.sudal.lclink.service;

import com.sudal.lclink.dto.UserDto;
import com.sudal.lclink.entity.Company;
import com.sudal.lclink.entity.User;
import com.sudal.lclink.exception.AlreadyExistElementException;
import com.sudal.lclink.repository.CompanyRepository;
import com.sudal.lclink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    // CREATE
    public String register(UserDto userDto) {
        if (userDto.getUserId() == null || userDto.getUserId().isBlank()){
            throw new IllegalArgumentException("userId는 필수입니다."); //400
        }
        if (userRepository.findByUserId(userDto.getUserId()).isPresent()) {
            throw new AlreadyExistElementException("이미 존재하는 ID입니다."); //409
        }
        if (userDto.getCompanyId() == null) {
            throw new IllegalArgumentException("companyId는 필수입니다."); //400
        }

        Company company = companyRepository.findByCompanyId(userDto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 company입니다."));

        byte[] certificateData = null;
        String filename = null;
        String contentType = null;

        if (userDto.getCertificateFile() != null && !userDto.getCertificateFile().isEmpty()) {
            try {
                certificateData = userDto.getCertificateFile().getBytes();
                filename = userDto.getCertificateFile().getOriginalFilename();
                contentType = userDto.getCertificateFile().getContentType();

                // PDF 파일 검증
                if (!contentType.equals("application/pdf")) {
                    throw new IllegalArgumentException("PDF 파일만 업로드 가능합니다.");
                }
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
            }
        }

        User user = User.builder()
                .userId(userDto.getUserId())
                .company(company)
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .name(userDto.getName())
                .createdAt(userDto.getCreatedAt())
                .certificate(certificateData)
                .certificateFilename(filename)
                .certificateContentType(contentType)
                .build();

        User saved = userRepository.save(user);
        return saved.getUserId();
    }

    // READ
    @Transactional(readOnly = true)
    public UserDto get(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId));
        return toDto(user);
    }

    // PDF 다운로드
    @Transactional(readOnly = true)
    public byte[] getCertificate(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId));

        if (user.getCertificate() == null) {
            throw new IllegalArgumentException("인증서가 존재하지 않습니다.");
        }

        return user.getCertificate();
    }

    // READ - List
    @Transactional(readOnly = true)
    public Page<UserDto> list(Integer companyId, String keyword, Pageable pageable) {
        Page<User> page;
        if (companyId != null) {
            page = userRepository.findByCompany_CompanyId(companyId, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            page = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }
        return page.map(this::toDto);
    }

    // UPDATE
    public UserDto update(String userId, UserDto dto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId));

        if (dto.getEmail() != null) {
            String email = dto.getEmail().trim().toLowerCase();
            if (!email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
            }
            user.setEmail(email);
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(dto.getPassword());

        }
        if (dto.getName() != null) user.setName(dto.getName());

        if (dto.getCertificateFile() != null && !dto.getCertificateFile().isEmpty()) {
            try {
                String contentType = dto.getCertificateFile().getContentType();
                if (!contentType.equals("application/pdf")) {
                    throw new IllegalArgumentException("PDF 파일만 업로드 가능합니다.");
                }

                user.setCertificate(dto.getCertificateFile().getBytes());
                user.setCertificateFilename(dto.getCertificateFile().getOriginalFilename());
                user.setCertificateContentType(contentType);
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
            }
        }

        return toDto(user);
    }

    // DELETE
    public void delete(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId));
        userRepository.delete(user);
    }

    private UserDto toDto(User u) {
        UserDto dto = new UserDto();
        dto.setUserId(u.getUserId());
        dto.setCompanyId(u.getCompany().getCompanyId());
        dto.setEmail(u.getEmail());
        dto.setPassword(null);
        dto.setName(u.getName());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setCertificateFilename(u.getCertificateFilename());
        dto.setHasCertificate(u.getCertificate() != null);
        return dto;
    }
}