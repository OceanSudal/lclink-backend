package com.sudal.lclink.service;

import com.sudal.lclink.dto.CargoRequestDto;
import com.sudal.lclink.dto.QuoteDto;
import com.sudal.lclink.entity.CargoRequest;
import com.sudal.lclink.entity.Company;
import com.sudal.lclink.entity.Quote;
import com.sudal.lclink.entity.User;
import com.sudal.lclink.repository.CargoRequestRepository;
import com.sudal.lclink.repository.CompanyRepository;
import com.sudal.lclink.repository.QuoteRepository;
import com.sudal.lclink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final CargoRequestRepository cargoRequestRepository;
    private final UserRepository userRepository;

    // CREATE
    public QuoteDto createQuote(QuoteDto dto) {
        CargoRequest request = cargoRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        User forwarder = userRepository.findById(dto.getForwarderUserId())
                .orElseThrow(() -> new IllegalArgumentException("포워더 회사를 찾을 수 없습니다."));

        Quote entity = Quote.builder()
                .cargoRequest(request)
                .forwarderUser(forwarder)
                .totalPrice(dto.getTotalPrice())
                .currency(dto.getCurrency())
                .estimatedDepartureDate(dto.getEstimatedDepartureDate())
                .estimatedArrivalDate(dto.getEstimatedArrivalDate())
                .validUntil(dto.getValidUntil())
                .notes(dto.getNotes())
                .quoteStatus(dto.getQuoteStatus())
                .build();

        Quote saved = quoteRepository.save(entity);
        return QuoteDto.from(saved);
    }

    // READ (by requestId)
    @Transactional(readOnly = true)
    public List<QuoteDto> listByRequestId(Integer requestId) {
        return quoteRepository.findByCargoRequest_RequestId(requestId)
                .stream()
                .map(QuoteDto::from)
                .collect(Collectors.toList());
    }

    // READ (by quoteId)
    @Transactional(readOnly = true)
    public List<QuoteDto> listByQuoteId(Integer quoteId) {
        return quoteRepository.findByQuoteId(quoteId)
                .stream()
                .map(QuoteDto::from)
                .collect(Collectors.toList());
    }

    // READ (by userId)
    @Transactional(readOnly = true)
    public List<QuoteDto> listByForwarderUserId(String userId) {
        return quoteRepository.findByForwarderUser_UserId(userId)
                .stream()
                .map(QuoteDto::from)
                .collect(Collectors.toList());
    }

    // UPDATE
    public QuoteDto update(Integer quoteId, QuoteDto dto) {
        Quote q = quoteRepository.findByQuoteId(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 quoteId입니다: " + quoteId));


        if (dto.getTotalPrice() != null) q.setTotalPrice(dto.getTotalPrice());
        if (dto.getCurrency() != null) q.setCurrency(dto.getCurrency());
        if (dto.getEstimatedDepartureDate() != null) q.setEstimatedDepartureDate(dto.getEstimatedDepartureDate());
        if (dto.getEstimatedArrivalDate() != null) q.setEstimatedArrivalDate(dto.getEstimatedArrivalDate());
        if (dto.getValidUntil() != null) q.setValidUntil(dto.getValidUntil());
        if (dto.getNotes() != null) q.setNotes(dto.getNotes());
        if (dto.getQuoteStatus() != null) q.setQuoteStatus(dto.getQuoteStatus());

        Quote updated = quoteRepository.save(q);
        return QuoteDto.from(updated);
    }

    // DELETE
    public void delete(Integer quoteId) {
        quoteRepository.deleteById(quoteId);
    }

    private QuoteDto toDto(Quote q) {
        QuoteDto dto = new QuoteDto();
        dto.setTotalPrice(q.getTotalPrice());
        dto.setCurrency(q.getCurrency());
        dto.setEstimatedDepartureDate(q.getEstimatedDepartureDate());
        dto.setEstimatedArrivalDate(q.getEstimatedArrivalDate());
        dto.setValidUntil(q.getValidUntil());
        dto.setNotes(q.getNotes());
        dto.setQuoteStatus(q.getQuoteStatus());

        return dto;
    }
}
