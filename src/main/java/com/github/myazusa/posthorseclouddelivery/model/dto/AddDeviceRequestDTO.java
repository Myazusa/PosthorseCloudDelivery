package com.github.myazusa.posthorseclouddelivery.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddDeviceRequestDTO {
    private String deviceName;
    private double latitude;
    private double longitude;

    @NotNull
    @NotBlank
    private String verifyPassword;
}
