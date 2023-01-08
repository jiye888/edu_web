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
import org.springframework.beans.factory.annotation.Autowired;
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
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final AcademyMemberRepository academyMemberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member createMember(MemberFormDTO memberFormDTO) {
        Member member = Member.builder()
                .email(memberFormDTO.getEmail())
                .password(passwordEncoder.encode(memberFormDTO.getPassword()))
                .name(memberFormDTO.getName())
                .address(memberFormDTO.getAddress())
                .build();

        member.addRoles(Roles.USER);

        return member;
    }

    @Override
    public Long updateMember(MemberFormDTO memberFormDTO) {
        Member member = memberRepository.findByEmail(memberFormDTO.getEmail());
        member.updateMember(memberFormDTO.getPassword(), memberFormDTO.getName(), memberFormDTO.getAddress(), passwordEncoder);
        return member.getMemNum();
    }

    @Override
    public void withdraw(Long memNum) {
        Member member = memberRepository.findByMemNum(memNum);
        SecurityMember securityMember = new SecurityMember(member);
        memberRepository.deleteById(memNum);
    }

    @Override
    public String login(String email, String password, PasswordEncoder passwordEncoder) {

        UserDetails user = userDetailsService.loadUserByUsername(email);
        Member account = memberRepository.findByEmail(user.getUsername());
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("잘못된 아이디, 또는 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(email, account.getRolesList());

    }

    @Override
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


