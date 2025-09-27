package com.sudal.lclink.service;

import com.sudal.lclink.dto.UserDto;
import com.sudal.lclink.entity.Company;
import com.sudal.lclink.entity.User;
import com.sudal.lclink.repository.CompanyRepository;
import com.sudal.lclink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new IllegalStateException("userId는 필수입니다.");
        }
        if (userRepository.findByUserId(userDto.getUserId()).isPresent()) {
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

    // READ
    @Transactional(readOnly = true)
    public UserDto get(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId));
        return toDto(user);
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

        if (dto.getUserCountry() != null) user.setUserCountry(dto.getUserCountry());

        if (dto.getEmail() != null) {
            String email = dto.getEmail().trim().toLowerCase();
            if (!email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new IllegalStateException("이미 존재하는 이메일입니다.");
            }
            user.setEmail(email);
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(dto.getPassword());

        }
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getUserRole() != null) user.setUserRole(dto.getUserRole());

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
        dto.setUserCountry(u.getUserCountry());
        dto.setEmail(u.getEmail());
        dto.setPassword(null);
        dto.setName(u.getName());
        dto.setUserRole(u.getUserRole());
        dto.setCreatedAt(u.getCreatedAt());
        return dto;
    }
}