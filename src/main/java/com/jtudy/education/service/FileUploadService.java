package com.jtudy.education.service;

import com.jtudy.education.DTO.FileUploadDTO;
import com.jtudy.education.entity.FileUpload;
import com.jtudy.education.entity.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public interface FileUploadService {

    Long uploadFile(FileUpload file) throws IOException;

    void isValidName(String name);

    FileUpload fileToEntity(MultipartFile file, Member member) throws IOException;

    default FileUploadDTO entityToDTO(FileUpload file) {
        FileUploadDTO fileUploadDTO = FileUploadDTO.builder()
                .originalName(file.getOriginalName())
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .filePath(Paths.get(file.getFilePath()))
                .build();

        return fileUploadDTO;
    }

}
