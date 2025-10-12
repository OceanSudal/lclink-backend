package com.sudal.lclink.controller;

import com.sudal.lclink.dto.CargoItemDto;
import com.sudal.lclink.dto.CargoRequestDto;
import com.sudal.lclink.entity.CargoRequest;
import com.sudal.lclink.service.CargoRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargo-requests")
@RequiredArgsConstructor
public class CargoRequestController {

    private final CargoRequestService cargoRequestService;

    @PostMapping
    public ResponseEntity<CargoRequestDto> register(@RequestBody CargoRequestDto dto) {
        return ResponseEntity.ok(cargoRequestService.register(dto));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<CargoRequestDto> get(@PathVariable Integer requestId) {
        return ResponseEntity.ok(cargoRequestService.get(requestId));
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<List<CargoRequestDto>> getUserRequest(@PathVariable String userId) {
        return ResponseEntity.ok(cargoRequestService.getUserRequest(userId));
    }

    @GetMapping
    public ResponseEntity<List<CargoRequestDto>> list() {
        return ResponseEntity.ok(cargoRequestService.list());
    }

    // UPDATE
    @PutMapping("/{requestId}")
    public CargoRequestDto update(
            @PathVariable Integer requestId,
            @RequestBody CargoRequestDto req
    ) {
        return cargoRequestService.update(requestId, req);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> delete(@PathVariable Integer requestId) {
        cargoRequestService.delete(requestId);
        return ResponseEntity.noContent().build();
    }
}
