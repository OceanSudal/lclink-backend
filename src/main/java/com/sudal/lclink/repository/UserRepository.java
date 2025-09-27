package com.sudal.lclink.repository;

import com.sudal.lclink.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);
    boolean existsByEmail(String email);
    boolean existsByCompany_CompanyId(Integer CompanyId);

    Page<User> findByCompany_CompanyId(Integer companyId, Pageable pageable);
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);
}
