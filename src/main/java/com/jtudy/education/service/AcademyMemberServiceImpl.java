package com.jtudy.education.service;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.AcademyMember;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.AcademyMemberRepository;
import com.jtudy.education.repository.AcademyRepository;
import com.jtudy.education.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AcademyMemberServiceImpl implements AcademyMemberService {

    private final AcademyRepository academyRepository;
    private final MemberRepository memberRepository;
    private final AcademyMemberRepository academyMemberRepository;
    private final MemberService memberService;

    @Override
    public Long join(Long memNum, Long acaNum) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        Member member = memberRepository.findByMemNum(memNum);
        AcademyMember academyMember = AcademyMember.builder()
                .academy(academy)
                .member(member)
                .build();
        if(!isPresent(memNum, acaNum)) {
            academyMemberRepository.save(academyMember);
            if (!member.getRolesList().contains(Roles.STUDENT)) {
                member.addRoles(Roles.STUDENT);
            }
            memberRepository.save(member);
            return academyMember.getAmNum();
        } else {
            return getOne(memNum, acaNum);
        }
    }

    @Override
    public void withdraw(Long memNum, Long acaNum) {
        Member member = memberRepository.findByMemNum(memNum);
        if (isPresent(memNum, acaNum)) {
            Long am = getOne(memNum, acaNum);
            academyMemberRepository.deleteById(am);
            if (academyMemberRepository.findByMember(member) == null) {
                member.removeRoles(Roles.STUDENT);
            }
        }
    }

    @Override
    public Long getOne(Long memNum, Long acaNum) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        Member member = memberRepository.findByMemNum(memNum);
        Optional<AcademyMember> am = academyMemberRepository.findByAcademyAndMember(academy, member);
        AcademyMember academyMember = am.get();
        return academyMember.getAmNum();
    }

    @Override
    public boolean isPresent(Long memNum, Long acaNum) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        Member member = memberRepository.findByMemNum(memNum);
        Optional<AcademyMember> am = academyMemberRepository.findByAcademyAndMember(academy, member);
        return am.isPresent();
    }

}