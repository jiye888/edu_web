package com.jtudy.education.repository;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademyRepository extends JpaRepository<Academy, Long> {

    Academy findByAcaNum(Long acaNum);

    @Query("select new com.jtudy.education.DTO.AcademyDTO(a, avg(r.grade), count(r)) from Academy a left outer join Review r on r.academy = a group by a")
    List<AcademyDTO> getAcademyWithReviewInfo();

    //@Query("select a, n from Academy a left outer join Notice n on n.academy = a where a.acaNum = :acaNum")
    //Page<Academy> getWithNotice(Long acaNum);

    //@Query("select a, r from Academy a left outer join Review r on r.academy = a where a.acaNum = :acaNum")
    //Page<Academy> getAcademyWithReview(Long acaNum);

    Page<Academy> findByAcaNameContaining(String keyword, Pageable pageable);

    Page<Academy> findBySubjectContaining(String keyword, Pageable pageable);

    Page<Academy> findByLocationContaining(String keyword, Pageable pageable);

}
