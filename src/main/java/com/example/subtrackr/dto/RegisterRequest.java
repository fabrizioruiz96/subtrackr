package com.example.subtrackr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message="Il nome è obbligatorio")
    private String name;

    @NotBlank(message="La mail è obbligatoria")
    @Email(message="Inserisci una mail valida")
    private String email;
    
    @NotBlank(message="La password è obbligatoria")
    @Size(min=6, message="La password deve essere lunga almeno 6 caratteri")
    private String password;
}
