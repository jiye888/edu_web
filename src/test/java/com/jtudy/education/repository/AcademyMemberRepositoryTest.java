package com.jtudy.education.repository;

import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.AcademyMember;
import com.jtudy.education.entity.Member;
import groovy.util.logging.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class AcademyMemberRepositoryTest {

    @Autowired
    private AcademyMemberRepository repository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AcademyRepository academyRepository;

    @Test
    public void memberList() {
        Academy academy = academyRepository.findByAcaNum(Long.parseLong("1"));
        Member member = memberRepository.findByMemNum(Long.parseLong("1"));
        AcademyMember academyMember = AcademyMember.builder()
                .academy(academy)
                .member(member)
                .build();
        repository.save(academyMember);
        List<AcademyMember> academyMemberList = repository.findByAcademy(academy);
        List<Member> memberList = academyMemberList.stream().map(e -> e.getMember()).collect(Collectors.toList());
        System.out.println("member list" + memberList);
    }

}