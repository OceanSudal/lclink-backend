package com.sudal.lclink.dto;

import com.sudal.lclink.entity.CargoItem;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class CargoItemDto {
    private Integer itemId;

    private String itemName;
    private String pol;
    private String pod;

    //Incoterms

    private String hsCode;
    private Integer quantity;

    private Integer width_cm;
    private Integer length_cm;
    private Integer height_cm;
    private Integer cbm;
    private Integer weight_kg;
    private Date etd;

    private String packagingType;
    private String itemDescription;

}
