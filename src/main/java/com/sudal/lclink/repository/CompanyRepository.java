package com.sudal.lclink.repository;

import com.sudal.lclink.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    boolean existsByBusinessNum(String businessNum);
    Optional<Company> findByCompanyId(Integer companyId);

    Page<Company> findByCompanyNameContainingIgnoreCaseOrBusinessNumContainingIgnoreCase(
            String name, String businessNum, Pageable pageable);
}
