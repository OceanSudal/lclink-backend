package com.sudal.lclink.controller;

import com.sudal.lclink.dto.ContractRequestDto;
import com.sudal.lclink.dto.ContractResponseDto;
import com.sudal.lclink.service.BlockchainService;
import com.sudal.lclink.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/blockchain/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final BlockchainService blockchainService;

    @PostMapping
    public ResponseEntity<ContractResponseDto> createContract(@RequestBody ContractRequestDto request) {
        try {

            String result = blockchainService.createContract(
                    request.getOnchainContractId(),
                    request.getShipperCompanyId(),
                    request.getForwarderCompanyId(),
                    request.getTotalPrice(),
                    request.getCurrency(),
                    request.getOriginPortCode(),
                    request.getDestinationPortCode(),
                    request.getIncotermsCode()
            );

            return ResponseEntity.ok(ContractResponseDto.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ContractResponseDto.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponseDto> getContract(@PathVariable String id) {
        try {
            String result = blockchainService.queryContract(id);
            return ResponseEntity.ok(ContractResponseDto.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ContractResponseDto.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<ContractResponseDto> contractExists(@PathVariable String id) {
        try {
            boolean exists = blockchainService.contractExists(id);
            return ResponseEntity.ok(ContractResponseDto.success(Map.of("exists", exists)));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ContractResponseDto.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ContractResponseDto> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        try {
            String result = blockchainService.updateShipmentStatus(id, request.get("newStatus"));
            return ResponseEntity.ok(ContractResponseDto.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ContractResponseDto.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ContractResponseDto> getHistory(@PathVariable String id) {
        try {
            String result = blockchainService.queryContractHistory(id);
            return ResponseEntity.ok(ContractResponseDto.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ContractResponseDto.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/documents")
    public ResponseEntity<ContractResponseDto> addDocument(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        try {
            String result = blockchainService.addDocumentHash(
                    id,
                    request.get("documentType"),
                    request.get("documentHash")
            );
            return ResponseEntity.ok(ContractResponseDto.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ContractResponseDto.error(e.getMessage()));
        }
    }
}