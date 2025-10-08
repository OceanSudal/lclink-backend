package com.sudal.lclink.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Integer companyId;

    private String companyName;

    private String businessNum;

    private String address;

    private String companyType;

    private String companyCountry;

    @OneToMany(mappedBy = "company")
    private List<User> users = new ArrayList<>();

    @Builder
    private Company(String companyName, String businessNum, String address, String companyType, String companyCountry) {
        this.companyName = companyName;
        this.businessNum = businessNum;
        this.address = address;
        this.companyType = companyType;
        this.companyCountry = companyCountry;
    }

}
