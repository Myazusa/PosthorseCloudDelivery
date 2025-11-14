package com.github.myazusa.posthorseclouddelivery.model.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserAuthRequestDTO {
    @NotNull
    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{6,20}$", message = "手机号格式错误")
    private String phone;

    @NotNull
    @NotBlank
    private String password;
}
