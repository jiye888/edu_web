package com.jtudy.education.service;

import com.jtudy.education.entity.FileUpload;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.FileUploadRepository;
import com.jtudy.education.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService{

    private final NoticeRepository noticeRepository;
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
    public boolean isValidExtension(String extension) throws IOException {
        String[] extensions = {".txt", ".hwp", ".pdf", ".xls", ".xlsx", ".doc", ".docx", ".ppt", ".pptx", ".png", ".jpg"};
        // 확장자명 하나하나 입력해야 하나?
        for (String e : extensions) {
            if (e.equals(extension)) {
                return true;
            }
        }
        throw new IOException("유효한 파일 형식이 아닙니다.");
    }

    @Override
    public FileUpload fileToEntity(MultipartFile file, Member member) throws IOException {
        String originalName = file.getOriginalFilename();
        isValidName(originalName);
        String extension = originalName.substring(originalName.lastIndexOf("."));
        isValidExtension(extension);

        String fileName = UUID.randomUUID()+extension;
        String fileType = file.getContentType();
        String date = LocalDate.now().toString();
        String datePath = date.replaceAll("-", "/");
        Path uploadPath = Paths.get(path + datePath);
        Path filePath = Paths.get(uploadPath + "\\" + fileName);

        FileUpload fileUpload = FileUpload.builder()
                .originalName(originalName)
                .fileName(fileName)
                .filePath(String.valueOf(filePath))
                .fileType(fileType)
                .uploader(member)
                .build();
        return fileUpload;
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

    @Override
    public Double getFileSize(FileUpload fileUpload) throws IOException {
        String filePath = fileUpload.getFilePath();
        Resource resource = new UrlResource("file:"+filePath);
        if (resource.exists()) {
            Double fileSize = Double.valueOf(resource.contentLength());
            return fileSize;
        }
        return null;
    }

    @Override
    public Long uploadFile(FileUpload fileUpload, MultipartFile file) throws IOException {
        /*
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".")+1);
        if (!isValidExtension(extension)) {
            throw new IOException("유효한 파일 형식이 아닙니다.");
        }
        FileUpload fileUpload = fileToEntity(file, member);
        */
        String filePath = fileUpload.getFilePath();
        Path uploadPath = Paths.get(filePath.substring(0, filePath.lastIndexOf(fileUpload.getFileName())));

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()){
            Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //fileUploadRepository.save(fileUpload);
        return fileUpload.getFileId();
    }

    @Override
    public boolean isNullOrEmpty(MultipartFile[] files) {
        return files != null && files.length > 0;
    }

    @Override
    public boolean needsUpdate(MultipartFile[] files, List<FileUpload> existFiles) throws IOException {
        if (existFiles == null || existFiles.isEmpty()) {
            return true;
        }
        List<String> existNames = existFiles.stream().map(e -> e.getOriginalName()).collect(Collectors.toList());
        if (isNullOrEmpty(files)) {
            for (MultipartFile file : files) {
                if (!(existNames.contains(file.getOriginalFilename()))) {
                    return true;
                } else {
                    FileUpload existFile = fileUploadRepository.findByOriginalName(file.getOriginalFilename());
                    Resource resource = new UrlResource("file:"+existFile);
                    Double existSize = (double) resource.contentLength();
                    Double fileSize = (double) file.getSize();
                    boolean isInRange = (existSize * 0.9 <= fileSize) && (existSize * 1.1 >= fileSize);
                    if (!isInRange) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
