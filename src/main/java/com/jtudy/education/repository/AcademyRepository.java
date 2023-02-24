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

    Page<Academy> findByAcaNameContaining(String keyword, Pageable pageable);

    Page<Academy> findBySubjectContaining(String keyword, Pageable pageable);
    //@Query("select a from Academy a where a.subject in :subjects")
    //Page<Academy> findBySubjectContaining(Set<Subject> subjects, Pageable pageable);
    //@Query("SELECT a FROM Academy a WHERE :subject = TREAT(a.subject AS Subject)")
    //List<Academy> findBySubjectContaining(EnumSet<Subject> subjects);
    @Query(value = "SELECT * FROM academy WHERE FIND_IN_SET(:subject, subject)", nativeQuery = true)
    List<Academy> findBySubjectContaining(@Param("subject") String subject);
    /*@Query("SELECT a FROM Academy a WHERE " +
            "(:subject is null OR :subject = '' OR " +
            "CONCAT(',', a.subject, ',') LIKE CONCAT('%,', :subject, ',%'))")
    List<Academy> findBySubjectContaining(@Param("subjects") List<String> subject);*/




    Page<Academy> findByLocationContaining(String keyword, Pageable pageable);


}
