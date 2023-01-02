package com.jtudy.education.repository;

import com.jtudy.education.entity.SubjectClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectClassRepository extends JpaRepository<SubjectClass, Long> {

    /*
    @Query("select a from AcademyMember a order by a.amNum desc")
    Page<SubjectClass> findAllPaging(Pageable pageable);

    SubjectClass findByAmNum(Long amNum);
    SubjectClass getReferenceByAmNum(Long amNum);

    List<SubjectClass> findByMembersContains(Long memNum);

    // Member getMemberByMemNum(Long memNum);

    */

}
