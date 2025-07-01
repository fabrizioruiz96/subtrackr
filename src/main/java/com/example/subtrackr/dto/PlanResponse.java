package com.example.subtrackr.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanResponse {

    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationDays;
    private LocalDateTime createdAt;
}
