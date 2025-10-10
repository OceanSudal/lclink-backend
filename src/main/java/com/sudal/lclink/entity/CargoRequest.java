package com.sudal.lclink.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cargo_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CargoRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    // cargo_items 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private CargoItem cargoItem;

    // shipper_company_id 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_company_id", nullable = false)
    private Company shipperCompany;

    @Column(name = "origin_port_code", nullable = false)
    private String originPortCode;

    @Column(name = "destination_port_code", nullable = false)
    private String destinationPortCode;

    @Column(name = "ready_to_load_date")
    private LocalDate readyToLoadDate;

    @Column(name = "incoterms_code")
    private String incotermsCode;

    @Column(name = "request_status")
    private String requestStatus; // OPEN, MATCHED, CANCELED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.requestStatus == null) this.requestStatus = "OPEN";
    }
}
