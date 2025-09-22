package com.sudal.lclink.controller;

import com.sudal.lclink.dto.UserDto;
import com.sudal.lclink.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody UserDto userDto) {
        return userService.register(userDto);
    }
}
