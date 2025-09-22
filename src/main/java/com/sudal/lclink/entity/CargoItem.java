package com.sudal.lclink.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "cargo_items")
@Getter
@Setter
public class CargoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private String pol;

    @Column(nullable = false)
    private String pod;

    //Incoterms

    @Column(nullable = false)
    private String hsCode;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer width_cm;

    @Column(nullable = false)
    private Integer length_cm;

    @Column(nullable = false)
    private Integer height_cm;

    private Integer cbm;

    @Column(nullable = false)
    private Integer weight_kg;

    @Column(nullable = false)
    private Date etd;

    private String packagingType;
    private String itemDescription;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "request_id")
//    private CargoRequest cargoRequest;

    @Builder
    private CargoItem(Integer itemId, String itemName, String pol, String pod, String hsCode, Integer quantity, Integer width_cm, Integer length_cm, Integer height_cm, Integer cbm, Integer weight_kg, Date etd){
        this.itemId = itemId;
        this.itemName = itemName;
        this.pol = pol;
        this.pod = pod;
        this.hsCode = hsCode;
        this.quantity = quantity;
        this.width_cm = width_cm;
        this.length_cm = length_cm;
        this.height_cm = height_cm;
        this.cbm = width_cm*length_cm*height_cm;
        this.weight_kg = weight_kg;
        this.etd = etd;
    }
}