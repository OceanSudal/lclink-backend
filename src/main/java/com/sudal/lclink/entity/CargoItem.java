package com.sudal.lclink.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Table(name = "cargo_items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CargoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
    private Integer widthCm;

    @Column(nullable = false)
    private Integer lengthCm;

    @Column(nullable = false)
    private Integer heightCm;

    private Double cbm;

    @Column(nullable = false)
    private Integer weightKg;

    @Column(nullable = false)
    private Date etd;

    private String packagingType;
    private String itemDescription;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "request_id")
//    private CargoRequest cargoRequest;

    public static CargoItem create(
            User user,
            String itemName,
            String pol,
            String pod,
            String hsCode,
            Integer quantity,
            Integer widthCm,
            Integer lengthCm,
            Integer heightCm,
            Integer weightKg,
            Date etd,
            String packagingType,
            String itemDescription
    ){
        CargoItem e = new CargoItem();
        e.setUser(user);
        e.setItemName(itemName);
        e.setPol(pol);
        e.setPod(pod);
        e.setHsCode(hsCode);
        e.setQuantity(quantity);
        e.setWidthCm(widthCm);
        e.setLengthCm(lengthCm);
        e.setHeightCm(heightCm);
        e.setWeightKg(weightKg);
        e.setEtd(etd);
        e.setPackagingType(packagingType);
        e.setItemDescription(itemDescription);
        return e;
    }

    public void updateDetails(
            String itemName,
            String pol,
            String pod,
            String hsCode,
            Integer quantity,
            Integer widthCm,
            Integer lengthCm,
            Integer heightCm,
            Integer weightKg,
            Date etd,
            String packagingType,
            String itemDescription
    ) {
        setItemName(itemName);
        setPol(pol);
        setPod(pod);
        setHsCode(hsCode);
        setQuantity(quantity);
        setWidthCm(widthCm);
        setLengthCm(lengthCm);
        setHeightCm(heightCm);
        setWeightKg(weightKg);
        setEtd(etd);
        setPackagingType(packagingType);
        setItemDescription(itemDescription);
    }

}