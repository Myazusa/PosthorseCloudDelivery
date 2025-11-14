package com.github.myazusa.posthorseclouddelivery.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ListFilesRequestDTO {
    @NotNull
    @NotBlank
    private String fileType;

    private Integer pageNumber = 1;
    private Integer pageSize = 10;
    private String sortOrder = "desc";

    private String sortBy = "";
    private String keyword = "";
}
