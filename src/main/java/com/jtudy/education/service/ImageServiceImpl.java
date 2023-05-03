package com.jtudy.education.service;

import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Image;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.ImageRepository;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final AcademyRepository academyRepository;
    private final ImageRepository imageRepository;

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
    public boolean isImage(MultipartFile file) {
        String fileType = file.getContentType();
        return fileType.contains("image");
    }

    @Override
    public Image fileToEntity(MultipartFile file, Member member) throws IOException {
        if (!isImage(file)) {
            throw new IOException("유효한 파일 형식이 아닙니다.");
        }

        String originalName = file.getOriginalFilename();
        isValidName(originalName);
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String fileName = UUID.randomUUID()+extension;
        String date = LocalDate.now()+toString();
        String datePath = date.replaceAll("-", "/");
        Path uploadPath = Paths.get(path + datePath);
        Path filePath = Paths.get(uploadPath + "\\" + fileName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Image image = Image.builder()
                .originalName(originalName)
                .name(fileName)
                .path(String.valueOf(filePath))
                .uploader(member)
                .build();

        return image;
    }

    @Override
    public Image uploadImage(MultipartFile img, Member member) throws IOException {
        Image image = fileToEntity(img, member);

        try (InputStream inputStream = img.getInputStream()) {
            Files.copy(inputStream, Paths.get(image.getPath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    @Override
    public List<ImageDTO> getList(String entity, Long entityId) throws FileNotFoundException {
        if (entity == "notice") {
            List<Image> fileList = imageRepository.findByNotNum(entityId);
            List<ImageDTO> dtoList = fileList.stream().map(e -> entityToDTO(e)).collect(Collectors.toList());
            return dtoList;
        } else if (entity == "review") {
            List<Image> fileList = imageRepository.findByRevNum(entityId);
            List<ImageDTO> dtoList = fileList.stream().map(e -> entityToDTO(e)).collect(Collectors.toList());
            return dtoList;
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    public ImageDTO getAcademyMain(Long acaNum) {
        Image image = imageRepository.findByAcaNum(acaNum);
        ImageDTO fileDTO = entityToDTO(image);
        return fileDTO;
    }

    @Override
    public void deleteAcademyMain(Long acaNum) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        Image image = imageRepository.findByAcaNum(acaNum);
        academy.setImage(null);
        academyRepository.save(academy);
        imageRepository.deleteById(image.getImageId());
    }

    @Override
    public boolean isNullOrEmpty(MultipartFile[] images, List<List> imgArray) {
        if (images == null || !(images.length > 0)) {
            return false;
        } else if (imgArray == null || imgArray.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isInRangeSize(MultipartFile newImage, Image existImage) throws IOException {;
        if (existImage != null) {
            Resource resource = new UrlResource("file:" + existImage.getPath());
            Double existSize = (double) resource.contentLength();
            Double imageSize = (double) newImage.getSize();
            return (existSize * 0.9 <= imageSize) && (existSize * 1.1 >= imageSize);
        }
        return false;
    }

    @Override
    public boolean needsUpdateFile(MultipartFile[] images, List<Image> existImages) throws IOException {
        if (existImages == null || existImages.isEmpty()) {
            return true;
        }
        List<String> existNames = existImages.stream().map(e -> e.getOriginalName()).collect(Collectors.toList());
        if (images != null) {
            for (MultipartFile image : images) {
                if (!(existNames.contains(image.getOriginalFilename()))) {
                    return true;
                } else {
                    Integer existIndex = existNames.indexOf(image.getOriginalFilename());
                    Image existImage = existImages.get(existIndex);
                    if (!isInRangeSize(image, existImage)) {
                        return true;
                    }
                }
            }
        } else if (images == null || !(images.length > 0)) {
            return true; // 기존 파일이 존재하고 전달할 파일은 존재하지 x > 삭제(수정)
        }
        return false;
    } // 파일 업로드 및 삭제 필요한 경우

    @Override
    public boolean needsUpdateInfo(MultipartFile[] images, List<List> imgArray, List<Image> existImages) throws IOException {
        if (existImages != null && !(existImages.isEmpty()) && !(isNullOrEmpty(images, imgArray))) {
            List<String> existNames = existImages.stream().map(e -> e.getOriginalName()).collect(Collectors.toList());
            for (int i=0; i<images.length; i++) {
                MultipartFile image = images[i];
                Integer existIndex = existNames.indexOf(image.getOriginalFilename());
                if (existIndex > -1) {
                    Image existImage = existImages.get(existIndex);
                    if (isInRangeSize(image, existImage)) {
                        for (List<Object> imgArr : imgArray) {
                            if (imgArr.get(0).equals(existImage.getOriginalName())) {
                                boolean isSameIndex = imgArr.get(1).equals(existImage.getIndex());
                                boolean isSamePreText = imgArr.get(2) == existImage.getPreText();
                                boolean isSamePostText = imgArr.get(3) == existImage.getPostText();
                                return isSameIndex && isSamePreText && isSamePostText;
                            }
                        }
                    }
                }
            }
        }
        return false;
    } // 파일 엔티티 수정 필요한 경우(order, index)

    @Override
    public Image updateImage(Image image, List<Object> imgArray) {
        image.changeInfo(imgArray.get(1).toString(), imgArray.get(2).toString(), imgArray.get(3).toString());
        imageRepository.save(image);
        return image;
    }

    @Override
    public void deleteImage(Image image) throws IOException {
        Files.delete(Paths.get(image.getPath()));

        String date = LocalDate.now()+toString();
        String datePath = date.replaceAll("-", "/");
        Path uploadPath = Paths.get(path + datePath);

        String folderPath = image.getPath().substring(0, image.getPath().lastIndexOf("/"));
        if (!folderPath.equals(uploadPath)) {
            File folder = new File(folderPath);
            if (folder.isDirectory() && folder.list().length == 0) {
                folder.delete();
            }
        }
    }

}
