package com.jtudy.education.service;

import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.entity.Image;
import com.jtudy.education.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    void isValidName(String name);

    boolean isImage(MultipartFile file);

    String getNewName(Image image);

    Image fileToEntity(MultipartFile file, Member member) throws IOException;

    ImageDTO entityToDTO(Image image) throws IOException;

    ImageDTO getAcademyMain(Long acaNum) throws IOException;

    void deleteAcademyMain(Long acaNum);

    Image uploadImage(MultipartFile img, Image image);

    boolean isNotNullOrEmpty(MultipartFile[] images, List<List<String>> imgArray);

    List<Image> deleteImages(List<Image> existImages, List<List<String>> existImgArray);

    List<Image> modifyImages(List<Image> existImages, List<List<String>> existImgArray);

    void deleteImage(Image image) throws IOException;

    List<String> matchArray(String name, List<List<String>> imgArray);
}
