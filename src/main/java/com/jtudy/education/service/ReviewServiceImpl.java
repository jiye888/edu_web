package com.jtudy.education.service;

import com.jtudy.education.DTO.ReviewDTO;
import com.jtudy.education.DTO.ReviewFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.*;
import com.jtudy.education.repository.AcademyMemberRepository;
import com.jtudy.education.repository.AcademyRepository;
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
    public Long register(ReviewFormDTO reviewFormDTO, Member member) {
        Academy academy = academyRepository.findByAcaNum(reviewFormDTO.getAcademy());
        Review review = formToEntity(reviewFormDTO, academy, member);
        reviewRepository.save(review);
        return review.getRevNum();
    }
/*
    public void uploadImages(MultipartFile[] files, Long revNum, Member member) throws IOException {
        Review review = reviewRepository.findByRevNum(revNum);
        for (MultipartFile file : files) {
            Image image = imageService.fileToEntity(file, member);
            image.setReview(review);
            imageService.uploadImage(image, member);
        }
    }*/

    @Override
    public Long update(ReviewFormDTO reviewFormDTO) {
        Review review = reviewRepository.findByRevNum(reviewFormDTO.getNumber());
        review.changeReview(reviewFormDTO.getTitle(), reviewFormDTO.getContent(), reviewFormDTO.getGrade());
        reviewRepository.save(review);
        return review.getRevNum();
    }

    @Override
    public void delete(Long revNum) {
        reviewRepository.deleteById(revNum);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviews(Member member, Pageable pageable) {
        Page<Review> review = reviewRepository.findByCreatedBy(member.getEmail(), pageable);
        Page<ReviewDTO> reviewDTO = review.map(e -> entityToDTO(e));
        return reviewDTO;
    }

}
