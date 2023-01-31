package com.jtudy.education.service;

import com.jtudy.education.DTO.ReviewDTO;
import com.jtudy.education.DTO.ReviewFormDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.Review;
import com.jtudy.education.security.SecurityMember;
import org.springframework.data.domain.Page;

public interface ReviewService {

    boolean validateMember(Long revNum, SecurityMember member);

    Page<ReviewDTO> getAll(Long acaNum);

    ReviewDTO getOne(Long revNum);

    Long register(ReviewFormDTO reviewFormDTO);

    Long update(ReviewFormDTO reviewFormDTO);

    void delete(Long revNum);

    default Review dtoToEntity(ReviewDTO reviewDTO) {
        Review review = Review.builder()
                .revNum(reviewDTO.getRevNum())
                .title(reviewDTO.getTitle())
                .content(reviewDTO.getContent())
                .grade(reviewDTO.getGrade())
                .build();

        return review;
    }

    default Review formToEntity(ReviewFormDTO reviewFormDTO) {
        Review review = Review.builder()
                .title(reviewFormDTO.getTitle())
                .content(reviewFormDTO.getContent())
                .grade(reviewFormDTO.getGrade())
                .build();

        return review;
    }

    default ReviewDTO entityToDTO(Review review) {
        ReviewDTO reviewDTO = ReviewDTO.builder()
                .revNum(review.getRevNum())
                .title(review.getTitle())
                .content(review.getContent())
                .acaNum(review.getAcademy().getAcaNum())
                .acaName(review.getAcademy().getAcaName())
                .writerNum(review.getWriter().getMemNum())
                .writerName(review.getWriter().getName())
                .writerEmail(review.getWriter().getEmail())
                .grade(review.getGrade())
                .createdAt(review.getCreatedAt())
                .modifiedAt(review.getModifiedAt())
                .build();

        return reviewDTO;
    }
}
