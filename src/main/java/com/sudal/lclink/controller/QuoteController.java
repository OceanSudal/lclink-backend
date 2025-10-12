package com.sudal.lclink.controller;

import com.sudal.lclink.dto.CargoItemDto;
import com.sudal.lclink.dto.QuoteDto;
import com.sudal.lclink.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<QuoteDto> create(@RequestBody QuoteDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quoteService.createQuote(dto));
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<QuoteDto>> getByRequest(@PathVariable Integer requestId) {
        return ResponseEntity.ok(quoteService.listByRequestId(requestId));
    }

    @GetMapping("/{quoteId}")
    public ResponseEntity<List<QuoteDto>> getByQuote(@PathVariable Integer quoteId) {
        return ResponseEntity.ok(quoteService.listByQuoteId(quoteId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuoteDto>> getByForwarderUserId(@PathVariable String userId) {
        return ResponseEntity.ok(quoteService.listByForwarderUserId(userId));
    }

    // UPDATE
    @PutMapping("/{quoteId}")
    public QuoteDto update(
            @PathVariable Integer quoteId,
            @RequestBody QuoteDto req
    ) {
        return quoteService.update(quoteId, req);
    }

    @DeleteMapping("/{quoteId}")
    public ResponseEntity<Void> delete(@PathVariable Integer quoteId) {
        quoteService.delete(quoteId);
        return ResponseEntity.noContent().build();
    }
}
