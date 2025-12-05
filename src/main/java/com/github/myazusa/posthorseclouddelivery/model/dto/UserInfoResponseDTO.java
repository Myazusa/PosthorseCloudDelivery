package com.github.myazusa.posthorseclouddelivery.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class UserInfoResponseDTO {
    private UUID uuid;
    private String phone;

    private String username;
    private Boolean enabled;
    private LocalDateTime createTime;
}
