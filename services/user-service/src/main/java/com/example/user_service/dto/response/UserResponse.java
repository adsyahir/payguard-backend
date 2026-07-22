package com.example.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserResponse {
    private UUID uuid;
    private String email;
    private String fullName;
    private String merchant;
}
