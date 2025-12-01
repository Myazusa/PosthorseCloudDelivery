package com.github.myazusa.posthorseclouddelivery.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class VerificationPasswordResponseDTO {
    private String verifyPassword;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;
}
