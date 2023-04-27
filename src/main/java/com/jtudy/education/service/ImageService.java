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
                .order(image.getOrder())
                .index(image.getIndex())
                .uploader(image.getUploader().getEmail())
                .build();

        return imageDTO;
    }

    ImageDTO getAcademyMain(Long acaNum);

    void deleteAcademyMain(Long acaNum);

    List<ImageDTO> getList(String entity, Long entityId) throws FileNotFoundException;
}
