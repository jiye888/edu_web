package com.jtudy.education.repository;

import com.jtudy.education.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String username);

    Member findByMemNum(Long memNum);

    boolean existsByEmail(String email);

}
