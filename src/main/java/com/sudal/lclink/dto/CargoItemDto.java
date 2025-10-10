package com.sudal.lclink.dto;

import com.sudal.lclink.entity.CargoItem;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CargoItemDto {

    private Integer itemId;

    private String userId;

    private String itemName;
    private String pol;
    private String pod;

    private String incoterms;

    private String hsCode;
    private Integer quantity;

    private Integer widthCm;
    private Integer lengthCm;
    private Integer heightCm;
    private Double cbm;
    private Integer weightKg;
    private Date etd;

    private String packagingType;
    private String itemDescription;

    private String companyType;


    public static CargoItemDto from(CargoItem e) {
        long w = e.getWidthCm();
        long l = e.getLengthCm();
        long h = e.getHeightCm();
        long q = e.getQuantity();

        double m3 = (w*1.0) * l * h * q / 1000000.0;
        double cbmVal = Math.round(m3);

        return CargoItemDto.builder()
                .itemId(e.getItemId())
                .userId(e.getUser().getUserId())
                .itemName(e.getItemName())
                .pol(e.getPol())
                .pod(e.getPod())
                .incoterms(e.getIncoterms())
                .hsCode(e.getHsCode())
                .quantity((int) q)
                .widthCm((int) w)
                .lengthCm((int) l)
                .heightCm((int) h)
                .cbm(cbmVal)
                .weightKg(e.getWeightKg())
                .etd(e.getEtd())
                .packagingType(e.getPackagingType())
                .itemDescription(e.getItemDescription())
                .companyType(e.getUser()
                        .getCompany()
                        .getCompanyType())
                .build();
    }
}
