package com.github.myazusa.posthorseclouddelivery.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class InformationResponseDTO {
    private String state;
    private String message;
    private Object data;
}
