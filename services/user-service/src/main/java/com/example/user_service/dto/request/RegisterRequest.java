package com.example.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String companyName;
    @NotBlank
    private String country;
    @NotBlank
    private String fullName;
    @Email
    private String email;
    @Size(min = 8)
    private String password;
}
