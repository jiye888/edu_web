package com.jtudy.education.service;

import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.DTO.InfoGuideDTO;
import com.jtudy.education.DTO.InfoGuideFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Image;
import com.jtudy.education.entity.InfoGuide;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.ImageRepository;
import com.jtudy.education.repository.InfoGuideRepository;
import com.jtudy.education.repository.specification.InfoGuideSpecification;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class InfoGuideServiceImpl implements InfoGuideService{

    private final InfoGuideRepository infoGuideRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean validateMember(SecurityMember member) {
        try {
            boolean isAdmin = member.getMember().getRolesList().contains(Roles.ADMIN);
            return isAdmin;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InfoGuideDTO> getAll(Pageable pageable) {
        Page<InfoGuide> infoGuide = infoGuideRepository.findAll(pageable);
        Page<InfoGuideDTO> infoGuideDTO = infoGuide.map(e -> entityToDTO(e));
        return infoGuideDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public InfoGuideDTO getOne(Long infoNum) {
        InfoGuide infoGuide = infoGuideRepository.findByInfoNum(infoNum);
        InfoGuideDTO infoGuideDTO = entityToDTO(infoGuide);
        return infoGuideDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageDTO> getAllImages(Long infoNum) {
        List<Image> image = imageRepository.findByInfoNum(infoNum);
        List<ImageDTO> images = image.stream().map(e -> {
            try {
                return imageService.entityToDTO(e);
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
        return images;
    }

    @Override
    public Long register(InfoGuideFormDTO infoGuideFormDTO, Member member) {
        InfoGuide infoGuide = formToEntity(infoGuideFormDTO);
        infoGuideRepository.save(infoGuide);
        return infoGuide.getInfoNum();
    }

    @Override
    public void checkImageName(Long infoNum, Image image) {
        if (imageRepository.existsByOriginalNameAndInfoNum(image.getOriginalName(), infoNum)) {
            String newName = imageService.getNewName(image);
            image.changeOriginalName(newName);
        }
    }

    @Override
    public Image setInfoGuide(Image image, Long infoNum, List<String> imgArray) {
        if (image != null && infoNum != null && imgArray != null && !(imgArray.isEmpty())) {
            InfoGuide infoGuide = infoGuideRepository.findByInfoNum(infoNum);
            image.setInfoGuide(infoGuide, imgArray.get(1), imgArray.get(2), imgArray.get(3));
            checkImageName(infoNum, image);
            return image;
        }
        return null;
    }

    @Override
    public void registerImg(MultipartFile[] images, List<List<String>> imgArray, Long infoNum, Member member) throws IOException {
        InfoGuide infoGuide = infoGuideRepository.findByInfoNum(infoNum);
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                List<String> imgArr = imageService.matchArray(image.getOriginalFilename(), imgArray);
                if (imgArr != null) {
                    Image img = imageService.fileToEntity(image, member);
                    img = setInfoGuide(img, infoNum, imgArr);
                    imageService.uploadImage(image, img);
                    infoGuide.addImage(img);
                    imageRepository.save(img);
                }
            }
            infoGuideRepository.save(infoGuide);
        }
    }

    @Override
    public Long update(InfoGuideFormDTO infoGuideFormDTO) {
        InfoGuide infoGuide = infoGuideRepository.findByInfoNum(infoGuideFormDTO.getInfoNum());
        infoGuide.changeInfoGuide(infoGuideFormDTO.getTitle(), infoGuideFormDTO.getContent());
        infoGuideRepository.save(infoGuide);
        return infoGuide.getInfoNum();
    }

    @Override
    public void updateImg(MultipartFile[] images, List<List<String>> imgArray, List<List<String>> existImgArray, Long infoNum, Member member) throws IOException {
        InfoGuide infoGuide = infoGuideRepository.findByInfoNum(infoNum);
        List<Image> existImages = imageRepository.findByInfoNum(infoNum);
        List<Image> modifyList = imageService.modifyImages(existImages, existImgArray);
        if (modifyList != null && modifyList.size() > 0) {
            for (Image modify : modifyList) {
                infoGuide.addImage(modify);
            }
            infoGuideRepository.save(infoGuide);
        }
        List<Image> deleteList = imageService.deleteImages(existImages, existImgArray);
        if (deleteList != null && deleteList.size() > 0) {
            for (Image deleteEntity : deleteList) {
                infoGuide.removeImage(deleteEntity);
                imageService.deleteImage(deleteEntity);
            }
            infoGuideRepository.save(infoGuide);
        }
        if (images != null && images.length > 0) {
            registerImg(images, imgArray, infoNum, member);
        }
    }

    @Override
    public void delete(Long infoNum) throws IOException {
        removeAllImg(infoNum);
        infoGuideRepository.deleteById(infoNum);
    }

    @Override
    public void removeAllImg(Long infoNum) throws IOException {
        List<Image> images = imageRepository.findByInfoNum(infoNum);
        for (Image image : images) {
            removeImg(image.getImageId(), infoNum);
        }
    }

    @Override
    public void removeImg(Long imageId, Long infoNum) throws IOException {
        InfoGuide infoGuide = infoGuideRepository.findByInfoNum(infoNum);
        Image image = imageRepository.findByImageId(imageId);
        try {
            infoGuide.removeImage(image);
            imageService.deleteImage(image);
            infoGuideRepository.save(infoGuide);
        } catch (IOException e) {
            throw new IOException();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InfoGuideDTO> search(Map<String, String> map, Pageable pageable) {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (entry.getValue() == null) {
                iterator.remove();
            }
        }

        List<String> categories = new ArrayList<>(map.keySet());
        Specification<InfoGuide> spec = Specification.where(null);
        if (categories.isEmpty()) {
            throw new NullPointerException("검색어를 입력해주세요.");
        } else {
            for (String category : categories) {
                if (category.contains("title")) {
                    spec = spec.and(InfoGuideSpecification.titleContaining(map.get("title")));
                }
                if (category.contains("content")) {
                    spec = spec.and(InfoGuideSpecification.contentContaining(map.get("content")));
                }
            }
        }
        Page<InfoGuide> info = infoGuideRepository.findAll(spec, pageable);
        Page<InfoGuideDTO> infoDTO = info.map(e -> entityToDTO(e));
        return infoDTO;
    }

}
