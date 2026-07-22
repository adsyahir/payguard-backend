package com.example.user_service.controller;

import com.example.user_service.dto.request.RegisterRequest;
import com.example.user_service.dto.response.UserResponse;
import com.example.user_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

        UserResponse userResponse = authService.register(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
