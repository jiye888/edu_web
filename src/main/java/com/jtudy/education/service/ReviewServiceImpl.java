package com.jtudy.education.service;

import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.DTO.ImgArrayDTO;
import com.jtudy.education.DTO.ReviewDTO;
import com.jtudy.education.DTO.ReviewFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.*;
import com.jtudy.education.repository.AcademyMemberRepository;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.ImageRepository;
import com.jtudy.education.repository.ReviewRepository;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AcademyRepository academyRepository;
    private final AcademyMemberRepository academyMemberRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean validateMember(Long revNum, SecurityMember member) {
        try {
            Review review = reviewRepository.findByRevNum(revNum);
            Academy academy = review.getAcademy();
            List<AcademyMember> academyMemberList = academyMemberRepository.findByMember(member.getMember());
            List<Academy> academyList = academyMemberList.stream().map(e -> e.getAcademy()).collect(Collectors.toList());
            boolean regInfo = academyList.contains(academy);
            boolean modInfo = review.getCreatedBy().equals(member.getUsername());
            return (regInfo || modInfo || member.getMember().getRolesList().contains(Roles.ADMIN));
        } catch (NullPointerException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getAll(Long acaNum, Pageable pageable) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        Page<Review> review = reviewRepository.findByAcademy(academy, pageable);
        Page<ReviewDTO> reviewDTO = review.map(e -> entityToDTO(e));
        return reviewDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO getOne(Long revNum) {
        Review review = reviewRepository.findByRevNum(revNum);
        ReviewDTO reviewDTO = entityToDTO(review);
        return reviewDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageDTO> getAllImages(Long revNum) {
        List<Image> image = imageRepository.findByRevNum(revNum);
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
    @Transactional(readOnly = true)
    public ReviewDTO getByAcademy(Long acaNum, String email) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        Review review = reviewRepository.findByAcademyAndCreatedBy(academy, email);
        if (review == null) {
            return null;
        }
        ReviewDTO reviewDTO = entityToDTO(review);
        return reviewDTO;
    }

    @Override
    public Long register(ReviewFormDTO reviewFormDTO, Member member) throws IOException {
        Academy academy = academyRepository.findByAcaNum(reviewFormDTO.getAcademy());
        Review review = formToEntity(reviewFormDTO, academy, member);
        reviewRepository.save(review);
        return review.getRevNum();
    }

    @Override
    public void checkImageName(Long revNum, Image image) {
        if (imageRepository.existsByOriginalNameAndRevNum(image.getOriginalName(), revNum)) {
            String newName = imageService.getNewName(image);
            image.changeOriginalName(newName);
        }
    }

    @Override
    public Image setReview(Image image, Long revNum, ImgArrayDTO imgArray) {
        if (image != null && revNum != null && imgArray != null) {
            Review review = reviewRepository.findByRevNum(revNum);
            imageService.setInfo(image, imgArray); //*
            image.setReview(review);
            checkImageName(revNum, image);
            return image;
        }
        return null;
    }

    @Override
    public void registerImg(MultipartFile[] images, List<ImgArrayDTO> imgArray, Long revNum, Member member) throws IOException {
        Review review = reviewRepository.findByRevNum(revNum);
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                ImgArrayDTO imgArr = imageService.matchDTO(image, imgArray);
                if (imgArr != null) {
                    Image img = imageService.fileToEntity(image, member);
                    img = imageService.setNewName(img, imgArr);
                    img = setReview(img, revNum, imgArr);
                    imageService.uploadImage(image, img);
                    review.addImage(img);
                    imageRepository.save(img);
                }
            }
            reviewRepository.save(review);
        }
    }

    @Override
    public Long update(ReviewFormDTO reviewFormDTO) {
        Review review = reviewRepository.findByRevNum(reviewFormDTO.getNumber());
        review.changeReview(reviewFormDTO.getTitle(), reviewFormDTO.getContent(), reviewFormDTO.getGrade());
        reviewRepository.save(review);
        return review.getRevNum();
    }

    @Override
    public void updateImg(MultipartFile[] images, List<ImgArrayDTO> imgArray, List<ImgArrayDTO> existImgArray, Long revNum, Member member) throws IOException {
        Review review = reviewRepository.findByRevNum(revNum);
        List<Image> existImages = imageRepository.findByRevNum(revNum);
        List<Image> modifyList = imageService.modifyImages(existImages, existImgArray);
        /*if (modifyList != null && modifyList.size() > 0) {
            for (Image modify : modifyList) {
                imageService.modifyImages(existImages, existImgArray);
                review.addImage(modify);
            }
            reviewRepository.save(review);
            imageService.modifyImages(existImages, existImgArray);
        }*/
        List<Image> deleteList = imageService.deleteImages(existImages, existImgArray);
        if (deleteList != null && deleteList.size() > 0) {
            for (Image deleteEntity : deleteList) {
                review.removeImage(deleteEntity);
                imageService.deleteImage(deleteEntity);
            }
            reviewRepository.save(review);
        }
        if (images != null && images.length > 0) {
            registerImg(images, imgArray, revNum, member);
        }
    }

    @Override
    public void delete(Long revNum) throws IOException {
        removeAllImg(revNum);
        reviewRepository.deleteById(revNum);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviews(Member member, Pageable pageable) {
        Page<Review> review = reviewRepository.findByCreatedBy(member.getEmail(), pageable);
        Page<ReviewDTO> reviewDTO = review.map(e -> entityToDTO(e));
        return reviewDTO;
    }

    @Override
    public void removeAllImg(Long revNum) throws IOException {
        List<Image> images = imageRepository.findByRevNum(revNum);
        for (Image image : images) {
            removeImg(image.getImageId(), revNum);
        }
    }

    @Override
    public void removeImg(Long imageId, Long revNum) throws IOException {
        Review review = reviewRepository.findByRevNum(revNum);
        Image image = imageRepository.findByImageId(imageId);
        try {
            review.removeImage(image);
            imageService.deleteImage(image);
            reviewRepository.save(review);
        } catch (IOException e) {
            throw new IOException();
        }
    }

    @Override
    public void duplicateImage(Long revNum, ImgArrayDTO imgArrayDTO) {
        Review review = reviewRepository.findByRevNum(revNum);
        Image duplicate = imageRepository.findByRevNumAndOriginalName(revNum, imgArrayDTO.getDuplicate());
        Image image = imageService.duplicateImage(duplicate, imgArrayDTO);
        image.setReview(review);
        imageRepository.save(image);
    }

}
