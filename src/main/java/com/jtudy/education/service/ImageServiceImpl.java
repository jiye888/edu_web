package com.jtudy.education.service;

import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.DTO.ImgArrayDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Image;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public String getNewName(Image image) {
        String name = image.getOriginalName();
        Pattern pattern = Pattern.compile("\\(\\d+\\)\\.[a-z]+$");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            String numbers = matcher.group();
            String number = numbers.replaceAll("\\D+", "");
            int nextNumber = Integer.parseInt(number) + 1;
            String newName = name.substring(0, name.lastIndexOf("(")+1) + nextNumber + name.substring(name.lastIndexOf(")"));
            return newName;
        } else {
            String newName = name.substring(0, name.lastIndexOf(".")) + "(2)" + name.substring(name.lastIndexOf("."));
            return newName;
        }
    }

    @Override
    public Image setNewName(Image image, ImgArrayDTO imgArrayDTO) {
        if (imgArrayDTO.getBase64() != null) {
            String newName = getNewName(image);
            image.changeOriginalName(newName);
        }
        return image;
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
        String textIndex;
        Integer arrayIndex = -1;

        if (image.getIndex() != null && image.getIndex().indexOf(",") != -1) {
            textIndex = image.getIndex().substring(0, image.getIndex().indexOf(","));
            arrayIndex = Integer.valueOf(image.getIndex().substring(image.getIndex().indexOf(",")+1));
        } else {
            textIndex = image.getIndex();
        }
        textIndex = (textIndex == null) || (textIndex.equals("")) ? null : textIndex;
        //원래는 index가 null이면 안되긴 하는데..........

        ImageDTO imageDTO = ImageDTO.builder()
                .imageId(image.getImageId())
                .originalName(image.getOriginalName())
                .name(image.getName())
                .path(image.getPath())
                .mimeType(mimeType)
                .base64(base64)
                .preText(image.getPreText())
                .textIndex(textIndex == null ? null: Integer.valueOf(textIndex))
                .arrayIndex(arrayIndex)
                .postText(image.getPostText())
                .uploader(image.getUploader().getEmail())
                .build();

        return imageDTO;
    }

    @Override
    public Image setInfo(Image image, ImgArrayDTO imgArrayDTO) {
        String index = imgArrayDTO.getTextIndex() + "," + imgArrayDTO.getArrayIndex();
        image.changeInfo(imgArrayDTO.getPreText(), imgArrayDTO.getPostText(), index);
        return image;
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
    public ImgArrayDTO matchDTO(MultipartFile image, List<ImgArrayDTO> arrayList) throws IOException {
        for (ImgArrayDTO imgArrayDTO : arrayList) {
            if (image.getOriginalFilename().equals(imgArrayDTO.getName())) {
                if (imgArrayDTO.getBase64() != null) {
                    byte[] imageByte = image.getBytes();
                    byte[] base64Byte = Base64.getDecoder().decode(imgArrayDTO.getBase64());
                    if (Arrays.equals(imageByte, base64Byte)) {
                        return imgArrayDTO;
                    }
                } else {
                    return imgArrayDTO;
                }
            }
        }
        return null;
    }

    @Override
    public ImgArrayDTO matchDTO(String name, List<ImgArrayDTO> imgArray) {
        for (ImgArrayDTO imgArrayDTO : imgArray) {
            if (name.equals(imgArrayDTO.getName())) {
                return imgArrayDTO;
            }
        }
        return null;
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
    public boolean isNotNullOrEmpty(MultipartFile[] images, List<ImgArrayDTO> imgArray) {
        if (images == null || !(images.length > 0)) {
            return false;
        } else if (imgArray == null || imgArray.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<Image> deleteImages(List<Image> existImages, List<ImgArrayDTO> existImgArray) {
        List<Image> deleteList = new ArrayList<>();
        if (existImages != null && !(existImages.isEmpty())) {
            if (existImgArray == null || existImgArray.isEmpty()) {
                deleteList.addAll(existImages);
                return deleteList;
            } else {
                for (Image existImage : existImages) {
                    ImgArrayDTO existImgArr = matchDTO(existImage.getOriginalName(), existImgArray);
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
    public List<Image> modifyImages(List<Image> existImages, List<ImgArrayDTO> existImgArray) {
        if (existImages != null && !(existImages.isEmpty())) {
            if (existImgArray != null && !(existImgArray.isEmpty())) {
                for (Image existImage : existImages) {
                    ImgArrayDTO existImgArr = matchDTO(existImage.getOriginalName(), existImgArray);
                    if (existImgArr != null) {
                        setInfo(existImage, existImgArr);
                        imageRepository.save(existImage);
                    }
                }
            }
        }
        return null;
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


}
