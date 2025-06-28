package com.example.subtrackr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotBlank(message="Inserisci la mail")
    @Email(message="Email non valida")
    private String email;

    @NotBlank(message="Inserisci la password")
    private String password;
}
