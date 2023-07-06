package com.jtudy.education.service;

import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.DTO.ImgArrayDTO;
import com.jtudy.education.entity.Image;
import com.jtudy.education.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    void isValidName(String name);

    boolean isImage(MultipartFile file);

    String getNewName(Image image);

    Image setNewName(Image image, ImgArrayDTO imgArrayDTO);

    Image fileToEntity(MultipartFile file, Member member) throws IOException;

    ImageDTO entityToDTO(Image image) throws IOException;

    ImgArrayDTO matchDTO(MultipartFile image, List<ImgArrayDTO> arrayList) throws IOException;

    ImgArrayDTO matchDTO(String name, List<ImgArrayDTO> imgArray);

    ImageDTO getAcademyMain(Long acaNum) throws IOException;

    void deleteAcademyMain(Long acaNum);

    Image setInfo(Image image, ImgArrayDTO imgArrayDTO);

    Image uploadImage(MultipartFile img, Image image);

    boolean isNotNullOrEmpty(MultipartFile[] images, List<ImgArrayDTO> imgArray);

    List<Image> deleteImages(List<Image> existImages, List<ImgArrayDTO> existImgArray);

    List<Image> modifyImages(List<Image> existImages, List<ImgArrayDTO> existImgArray);

    void deleteImage(Image image) throws IOException;
}
