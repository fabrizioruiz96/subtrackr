package com.example.subtrackr.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlanRequest {

    @NotBlank(message="Inserisci un nome")
    private String name;

    @Size(max=1000, message="Descrizio troppo lunga, non superare i 1000 caratteri")
    private String description;

    @NotNull(message="Inserisci un prezzo")
    @DecimalMin(value="0.0", inclusive=false, message="Il prezzo non può essere negativo")
    private BigDecimal price;

    @NotNull(message="Inserisci la durata del piano")
    @Positive(message="La durata non può essere negativa")
    private Integer durationDays; 
}
