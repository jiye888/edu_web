package com.jtudy.education.service;

import com.jtudy.education.DTO.ReviewDTO;
import com.jtudy.education.entity.SubjectClass;
import com.jtudy.education.entity.Review;
import org.springframework.data.domain.Page;

public interface ReviewService {

    Page<ReviewDTO> getAll();

    ReviewDTO getOne(Long revNum);

    Long register(ReviewDTO reviewDTO);

    Long update(ReviewDTO reviewDTO);

    void delete(Long revNum);

    default Review dtoToEntity(ReviewDTO reviewDTO) {
        SubjectClass academyMember = SubjectClass.builder()
                .amNum(reviewDTO.getAmNum())
                .build();

        Review review = Review.builder()
                .revNum(reviewDTO.getRevNum())
                .title(reviewDTO.getTitle())
                .content(reviewDTO.getContent())
                .grade(reviewDTO.getGrade())
                .build();

        return review;
    }

    default ReviewDTO entityToDTO(Review review) {
        ReviewDTO reviewDTO = ReviewDTO.builder()
                .revNum(review.getRevNum())
                .title(review.getTitle())
                .content(review.getContent())
                .amNum(review.getAcademyMember().getAmNum())
                .acaNum(review.getAcademyMember().getAcademy().getAcaNum())
                .acaName(review.getAcademyMember().getAcademy().getAcaName())
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
