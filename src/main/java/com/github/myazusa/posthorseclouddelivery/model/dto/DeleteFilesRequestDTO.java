package com.github.myazusa.posthorseclouddelivery.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DeleteFilesRequestDTO {
    @NotNull
    @NotBlank
    private String fileType;

    @NotNull
    private List<String> uuidList;
}
