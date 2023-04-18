package com.jtudy.education.service;

import com.jtudy.education.DTO.FileUploadDTO;
import com.jtudy.education.entity.FileUpload;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.FileUploadRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public FileUpload fileToEntity(MultipartFile file, Member member) throws IOException {
        String originalName = file.getOriginalFilename();
        isValidName(originalName);
        String fileName = UUID.randomUUID()+"."+originalName.substring(originalName.lastIndexOf(".")+1);
        System.out.println(fileName);
        String fileType = file.getContentType();
        String date = LocalDate.now().toString();
        String datePath = date.replaceAll("-", "/");
        Path uploadPath = Paths.get(path + datePath);
        byte[] fileData = file.getBytes();
        Path filePath = Paths.get(uploadPath + "\\" + fileName);

        FileUpload fileUpload = FileUpload.builder()
                .originalName(originalName)
                .fileName(fileName)
                .filePath(String.valueOf(filePath))
                .fileType(fileType)
                .fileData(fileData)
                .uploader(member)
                .build();

        return fileUpload;
    }

    @Override
    public Long uploadFile(FileUpload file) throws IOException {
        String filePath = file.getFilePath();
        Path uploadPath = Paths.get(filePath.substring(0, filePath.lastIndexOf(file.getFileName())));

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try {
            Files.write(Paths.get(filePath), file.getFileData());
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileUpload uploaded = fileUploadRepository.save(file);
        return uploaded.getFileId();
    }

    @Override
    public void deleteFile(Long fileId) {
        fileUploadRepository.deleteById(fileId);
    }

    @Override
    public void deleteAcademyMain(Long acaNum) {
        FileUpload fileUpload = fileUploadRepository.findByAcaNum(acaNum);
        fileUploadRepository.deleteById(fileUpload.getFileId());
    }

    @Override
    public List<FileUploadDTO> getList(String entity, Long entityId) throws FileNotFoundException {
        if (entity == "notice") {
            List<FileUpload> fileList = fileUploadRepository.findByNotNum(entityId);
            List<FileUploadDTO> dtoList = fileList.stream().map(e -> entityToDTO(e)).collect(Collectors.toList());
            return dtoList;
        } else if (entity == "review") {
            List<FileUpload> fileList = fileUploadRepository.findByRevNum(entityId);
            List<FileUploadDTO> dtoList = fileList.stream().map(e -> entityToDTO(e)).collect(Collectors.toList());
            return dtoList;
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    public FileUploadDTO academyMain(Long acaNum) throws FileNotFoundException {
        FileUpload fileUpload = fileUploadRepository.findByAcaNum(acaNum);
        FileUploadDTO fileDTO = entityToDTO(fileUpload);
        return fileDTO;
    }

    @Override
    public File getFile(FileUpload fileUpload) throws FileNotFoundException {
        String filePath = fileUpload.getFilePath();
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }

}
