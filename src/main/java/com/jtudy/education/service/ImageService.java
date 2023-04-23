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

    boolean isImage(String fileType);

    Image fileToEntity(MultipartFile file, Member member) throws IOException;

    default ImageDTO entityToDTO(Image image) {
        ImageDTO imageDTO = ImageDTO.builder()
                .imageId(image.getImageId())
                .originalName(image.getOriginalName())
                .fileData(image.getFileData())
                .uploader(image.getUploader().getEmail())
                .build();

        return imageDTO;
    }

    ImageDTO getAcademyMain(Long acaNum);

    void deleteAcademyMain(Long acaNum);

    void setImagePositions(MultipartFile[] files, List<Integer> orders, String content, Member member) throws IOException;

    List<ImageDTO> getList(String entity, Long entityId) throws FileNotFoundException;

    //File getFile(Image image) throws FileNotFoundException;
}
