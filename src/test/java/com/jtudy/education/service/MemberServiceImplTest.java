package com.jtudy.education.service;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.constant.Subject;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class MemberServiceImplTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AcademyRepository academyRepository;
    @Autowired
    private AcademyMemberService academyMemberService;

    @Test
    public void joinedDate() {
        List<Roles> rolesList = new ArrayList<>();
        rolesList.add(Roles.USER);
        rolesList.add(Roles.STUDENT);

        EnumSet subject = EnumSet.noneOf(Subject.class);
        subject.add(Subject.KOREAN);
        subject.add(Subject.ENGLISH);

        Academy academy = Academy.builder()
                .acaName("언어제일")
                .location("경기도 안산시")
                .subject(subject)
                .build();
        academyRepository.save(academy);

        Member member1 = Member.builder()
                .email("11@gmail.com")
                .password("12345678")
                .name("kim")
                .address("경기도 고양시")
                .rolesList(rolesList)
                .build();

        Member member2 = Member.builder()
                .email("111@gmail.com")
                .password("12345678")
                .name("kang")
                .address("인천시 서구")
                .rolesList(rolesList)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        Long am1 = academyMemberService.join(member1.getMemNum(), academy.getAcaNum());
        Long am2 = academyMemberService.join(member2.getMemNum(), academy.getAcaNum());

    }

}