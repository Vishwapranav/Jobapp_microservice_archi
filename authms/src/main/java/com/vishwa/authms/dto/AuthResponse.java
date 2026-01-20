package com.vishwa.authms.dto;

import com.vishwa.authms.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private UserRole role;
    private String email;
}
