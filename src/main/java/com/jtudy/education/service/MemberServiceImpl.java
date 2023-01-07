package com.jtudy.education.service;

import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.DTO.MemberFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.*;
import com.jtudy.education.security.JwtTokenProvider;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberServiceImpl {

    private final MemberRepository memberRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final AcademyMemberRepository academyMemberRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    public MemberDTO entityToDTO(Member member) {
        MemberDTO memberDTO = MemberDTO.builder()
                .memNum(member.getMemNum())
                .email(member.getEmail())
                .name(member.getName())
                .address(member.getAddress())
                .build();

        return memberDTO;
    }

    public Member createMember(MemberFormDTO memberFormDTO, PasswordEncoder passwordEncoder) {
        Member member = Member.builder()
                .email(memberFormDTO.getEmail())
                .password(passwordEncoder.encode(memberFormDTO.getPassword()))
                .name(memberFormDTO.getName())
                .address(memberFormDTO.getAddress())
                .build();

        member.addRoles(Roles.USER);

        return member;
    }

    public Long updateMember(MemberFormDTO memberFormDTO, PasswordEncoder passwordEncoder) {
        Member member = memberRepository.findByEmail(memberFormDTO.getEmail());
        member.updateMember(memberFormDTO.getPassword(), memberFormDTO.getName(), memberFormDTO.getAddress(), passwordEncoder);
        return member.getMemNum();
    }

    public void withdraw(Long memNum) {
        Member member = memberRepository.findByMemNum(memNum);
        SecurityMember securityMember = new SecurityMember(member);
/*
        if (securityMember.getRolesList().contains("MANAGER")) {
            List<Academy> academy = academyRepository.findByManager(member);
            for (Academy aca : academy) {
                noticeRepository.deleteByAcademy(aca);
                academyRepository.delete(aca);
            }
        } else if (securityMember.getRolesList().contains("STUDENT")){
            reviewRepository.deleteByWriter(member);
            List<AcademyMember> academyMember = academyMemberRepository.findByMembersContains(memNum);
            for (AcademyMember am : academyMember) {
                am.removeMember(member);
            }
        }
 */
        memberRepository.deleteById(memNum);
    }

    public String login(String email, String password) {

        UserDetails user = userDetailsService.loadUserByUsername(email);
        Member account = memberRepository.findByEmail(user.getUsername());
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("잘못된 아이디, 또는 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(email, account.getRolesList());

    }
    
    public Page<MemberDTO> getMembers(Academy academy) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "acaNum"));
        List<Member> memberList = academyMemberRepository.findByAcademy(academy);
        memberList.stream().map(e -> entityToDTO(e)).collect(Collectors.toList());
        PageImpl page = new PageImpl(memberList, pageable, memberList.size());
        return page;
    }
    
    public void managerAuth(Long memNum) {
        Member member = memberRepository.findByMemNum(memNum);
        member.addRoles(Roles.MANAGER);
    }
    // 먼저 manager로 처리가 되어야 academy create 가능
    
    public void studentAuth(Long memNum) {
        Member member = memberRepository.findByMemNum(memNum);
        member.addRoles(Roles.STUDENT);
    }
    // academy join 시에 student role 없으면 추가

}


