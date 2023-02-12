package com.jtudy.education.service;

import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.DTO.MemberFormDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Academy;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.RefreshToken;
import com.jtudy.education.repository.*;
import com.jtudy.education.security.JwtTokenProvider;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final AcademyRepository academyRepository;
    private final AcademyMemberRepository academyMemberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public boolean validateMember(Long memNum, SecurityMember securityMember) {
        Member member = memberRepository.findByMemNum(memNum);
        return member.getEmail().equals(securityMember.getUsername());
    }

    @Override
    public MemberDTO getOne(Long memNum) {
        Member member = memberRepository.findByMemNum(memNum);
        MemberDTO memberDTO = entityToDTO(member);
        return memberDTO;
    }

    @Override
    public Member findOne(String email) {
        Member member = memberRepository.findByEmail(email);
        return member;
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
        member.updateMember(passwordEncoder.encode(memberFormDTO.getPassword()), memberFormDTO.getName(), memberFormDTO.getAddress());
        memberRepository.save(member);
        return member.getMemNum();
    }

    @Override
    public void withdraw(Long memNum) {
        memberRepository.deleteById(memNum);
    }

    @Override
    public String login(String email, String password) {
        UserDetails user = userDetailsService.loadUserByUsername(email);
        Member member = memberRepository.findByEmail(user.getUsername());
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("잘못된 아이디, 또는 비밀번호입니다.");
        }
        String accessToken = jwtTokenProvider.createAccessToken(email, member.getRolesList());
        String refreshToken = jwtTokenProvider.createRefreshToken(email, member.getRolesList());
        RefreshToken token = new RefreshToken(email, refreshToken);
        refreshTokenRepository.save(token);
        return accessToken;
    }

    @Override
    public void logout(String email) {
        Member member = memberRepository.findByEmail(email);
        Optional<RefreshToken> token = refreshTokenRepository.findById(email);
        if (token.isPresent()) {
            refreshTokenRepository.delete(token.get());
        }
    }

    @Override
    public Page<MemberDTO> getMembers(Long acaNum) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "acaNum"));
        List<Member> memberList = academyMemberRepository.findByAcademy(academy);
        List<MemberDTO> memberDTOList = memberList.stream().map(e -> entityToDTO(e)).collect(Collectors.toList());
        Page<MemberDTO> page = new PageImpl<>(memberDTOList, pageable, memberList.size());
        return page;
    }

    @Override
    public void requestManagerAuth(Long memNum){
        Member member = memberRepository.findByMemNum(memNum);
    }

    @Override
    public void acceptManagerAuth(Long memNum){

    }

}


