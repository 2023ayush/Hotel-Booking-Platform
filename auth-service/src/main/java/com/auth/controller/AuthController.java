package com.auth.controller;

import com.auth.dto.APIResponse;
import com.auth.dto.LoginDto;
import com.auth.dto.UpdatePasswordDto;
import com.auth.dto.UserDto;
import com.auth.entity.Role;
import com.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<String>> register(@RequestBody UserDto dto) {
        APIResponse<String> response = authService.register(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/update-password")
    public ResponseEntity<APIResponse<String>> updatePassword(@RequestBody UpdatePasswordDto dto) {
        APIResponse<String> response = authService.setNewPassword(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login/admin")
    public ResponseEntity<APIResponse<String>> loginAdmin(@RequestBody LoginDto loginDto) {
        APIResponse<String> response = authService.login(loginDto, Role.ROLE_ADMIN);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login/user")
    public ResponseEntity<APIResponse<String>> loginUser(@RequestBody LoginDto loginDto) {
        APIResponse<String> response = authService.login(loginDto, Role.ROLE_USER);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
