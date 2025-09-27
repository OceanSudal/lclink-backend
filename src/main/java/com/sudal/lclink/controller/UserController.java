package com.sudal.lclink.controller;

import com.sudal.lclink.dto.UserDto;
import com.sudal.lclink.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody UserDto userDto) {
        return userService.register(userDto);
    }

    // READ
    @GetMapping("/{userId}")
    public UserDto get(@PathVariable("userId") String userId) {
        return userService.get(userId);
    }

    // READ (List)
    @GetMapping
    public Page<UserDto> list (@RequestParam(required = false) Integer companyId,
                               @RequestParam(required = false, name = "q") String keyword,
                               @PageableDefault(size = 10, sort = "userId") Pageable pageable) {
        return userService.list(companyId, keyword, pageable);
    }

    // UPDATE
    @PutMapping("/{userId}")
    public UserDto update(@PathVariable("userId") String userId, @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    // DELETE
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") String userId) {
        userService.delete(userId);
    }
}
