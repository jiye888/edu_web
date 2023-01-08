package com.jtudy.education.repository;

import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.AcademyMember;
import com.jtudy.education.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademyMemberRepository extends JpaRepository<AcademyMember, Long> {

    List<Academy> findByMember(Member member);
    List<Member> findByAcademy(Academy academy);
}
