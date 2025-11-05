package com.github.myazusa.posthorseclouddelivery.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserVerificationCodeRequestDTO {
    @NotBlank
    @NotNull
    @Pattern(regexp = "^\\+?[0-9]{6,20}$", message = "手机号格式错误")
    String username;

    String code;
}
