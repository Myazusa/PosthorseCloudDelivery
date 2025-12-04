package com.github.myazusa.posthorseclouddelivery.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AlterRoleRequestDTO {
    @NotNull
    @NotBlank
    private String userUuid;

    @NotNull
    private List<String> roles;
}
