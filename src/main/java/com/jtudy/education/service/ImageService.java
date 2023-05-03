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

    default ImageDTO entityToDTO(Image image) {
        ImageDTO imageDTO = ImageDTO.builder()
                .imageId(image.getImageId())
                .originalName(image.getOriginalName())
                .name(image.getName())
                .path(image.getPath())
                .index(image.getIndex())
                .preText(image.getPreText())
                .postText(image.getPostText())
                .uploader(image.getUploader().getEmail())
                .build();

        return imageDTO;
    }

    ImageDTO getAcademyMain(Long acaNum);

    void deleteAcademyMain(Long acaNum);

    Image uploadImage(MultipartFile img, Member member) throws IOException;

    List<ImageDTO> getList(String entity, Long entityId) throws FileNotFoundException;

    boolean isNullOrEmpty(MultipartFile[] images, List<List> imgArray);

    boolean isInRangeSize(MultipartFile newImage, Image existImage) throws IOException;

    boolean needsUpdateFile(MultipartFile[] images, List<Image> existImages) throws IOException;

    boolean needsUpdateInfo(MultipartFile[] images, List<List> imgArray, List<Image> existImages) throws IOException;

    Image updateImage(Image image, List<Object> imgArray);

    void deleteImage(Image image) throws IOException;
}
