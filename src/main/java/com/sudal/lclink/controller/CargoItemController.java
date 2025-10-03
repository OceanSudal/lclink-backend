package com.sudal.lclink.controller;

import com.sudal.lclink.dto.CargoItemDto;
import com.sudal.lclink.service.CargoItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cargo-items")
@RequiredArgsConstructor
public class CargoItemController {
    private final CargoItemService cargoItemService;

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CargoItemDto register(@RequestBody CargoItemDto req) {
        return cargoItemService.register(req);
    }

    // READ (by id)
    @GetMapping("/{itemId}")
    public CargoItemDto read(@PathVariable Integer itemId) {
        return cargoItemService.getCargoItem(itemId);
    }

    // LIST
    @GetMapping
    public Page<CargoItemDto> list(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return cargoItemService.list(userId, page, size);
    }

    // UPDATE
    @PutMapping("/{itemId}")
    public CargoItemDto update(
            @PathVariable Integer itemId,
            @RequestBody CargoItemDto req
    ) {
        return cargoItemService.update(itemId, req);
    }

    // DELETE
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer itemId) {
        cargoItemService.delete(itemId);
    }
}
