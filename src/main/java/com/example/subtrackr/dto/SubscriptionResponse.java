package com.example.subtrackr.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private Integer id;
    private Integer planId;
    private String planName;
    private LocalDateTime startAt;
    private LocalDateTime expiresAt;
    private String status;
}
