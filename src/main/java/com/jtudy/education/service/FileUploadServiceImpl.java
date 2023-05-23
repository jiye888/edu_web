package com.jtudy.education.service;

import com.jtudy.education.entity.FileUpload;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.FileUploadRepository;
import com.jtudy.education.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public String getNewName(FileUpload fileUpload) {
        String name = fileUpload.getOriginalName();
        Pattern pattern = Pattern.compile("\\(\\d+\\)\\.[a-z]+$");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            String numbers = matcher.group();
            String number = numbers.replaceAll("\\D+", "");
            int nextNumber = Integer.parseInt(number) + 1;
            String newName = name.substring(0, name.lastIndexOf("(")+1) + nextNumber + name.substring(name.lastIndexOf(")"));
            return newName;
        } else {
            String newName = name.substring(0, name.indexOf(".")) + "(2)" + name.substring(name.lastIndexOf("."));
            return newName;
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

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

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
        Resource resource = new PathResource(filePath);
        if (resource.exists()) {
            Double fileSize = Double.valueOf(resource.contentLength());
            return fileSize;
        }
        return null;
    }

    @Override
    public FileUpload uploadFile(MultipartFile file, Member member) throws IOException {
        FileUpload fileUpload = fileToEntity(file, member);

        try (InputStream inputStream = file.getInputStream()){
            Files.copy(inputStream, Paths.get(fileUpload.getFilePath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUpload;
    }

}
