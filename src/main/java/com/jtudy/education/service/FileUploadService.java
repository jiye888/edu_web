package com.jtudy.education.service;

import com.jtudy.education.entity.FileUpload;
import com.jtudy.education.entity.Member;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileUploadService {
    void isValidName(String name) throws InvalidFileNameException;

    boolean isValidExtension(String extension) throws IOException;

    FileUpload fileToEntity(MultipartFile file, Member member) throws IOException;

    File getFile(FileUpload fileUpload) throws FileNotFoundException;

    Long uploadFile(MultipartFile file, Member member) throws IOException;
}
