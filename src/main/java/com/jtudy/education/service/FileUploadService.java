package com.jtudy.education.service;

import com.jtudy.education.DTO.FileUploadDTO;
import com.jtudy.education.entity.FileUpload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public interface FileUploadService {

    Long uploadFile(FileUploadDTO fileDTO) throws IOException;

    void isValidName(String name);

    FileUploadDTO fileToDTO(MultipartFile file, Map<String, Long> entity) throws IOException;

    default FileUpload DTOtoEntity(FileUploadDTO fileDTO) {
        FileUpload fileUpload = FileUpload.builder()
                .fileId(fileDTO.getFileId())
                .fileName(fileDTO.getFileName())
                .fileType(fileDTO.getFileType())
                .build();
        return fileUpload;
    }

}
