package com.jtudy.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadDTO {

    private Long fileId;

    private String originalName;

    private String fileName;

    private String fileType;

    private Path uploadPath;

    private Path filePath;

    private byte[] fileData;

}
