package com.github.myazusa.posthorseclouddelivery.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BindDeviceRequestDTO {
    @NotBlank
    @NotNull
    private String deviceUuid;

    @NotBlank
    @NotNull
    private String userUuid;
}
