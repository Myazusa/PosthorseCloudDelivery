package com.github.myazusa.posthorseclouddelivery.model.dto;

import lombok.Data;

@Data
public class UserUuidRequestDTO {
    private String userUuid;
    private Boolean isQueryOthers;
}
