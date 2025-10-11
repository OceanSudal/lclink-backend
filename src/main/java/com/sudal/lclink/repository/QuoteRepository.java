package com.sudal.lclink.repository;

import com.sudal.lclink.entity.CargoRequest;
import com.sudal.lclink.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Integer> {
    List<Quote> findByCargoRequest_RequestId(Integer requestId);
    Optional<Quote> findByQuoteId(Integer quoteId);

}
