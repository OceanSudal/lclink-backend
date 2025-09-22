package com.sudal.lclink.controller;

import com.sudal.lclink.dto.CargoItemDto;
import com.sudal.lclink.service.CargoItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cargoItem")
@RequiredArgsConstructor
public class CargoItemController {
    private final CargoItemService cargoItemService;

    @PostMapping("/register")
    public String register(@RequestBody CargoItemDto cargoItemDto) {
        return cargoItemService.register(cargoItemDto);
    }
}
