package com.auth.controller;

import com.auth.dto.APIResponse;
import com.auth.dto.LoginDto;
import com.auth.dto.UpdatePasswordDto;
import com.auth.dto.UserDto;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import com.auth.service.AuthService;
import com.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<String>> register(@RequestBody UserDto dto) {
        APIResponse<String> response = authService.register(dto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @PutMapping("/update-password")
    public ResponseEntity<APIResponse<String>> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto){
        APIResponse<String> response = authService.setNewPassword(updatePasswordDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }


    @PostMapping("/login")
    public ResponseEntity<APIResponse<String>> loginCheck(@RequestBody LoginDto loginDto) {

        APIResponse<String> response = new APIResponse<>();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                );

        try {
            Authentication authentication = authManager.authenticate(authToken);

            if (authentication.isAuthenticated()) {

                // ✅ Fetch user from DB
                User user = userRepository.findByUsername(loginDto.getUsername());

                // 🔑 Generate JWT with ENUM role
                String jwtToken = jwtService.generateToken(
                        user.getUsername(),
                        user.getRole().name()
                );

                response.setMessage("Login Successful");
                response.setStatus(200);
                response.setData(jwtToken);

                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setMessage("Failed");
        response.setStatus(401);
        response.setData("Unauthorized Access");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping("/get-user")
    public User getUser(@RequestParam String username) {
        return userRepository.findByUsername(username);
    }

}