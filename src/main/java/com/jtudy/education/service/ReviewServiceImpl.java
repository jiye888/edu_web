package com.jtudy.education.service;

import com.jtudy.education.DTO.ReviewDTO;
import com.jtudy.education.DTO.ReviewFormDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.Review;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.MemberRepository;
import com.jtudy.education.repository.ReviewRepository;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AcademyRepository academyRepository;
    private final MemberRepository memberRepository;

    @Override
    public boolean validateMember(Long revNum, SecurityMember member) {
        Review review = reviewRepository.findByRevNum(revNum);
        if (review.getCreatedBy() == member.getUsername()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Page<ReviewDTO> getAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "revNum"));
        Page<Review> review = reviewRepository.findAll(pageable);
        Page<ReviewDTO> reviewDTO = review.map(e -> entityToDTO(e));
        return reviewDTO;
    }

    @Override
    public ReviewDTO getOne(Long revNum) {
        Review review = reviewRepository.findById(revNum)
                .orElseThrow(EntityNotFoundException::new);
        ReviewDTO reviewDTO = entityToDTO(review);
        return reviewDTO;
    }

    @Override
    public Long register(ReviewFormDTO reviewFormDTO, Long acaNum, Long memNum) {
        Review review = formToEntity(reviewFormDTO);
        Academy academy = academyRepository.findByAcaNum(acaNum);
        Member member = memberRepository.findByMemNum(memNum);
        review.builder().academy(academy).writer(member).build();
        reviewRepository.save(review);
        return review.getRevNum();
    }

    @Override
    public Long update(ReviewFormDTO reviewFormDTO) {
        Review review = reviewRepository.findByRevNum(reviewFormDTO.getRevNum());
        review.changeReview(reviewFormDTO.getTitle(), reviewFormDTO.getContent());
        reviewRepository.save(review);
        return review.getRevNum();
    }

    @Override
    public void delete(Long revNum) {
        reviewRepository.deleteById(revNum);
    }
}
