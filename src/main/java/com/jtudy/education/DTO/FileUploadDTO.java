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

    private String filePath;

    private String fileSize;

    private String uploaderName;

    private String uploaderEmail;

}