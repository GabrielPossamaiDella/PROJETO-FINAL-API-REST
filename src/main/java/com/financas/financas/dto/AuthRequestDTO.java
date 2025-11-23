package com.financas.financas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AuthRequestDTO {
    
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}