package com.sudal.lclink.controller;

import com.sudal.lclink.dto.UserDto;
import com.sudal.lclink.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // CREATE
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String create(
            @RequestParam("userId") String userId,
            @RequestParam("companyId") Integer companyId,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "certificate", required = false) MultipartFile certificate) {

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setCompanyId(companyId);
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setName(name);
        userDto.setCertificateFile(certificate);

        return userService.register(userDto);
    }

    // READ
    @GetMapping("/{userId}")
    public UserDto get(@PathVariable("userId") String userId) {
        return userService.get(userId);
    }

    // PDF 다운로드
    @GetMapping("/{userId}/certificate")
    public ResponseEntity<byte[]> getCertificate(@PathVariable("userId") String userId) {
        UserDto user = userService.get(userId);

        if (!user.getHasCertificate()) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfData = userService.getCertificate(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", user.getCertificateFilename());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }

    // READ (List)
    @GetMapping
    public Page<UserDto> list (@RequestParam(required = false) Integer companyId,
                               @RequestParam(required = false, name = "q") String keyword,
                               @PageableDefault(size = 10, sort = "userId") Pageable pageable) {
        return userService.list(companyId, keyword, pageable);
    }

    // UPDATE
    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDto update(
            @PathVariable("userId") String userId,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "certificate", required = false) MultipartFile certificate) {

        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setName(name);
        userDto.setCertificateFile(certificate);

        return userService.update(userId, userDto);
    }

    // DELETE
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") String userId) {
        userService.delete(userId);
    }
}
