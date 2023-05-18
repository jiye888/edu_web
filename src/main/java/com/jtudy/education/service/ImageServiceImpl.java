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
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
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
        String date = LocalDate.now().toString();
        String datePath = date.replaceAll("-", "/");
        Path uploadPath = Paths.get(path + datePath);
        String filePath = uploadPath + "\\" + fileName;

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Image image = Image.builder()
                .originalName(originalName)
                .name(fileName)
                .path(filePath)
                .uploader(member)
                .build();

        return image;
    }

    @Override
    public ImageDTO entityToDTO(Image image) throws IOException {
        PathResource resource = new PathResource(image.getPath());
        File file = resource.getFile();
        String mimeType = Files.probeContentType(file.toPath());
        byte[] bytes = Files.readAllBytes(file.toPath());
        String base64 = Base64Utils.encodeToString(bytes);

        ImageDTO imageDTO = ImageDTO.builder()
                .imageId(image.getImageId())
                .originalName(image.getOriginalName())
                .name(image.getName())
                .path(image.getPath())
                .mimeType(mimeType)
                .base64(base64)
                .index(image.getIndex())
                .preText(image.getPreText())
                .postText(image.getPostText())
                .uploader(image.getUploader().getEmail())
                .build();

        return imageDTO;
    }

    @Override
    public Image uploadImage(MultipartFile img, Image image){

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
            List<ImageDTO> dtoList = fileList.stream().map(e -> {
                try {
                    return entityToDTO(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());
            return dtoList;
        } else if (entity == "review") {
            List<Image> fileList = imageRepository.findByRevNum(entityId);
            List<ImageDTO> dtoList = fileList.stream().map(e -> {
                try {
                    return entityToDTO(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());
            return dtoList;
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    public ImageDTO getAcademyMain(Long acaNum) throws IOException {
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
    public boolean isNotNullOrEmpty(MultipartFile[] images, List<List<String>> imgArray) {
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
            Resource resource = new PathResource(existImage.getPath());
            Double existSize = (double) resource.contentLength();
            Double imageSize = (double) newImage.getSize();
            return (existSize * 0.9 <= imageSize) && (existSize * 1.1 >= imageSize);
        }
        return false;
    }

    @Override
    public List<Image> getImagesToDelete(List<Image> existImages, List<List<String>> existImgArray) {
        List<Image> deleteList = new ArrayList<>();
        if (existImages != null && !(existImages.isEmpty())) {
            if (existImgArray == null || existImgArray.isEmpty()) {
                deleteList.addAll(existImages);
                return deleteList;
            } else {
                for (Image existImage : existImages) {
                    List<String> existImgArr = matchArray(existImage.getOriginalName(), existImgArray);
                    if (existImgArr == null) {
                        deleteList.add(existImage);
                    }
                }
            }
            return deleteList;
        }
        return null;
    }

    @Override
    public List<Image> modifyImages(List<Image> existImages, List<List<String>> existImgArray) {
        if (existImages != null && !(existImages.isEmpty())) {
            if (existImgArray != null && !(existImgArray.isEmpty())) {
                for (Image existImage : existImages) {
                    List<String> existImgArr = matchArray(existImage.getOriginalName(), existImgArray);
                    if (existImgArr != null && !(existImgArr.isEmpty())) {
                        boolean index = existImage.getIndex().equals(existImgArr.get(1));
                        boolean preText = existImage.getPreText().equals(existImgArr.get(2));
                        boolean postText = existImage.getPostText().equals(existImgArr.get(3));
                        if (!index || !preText || !postText) {
                            existImage.changeInfo(existImgArr.get(1), existImgArr.get(2), existImgArr.get(3));
                            imageRepository.save(existImage);
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Image updateImage(Image image, List<String> imgArray) {
        image.changeInfo(imgArray.get(1), imgArray.get(2), imgArray.get(3));
        imageRepository.save(image);
        return image;
    }

    @Override
    public void deleteImage(Image image) throws IOException {
        if (Files.exists(Paths.get(image.getPath()))) {
            Files.delete(Paths.get(image.getPath()));
        }

        String folderPath = image.getPath().substring(0, image.getPath().lastIndexOf("\\"));

        File folder = new File(folderPath);
        if (folder.isDirectory() && folder.list().length == 0) {
            folder.delete();
        }

        imageRepository.delete(image);
    }

    @Override
    public List<String> matchArray(String name, List<List<String>> imgArray) {
        for (List<String> imgArr : imgArray) {
            if (name.equals(imgArr.get(0))) {
                return imgArr;
            }
        }
        return null;
    }

}
