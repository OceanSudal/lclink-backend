package com.sudal.lclink.dto;

import com.sudal.lclink.entity.Quote;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteDto {
    private Integer quoteId;
    private Integer requestId;
    private Integer itemId;
    private String forwarderUserId;
    private BigDecimal totalPrice;
    private String currency;
    private LocalDate estimatedDepartureDate;
    private LocalDate estimatedArrivalDate;
    private LocalDate validUntil;
    private String notes;
    private String quoteStatus;
    private LocalDate createdAt;

    public static QuoteDto from(Quote quote) {
        return QuoteDto.builder()
                .quoteId(quote.getQuoteId())
                .requestId(quote.getCargoRequest().getRequestId())
                .itemId(quote.getCargoRequest().getCargoItem().getItemId())
                .forwarderUserId(quote.getForwarderUser().getUserId())
                .totalPrice(quote.getTotalPrice())
                .currency(quote.getCurrency())
                .estimatedDepartureDate(quote.getEstimatedDepartureDate())
                .estimatedArrivalDate(quote.getEstimatedArrivalDate())
                .validUntil(quote.getValidUntil())
                .notes(quote.getNotes())
                .quoteStatus(quote.getQuoteStatus())
                .createdAt(quote.getCreatedAt())
                .build();
    }
}
