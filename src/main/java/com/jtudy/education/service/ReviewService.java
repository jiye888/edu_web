package com.jtudy.education.service;

import com.jtudy.education.DTO.ImageDTO;
import com.jtudy.education.DTO.ReviewDTO;
import com.jtudy.education.DTO.ReviewFormDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Image;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.Review;
import com.jtudy.education.security.SecurityMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReviewService {

    boolean validateMember(Long revNum, SecurityMember member);

    Page<ReviewDTO> getAll(Long acaNum, Pageable pageable);

    ReviewDTO getOne(Long revNum);

    @Transactional(readOnly = true)
    List<ImageDTO> getAllImages(Long revNum);

    ReviewDTO getByAcademy(Long acaNum, String email);

    Long register(ReviewFormDTO reviewFormDTO, Member member) throws IOException;

    void registerImg(MultipartFile[] images, List<List<String>> imgArray, Long revNum, Member member) throws IOException;

    Long update(ReviewFormDTO reviewFormDTO);

    void updateImg(MultipartFile[] images, List<List<String>> imgArray, List<List<String>> existImgArray, Long revNum, Member member) throws IOException;

    void delete(Long revNum) throws IOException;

    Page<ReviewDTO> getReviews(Member member, Pageable pageable);

    default Review formToEntity(ReviewFormDTO reviewFormDTO, Academy academy, Member member) {
        Review review = Review.builder()
                .title(reviewFormDTO.getTitle())
                .content(reviewFormDTO.getContent())
                .grade(reviewFormDTO.getGrade())
                .academy(academy)
                .writer(member)
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
                .writerName(review.getWriter().getName())
                .writerEmail(review.getCreatedBy())
                .grade(review.getGrade())
                .createdAt(review.getCreatedAt())
                .modifiedAt(review.getModifiedAt())
                .build();
        return reviewDTO;
    }


    void removeAllImg(Long revNum) throws IOException;
}
