package com.jtudy.education.repository;

import com.jtudy.education.DTO.AcademyDTO;
import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Academy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Repository
public interface AcademyRepository extends JpaRepository<Academy, Long>, JpaSpecificationExecutor<Academy> {

    Academy findByAcaNum(Long acaNum);

    Page<Academy> findAll(Specification<Academy> spec, Pageable pageable);

    @Query("select new com.jtudy.education.DTO.AcademyDTO(a, avg(r.grade), count(r)) from Academy a left outer join Review r on r.academy = a group by a")
    Page<AcademyDTO> getAllAcademyWithReviewInfo(Pageable pageable);

    @Query("select new com.jtudy.education.DTO.AcademyDTO(a, avg(r.grade), count(r)) from Academy a left outer join Review r on r.academy = a where a.acaNum = :acaNum")
    AcademyDTO getOneAcademyWithReviewInfo(Long acaNum);

    @Query("select a from Academy a join fetch a.member where a.member.memNum = :memNum")
    Page<Academy> findByMemNum(Long memNum, Pageable pageable);

    @Query(value = "SELECT * FROM academy WHERE FIND_IN_SET(:subject, subject)", nativeQuery = true)
    List<Academy> findBySubjectContaining(@Param("subject") String subject);

}
