package com.github.myazusa.posthorseclouddelivery.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFilesRequestDTO {
    @NotNull
    @NotBlank
    private String fileType;

    @NotNull
    private List<MultipartFile> files;
}
