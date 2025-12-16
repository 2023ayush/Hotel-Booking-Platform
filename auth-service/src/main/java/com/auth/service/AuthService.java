package com.auth.service;


import com.auth.dto.APIResponse;
import com.auth.dto.UpdatePasswordDto;
import com.auth.dto.UserDto;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public APIResponse<String> register(UserDto dto) {

        if(userRepository.existsByUsername(dto.getUsername())) {
            APIResponse<String> response = new APIResponse<>();
            response.setMessage("Registration Failed");
            response.setStatus(500);
            response.setData("User with username exists");
            return response;
        }
        if(userRepository.existsByEmail(dto.getEmail())) {
            APIResponse<String> response = new APIResponse<>();
            response.setMessage("Registration Failed");
            response.setStatus(500);
            response.setData("User with Email Id exists");
            return response;
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));


       // 🔥 SET DEFAULT ROLE HERE
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);

        APIResponse<String> response = new APIResponse<>();
        response.setMessage("Registration Done");
        response.setStatus(201);
        response.setData("User is registered");

        return response;


    }


    public APIResponse<String> setNewPassword(UpdatePasswordDto updatePasswordDto) {
        if(!userRepository.existsByUsername(updatePasswordDto.getUsername())) {
            APIResponse<String> response = new APIResponse<>();
            response.setMessage("Failed");
            response.setStatus(500);
            response.setData("User with username doesnot exists");
            return response;
        }
        if(!userRepository.existsByEmail(updatePasswordDto.getEmail())) {
            APIResponse<String> response = new APIResponse<>();
            response.setMessage("Failed");
            response.setStatus(500);
            response.setData("User with Email Id does not exists");
            return response;
        }

        User user = userRepository.findByEmail(updatePasswordDto.getEmail());

        if(BCrypt.checkpw(updatePasswordDto.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));

            userRepository.save(user);
            APIResponse<String> response = new APIResponse<>();
            response.setMessage("Done");
            response.setStatus(200);
            response.setData("User password is updated");
            return response;
        }

        return null;


    }

}