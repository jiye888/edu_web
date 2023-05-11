package com.jtudy.education.service;

import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.entity.Image;
import com.jtudy.education.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public interface ImageService {

    void isValidName(String name);

    boolean isImage(MultipartFile file);

    Image fileToEntity(MultipartFile file, Member member) throws IOException;

    ImageDTO entityToDTO(Image image) throws IOException;

    ImageDTO getAcademyMain(Long acaNum) throws IOException;

    void deleteAcademyMain(Long acaNum);

    Image uploadImage(MultipartFile img, Image image);

    List<ImageDTO> getList(String entity, Long entityId) throws FileNotFoundException;

    boolean isNotNullOrEmpty(MultipartFile[] images, List<List<String>> imgArray);

    boolean isInRangeSize(MultipartFile newImage, Image existImage) throws IOException;

    boolean needsUpdateFile(MultipartFile[] images, List<Image> existImages, List<List<String>> existImgArray) throws IOException;

    boolean needsUpdateInfo(List<Image> existImages, List<List<String>> existImgArray) throws IOException;

    Image updateImage(Image image, List<String> imgArray);

    void deleteImage(Image image) throws IOException;

    List<String> matchArray(String name, List<List<String>> imgArray);
}
