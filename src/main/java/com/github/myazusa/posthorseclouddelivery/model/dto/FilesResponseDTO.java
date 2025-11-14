package com.github.myazusa.posthorseclouddelivery.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class FilesResponseDTO {
    private UUID uuid;
    private String fileName;
    private String filetype;
    private Long fileSize;
    private Integer duration;
    private Integer priority;
    private LocalDateTime createdAt;
}
