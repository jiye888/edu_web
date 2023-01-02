package com.jtudy.education.service;

import com.jtudy.education.DTO.LoginFormDTO;
import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.*;
import com.jtudy.education.security.JwtTokenProvider;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final AcademyMemberRepository academyMemberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("<" + email + "> 사용자를 찾을 수 없습니다.");
        }
        SecurityMember securityMember = new SecurityMember(member);
        return securityMember;
    }

    public MemberDTO entityToDTO(Member member) {
        MemberDTO memberDTO = MemberDTO.builder()
                .memNum(member.getMemNum())
                .email(member.getEmail())
                .name(member.getName())
                .address(member.getAddress())
                .build();

        return memberDTO;
    }

    public Member createMember(MemberDTO memberDTO, PasswordEncoder passwordEncoder) {
        Member member = Member.builder()
                .email(memberDTO.getEmail())
                //.password(passwordEncoder.encode(memberDTO.getPassword()))
                .name(memberDTO.getName())
                .address(memberDTO.getAddress())
                .build();

        member.addRoles(Roles.USER);

        return member;
    }

    public Long updateMember(MemberDTO memberDTO, PasswordEncoder passwordEncoder) {
        Member member = memberRepository.findByEmail(memberDTO.getEmail());
        //member.updateMember(memberDTO.getPassword(), memberDTO.getName(), memberDTO.getAddress(), passwordEncoder);
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

    public String login(LoginFormDTO member) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserDetails user = loadUserByUsername(member.getEmail());
        Member account = memberRepository.findByEmail(user.getUsername());
        if (!passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 아이디, 또는 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(member.getEmail(), account.getRolesList());

    }


    public Page<MemberDTO> getMembers(Academy academy) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "acaNum"));
        List<Member> memberList = academyMemberRepository.findByAcademy(academy);
        memberList.stream().map(e -> entityToDTO(e)).collect(Collectors.toList());
        PageImpl page = new PageImpl(memberList, pageable, memberList.size());
        return page;
    }

}


