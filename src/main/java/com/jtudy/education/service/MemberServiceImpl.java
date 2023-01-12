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
    public MemberDTO getOne(Long memNum) {
        Member member = memberRepository.findByMemNum(memNum);
        MemberDTO memberDTO = entityToDTO(member);
        return memberDTO;
    }

    @Override
    public Long createMember(MemberFormDTO memberFormDTO) {
        Member member = Member.builder()
                .email(memberFormDTO.getEmail())
                .password(passwordEncoder.encode(memberFormDTO.getPassword()))
                .name(memberFormDTO.getName())
                .address(memberFormDTO.getAddress())
                .build();
        member.addRoles(Roles.USER);
        try{
            if(memberRepository.existsByEmail(member.getEmail())) {
                throw new Exception("사용중인 이메일입니다.");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        memberRepository.save(member);
        return member.getMemNum();
    }

    @Override
    public Long updateMember(MemberFormDTO memberFormDTO) {
        Member member = memberRepository.findByEmail(memberFormDTO.getEmail());
        member.updateMember(memberFormDTO.getPassword(), memberFormDTO.getName(), memberFormDTO.getAddress(), passwordEncoder);
        return member.getMemNum();
    }

    @Override
    public void withdraw(Long memNum) {
        //Member member = memberRepository.findByMemNum(memNum);
        memberRepository.deleteById(memNum);
    }

    @Override
    public String login(String email, String password) {

        UserDetails user = userDetailsService.loadUserByUsername(email);
        Member member = memberRepository.findByEmail(user.getUsername());
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("잘못된 아이디, 또는 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(email, member.getRolesList());

    }

    @Override
    public Page<MemberDTO> getMembers(Academy academy) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "acaNum"));
        List<Member> memberList = academyMemberRepository.findByAcademy(academy);
        List<MemberDTO> memberDTOList = memberList.stream().map(e -> entityToDTO(e)).collect(Collectors.toList());
        PageImpl<MemberDTO> page = new PageImpl<>(memberDTOList, pageable, memberList.size());
        return page;
    }

}


