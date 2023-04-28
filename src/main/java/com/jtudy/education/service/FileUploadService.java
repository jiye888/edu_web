package com.jtudy.education.service;

import com.jtudy.education.DTO.FileUploadDTO;
import com.jtudy.education.entity.FileUpload;
import com.jtudy.education.entity.Member;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.security.core.parameters.P;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

public interface FileUploadService {

    void isValidName(String name) throws InvalidFileNameException;

    boolean isValidExtension(String extension) throws IOException;

    FileUpload fileToEntity(MultipartFile file, Member member) throws IOException;

    File getFile(FileUpload fileUpload) throws FileNotFoundException;

    Double getFileSize(FileUpload fileUpload) throws IOException;

    Long uploadFile(FileUpload fileUpload, MultipartFile file) throws IOException;

    default FileUploadDTO entityToDTO(FileUpload fileUpload) throws IOException {
        Double fileSize = getFileSize(fileUpload);
        String[] indexUnit = {"B", "KB", "MB", "GB"};
        String sizeString = "";
        DecimalFormat format = new DecimalFormat("#.##");

        for (int i=0; i<indexUnit.length; i++) {
            if ((fileSize / Math.pow(1024, i)) > 1.0) {
                String formattedSize = format.format(fileSize / Math.pow(1024, i));
                sizeString = (formattedSize)+indexUnit[i];
            }
        }

        FileUploadDTO fileUploadDTO = FileUploadDTO.builder()
                .fileId(fileUpload.getFileId())
                .originalName(fileUpload.getOriginalName())
                .fileName(fileUpload.getFileName())
                .filePath(fileUpload.getFilePath())
                .fileType(fileUpload.getFileType())
                .uploader(fileUpload.getUploader())
                .fileSize(sizeString)
                .build();

        return fileUploadDTO;
    }
}
