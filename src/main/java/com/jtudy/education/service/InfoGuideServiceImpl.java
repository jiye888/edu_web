package com.jtudy.education.service;

import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.DTO.ImgArrayDTO;
import com.jtudy.education.DTO.InfoGuideDTO;
import com.jtudy.education.DTO.InfoGuideFormDTO;
import com.jtudy.education.config.exception.GlobalExceptionHandler;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Image;
import com.jtudy.education.entity.InfoGuide;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.ImageRepository;
import com.jtudy.education.repository.InfoGuideRepository;
import com.jtudy.education.repository.specification.InfoGuideSpecification;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(InfoGuideServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public boolean validateMember(SecurityMember member) {
        try {
            boolean isAdmin = member.getMember().getRolesList().contains(Roles.ADMIN);
            return isAdmin;
        } catch (NullPointerException e) {
            return false;
        } catch (Exception e) {
            logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InfoGuideDTO> getAll(Pageable pageable) {
        Page<InfoGuide> infoGuide = infoGuideRepository.findAll(pageable);
        Page<InfoGuideDTO> infoGuideDTO = infoGuide.map(e -> {
            try {
                return entityToDTO(e);
            } catch (Exception ex) {
                logger.error(GlobalExceptionHandler.exceptionStackTrace(ex));
                return null;
            }});
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
            } catch (Exception ex) {
                logger.error(GlobalExceptionHandler.exceptionStackTrace(ex));
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
    public Image setInfoGuide(Image image, Long infoNum, ImgArrayDTO imgArray) {
        if (image != null && infoNum != null && imgArray != null) {
            InfoGuide infoGuide = infoGuideRepository.findByInfoNum(infoNum);
            imageService.setInfo(image, imgArray);
            image.setInfoGuide(infoGuide);
            checkImageName(infoNum, image);
            return image;
        }
        return null;
    }

    @Override
    public void registerImg(MultipartFile[] images, List<ImgArrayDTO> imgArray, Long infoNum, Member member) throws IOException {
        InfoGuide infoGuide = infoGuideRepository.findByInfoNum(infoNum);
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                ImgArrayDTO imgArr = imageService.matchDTO(image, imgArray);
                if (imgArr != null) {
                    if (imgArr.getDuplicate() != null) {
                        duplicateImage(infoNum, imgArr);
                    } else {
                        Image img = imageService.fileToEntity(image, member);
                        img = imageService.setNewName(img, imgArr);
                        img = setInfoGuide(img, infoNum, imgArr);
                        imageService.uploadImage(image, img);
                        infoGuide.addImage(img);
                        imageRepository.save(img);
                        imgArray.remove(imgArr);
                    }
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
    public void updateImg(MultipartFile[] images, List<ImgArrayDTO> imgArray, List<ImgArrayDTO> existImgArray, Long infoNum, Member member) throws IOException {
        InfoGuide infoGuide = infoGuideRepository.findByInfoNum(infoNum);
        List<Image> existImages = imageRepository.findByInfoNum(infoNum);
        imageService.modifyImages(existImages, existImgArray);
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
        infoGuide.removeImage(image);
        imageService.deleteImage(image);
        infoGuideRepository.save(infoGuide);
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

    @Override
    public void duplicateImage(Long infoNum, ImgArrayDTO imgArrayDTO) {
        InfoGuide infoGuide = infoGuideRepository.findByInfoNum(infoNum);
        Image duplicate = imageRepository.findByInfoNumAndOriginalName(infoNum, imgArrayDTO.getDuplicate());
        Image image = imageService.duplicateImage(duplicate, imgArrayDTO);
        image.setInfoGuide(infoGuide);
        imageRepository.save(image);
    }

}
