package com.jtudy.education.service;

import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.AcademyMember;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.AcademyMemberRepository;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.MemberRepository;
import groovy.util.logging.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class AcademyMemberServiceImplTest {

    @Autowired
    AcademyMemberService academyMemberService;

    @Test
    public void withdraw() {
        academyMemberService.withdraw(Long.parseLong("1"), Long.parseLong("5"));
    }

    @Test
    public void validate() {
        Long memNum = Long.parseLong("1");
        Long acaNum = Long.parseLong("1");
        if(academyMemberService.isPresent(memNum, acaNum)) {
            Long am = academyMemberService.getOne(memNum, acaNum);
            System.out.println("true");
            System.out.println(am);
        } else {
            System.out.println("false");
        }

    }

}