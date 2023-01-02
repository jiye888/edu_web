package com.jtudy.education.repository;

import com.jtudy.education.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String username);

    Member findByMemNum(Long memNum);

}
