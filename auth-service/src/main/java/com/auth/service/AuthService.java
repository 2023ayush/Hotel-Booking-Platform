package com.auth.service;

import com.auth.dto.APIResponse;
import com.auth.dto.LoginDto;
import com.auth.dto.UpdatePasswordDto;
import com.auth.dto.UserDto;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exception.InvalidRequestException;
import com.auth.exception.ResourceNotFoundException;
import com.auth.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    // ----------------- Register User -----------------
    public APIResponse<String> register(UserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new InvalidRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new InvalidRequestException("Email already exists");
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.ROLE_USER); // Default role

        userRepository.save(user);

        return new APIResponse<>(201, "Registration Successful", "User registered successfully");
    }

    // ----------------- Update Password -----------------
    public APIResponse<String> setNewPassword(UpdatePasswordDto dto) {
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null || !user.getUsername().equals(dto.getUsername())) {
            throw new ResourceNotFoundException("User not found");
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return new APIResponse<>(200, "Password updated successfully", "Password updated successfully");
    }

    // ----------------- Login -----------------
    public APIResponse<String> login(LoginDto loginDto, Role expectedRole) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );

            User user = userRepository.findByUsername(loginDto.getUsername());
            if (user == null) {
                throw new ResourceNotFoundException("User not found");
            }

            if (user.getRole() != expectedRole) {
                throw new InvalidRequestException("User does not have required role");
            }

            String jwt = jwtService.generateToken(user.getUsername(), user.getRole().name());
            return new APIResponse<>(200, expectedRole.name() + " Login Successful", jwt);

        } catch (ResourceNotFoundException | InvalidRequestException e) {
            throw e; // Forward to global handler
        } catch (AuthenticationException e) {
            throw new InvalidRequestException("Invalid username or password");
        }
    }
}
