package com.sudal.lclink.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Setter(AccessLevel.NONE)
    @Id
    @Column(name = "user_id")
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private String userCountry;

    private String email;

    private String password;

    private String name;

    private String userRole;

    private LocalDateTime createdAt;

    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Builder
    private User(String userId, Company company, String userCountry, String email, String password, String name, String userRole, LocalDateTime createdAt) {
        this.userId = userId;
        this.company = company;
        this.userCountry = userCountry;
        this.email = email;
        this.password = password;
        this.name = name;
        this.userRole = userRole;
        this.createdAt = createdAt;

    }
}
