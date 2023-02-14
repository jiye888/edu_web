package com.jtudy.education.service;

import com.jtudy.education.DTO.ReviewDTO;
import com.jtudy.education.DTO.ReviewFormDTO;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.AcademyMember;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.Review;
import com.jtudy.education.repository.AcademyMemberRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AcademyRepository academyRepository;
    private final AcademyMemberRepository academyMemberRepository;

    @Override
    public boolean validateMember(Long revNum, SecurityMember member) {
        Review review = reviewRepository.findByRevNum(revNum);
        Academy academy = review.getAcademy();
        List<AcademyMember> academyMemberList = academyMemberRepository.findByMember(member.getMember());
        List<Academy> academyList = academyMemberList.stream().map(e -> e.getAcademy()).collect(Collectors.toList());
        boolean regInfo = academyList.contains(academy);
        boolean modInfo = review.getCreatedBy().equals(member.getUsername());
        return (regInfo || modInfo);
    }

    @Override
    public Page<ReviewDTO> getAll(Long acaNum, Pageable pageable) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        //Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "revNum"));
        Page<Review> review = reviewRepository.findByAcademy(academy, pageable);
        Page<ReviewDTO> reviewDTO = review.map(e -> entityToDTO(e));
        return reviewDTO;
    }

    @Override
    public ReviewDTO getOne(Long revNum) {
        Review review = reviewRepository.findByRevNum(revNum);
        ReviewDTO reviewDTO = entityToDTO(review);
        return reviewDTO;
    }

    @Override
    public Long register(ReviewFormDTO reviewFormDTO) {
        Academy academy = academyRepository.findByAcaNum(reviewFormDTO.getAcademy());
        Review review = formToEntity(reviewFormDTO, academy);
        reviewRepository.save(review);
        return review.getRevNum();
    }

    @Override
    public Long update(ReviewFormDTO reviewFormDTO) {
        Review review = reviewRepository.findByRevNum(reviewFormDTO.getNumber());
        System.out.println("!!!"+review.getAcademy());
        System.out.println(review.getTitle());
        review.changeReview(reviewFormDTO.getTitle(), reviewFormDTO.getContent(), reviewFormDTO.getGrade());
        System.out.println("!!!!!"+review.getAcademy());
        System.out.println(review.getTitle());
        reviewRepository.save(review);
        return review.getRevNum();
    }

    @Override
    public void delete(Long revNum) {
        reviewRepository.deleteById(revNum);
    }

    @Override
    public Page<ReviewDTO> getReviews(Member member) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "revNum"));
        Page<Review> review = reviewRepository.findByCreatedBy(member.getEmail(), pageable);
        Page<ReviewDTO> reviewDTO = review.map(e -> entityToDTO(e));
        return reviewDTO;
    }

}
