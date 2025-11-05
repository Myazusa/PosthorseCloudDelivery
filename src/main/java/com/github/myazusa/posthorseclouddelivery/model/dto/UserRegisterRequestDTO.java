package com.github.myazusa.posthorseclouddelivery.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegisterRequestDTO {
    @NotBlank
    @NotNull
    String username;

    @NotBlank
    @NotNull
    String password;
}
