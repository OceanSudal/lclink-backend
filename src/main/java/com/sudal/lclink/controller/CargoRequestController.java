package com.sudal.lclink.controller;

import com.sudal.lclink.dto.CargoRequestDto;
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

    @GetMapping("/{id}")
    public ResponseEntity<CargoRequestDto> get(@PathVariable Integer id) {
        return ResponseEntity.ok(cargoRequestService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<CargoRequestDto>> list() {
        return ResponseEntity.ok(cargoRequestService.list());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cargoRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
