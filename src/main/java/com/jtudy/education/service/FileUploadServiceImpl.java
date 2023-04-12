package com.jtudy.education.service;

import com.jtudy.education.DTO.FileUploadDTO;
import com.jtudy.education.config.exception.CustomException;
import com.jtudy.education.config.exception.ExceptionCode;
import com.jtudy.education.entity.FileUpload;
import com.jtudy.education.repository.FileUploadRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileUploadRepository fileUploadRepository;

    @Value("${upload.dir}")
    private String path;

    @Override
    public void isValidName(String name) throws InvalidFileNameException {
        String[] invalid = {"/", ":"};
        for (String invalidChar : invalid) {
            if (name.contains(invalidChar)) {
                throw new InvalidFileNameException(invalidChar, "올바르지 않은 파일명입니다.");
            }
        }
    }

    @Override
    public FileUploadDTO fileToDTO(MultipartFile file, Map<String, Long> entity) throws IOException {
        String originalName = file.getOriginalFilename();
        isValidName(originalName);
        String fileName = UUID.randomUUID().toString();
        String fileType = file.getContentType();
        String date = LocalDate.now().toString();
        String datePath = date.replaceAll("-", "/");
        Path uploadPath = Paths.get(path + datePath);
        byte[] fileData = file.getBytes();
        Path filePath = Paths.get(uploadPath + fileName);

        FileUploadDTO fileDTO = FileUploadDTO.builder()
                .originalName(originalName)
                .fileName(fileName)
                .fileData(fileData)
                .uploadPath(uploadPath)
                .filePath(filePath)
                .fileType(fileType)
                .build();

        return fileDTO;
    }

    @Override
    public Long uploadFile(FileUploadDTO fileDTO) throws IOException {
        if (Files.exists(fileDTO.getUploadPath())) {
            Files.createDirectory(fileDTO.getUploadPath());
        }

        FileUpload fileUpload = DTOtoEntity(fileDTO);
        Files.write(fileDTO.getFilePath(), fileDTO.getFileData());
        FileUpload uploaded = fileUploadRepository.save(fileUpload);

        return uploaded.getFileId();
    }

}
