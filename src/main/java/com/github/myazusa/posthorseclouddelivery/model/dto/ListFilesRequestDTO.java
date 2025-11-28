package com.github.myazusa.posthorseclouddelivery.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ListFilesRequestDTO {
    @NotNull
    @NotBlank
    private String fileType;

    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNumber = 1;

    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10;

    private String sortOrder = "desc";

    private String sortBy = "created_at";
    private String keyword = "";
}
