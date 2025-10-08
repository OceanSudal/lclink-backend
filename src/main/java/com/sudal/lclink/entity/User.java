package com.sudal.lclink.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String email;

    private String password;

    private String name;

    private LocalDateTime createdAt;

    @Lob
    @Column(name = "certificate", columnDefinition = "LONGBLOB")
    private byte[] certificate;

    @Column(name = "certificate_filename")
    private String certificateFilename;

    @Column(name = "certificate_content_type")
    private String certificateContentType;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @OneToMany(mappedBy = "user")
    private List<CargoItem> cargoItems = new ArrayList<>();

    @Builder
    private User(String userId, Company company, String email, String password, String name, LocalDateTime createdAt, byte[] certificate, String certificateFilename, String certificateContentType) {
        this.userId = userId;
        this.company = company;
        this.email = email;
        this.password = password;
        this.name = name;
        this.createdAt = createdAt;
        this.certificate = certificate;
        this.certificateFilename = certificateFilename;
        this.certificateContentType = certificateContentType;
    }
}
