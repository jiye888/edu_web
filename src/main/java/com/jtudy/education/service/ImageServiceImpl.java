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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        Image image = Image.builder()
                .originalName(originalName)
                .name(fileName)
                .path(String.valueOf(filePath))
                .uploader(member)
                .build();

        return image;
    }
/*
    @Override
    public void setImagePositions(MultipartFile[] files, List<Integer> orders, String content, Member member) throws IOException {
        List<Image> images = new ArrayList<>();
        for (MultipartFile file: files) {
            images.add(fileToEntity(file, member));
        }
        Map<Image, Integer> map = new HashMap<>();
        for (int i=0; i<files.length; i++) {
            map.put(images.get(i), orders.get(i));
        }
        images.sort((i1, i2) -> map.get(i1) - map.get(i2));
        //images.sort(Comparator.comparingInt(map::get));

        String string = "\\(IMAGE_INCLUDED\\)";
        Pattern pattern = Pattern.compile(string);
        Matcher matcher = pattern.matcher(content);

        List<Integer> positions = new ArrayList<>();
        while (matcher.find()) {
            positions.add(matcher.start());
        }
        if (images.size() == positions.size()) {
            for (int i=0; i<images.size(); i++) {
                images.get(i).setPosition(positions.get(i));
            }
        }
    }*/

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
    public boolean needsUpdate(MultipartFile[] images, List<List> imgArray, List<Image> existImages) throws IOException {
        if (existImages == null || existImages.isEmpty()) {
            return true;
        }
        List<String> existNames = existImages.stream().map(e -> e.getOriginalName()).collect(Collectors.toList());
        if (isNullOrEmpty(images, imgArray)) {
            for (MultipartFile image : images) {
                if (!(existNames.contains(image.getOriginalFilename()))) {
                    return true;
                } else {
                    Image existImage = imageRepository.findByOriginalName(image.getOriginalFilename());
                    Resource resource = new UrlResource("file:"+existImage.getPath());
                    Double existSize = (double) resource.contentLength();
                    Double imageSize = (double) image.getSize();
                    boolean isInRange = (existSize * 0.9 <= imageSize) && (existSize * 1.1 >= imageSize);
                    if (!isInRange) {
                        return true;
                    } else {
                        List<Object> imgArr = imgArray.stream().filter(list -> list.contains(image.getOriginalFilename())).findFirst().orElse(null);
                        if (imgArr.get(1) != existImage.getOrder() || imgArr.get(2).toString() != existImage.getIndex()) {
                            return true; // 테스트 코드 작성해야
                        }
                    }
                }
            }
        }
        return false;
    }


}
