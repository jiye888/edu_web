package com.jtudy.education.service;

import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.DTO.MemberFormDTO;
import com.jtudy.education.config.exception.CustomException;
import com.jtudy.education.config.exception.ExceptionCode;
import com.jtudy.education.config.exception.GlobalExceptionHandler;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.*;
import com.jtudy.education.repository.*;
import com.jtudy.education.security.JwtTokenProvider;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final AuthRepository authRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public boolean validateMember(Long memNum, SecurityMember securityMember) {
        try {
            Member member = memberRepository.findByMemNum(memNum);
            return member.getEmail().equals(securityMember.getUsername()) || securityMember.getMember().getRolesList().contains(Roles.ADMIN);
        } catch (NullPointerException e) {
            return false;
        } catch (Exception e) {
            logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO getOne(Long memNum) {
        Member member = memberRepository.findByMemNum(memNum);
        MemberDTO memberDTO = entityToDTO(member);
        return memberDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDTO findByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member != null) {
            MemberDTO memberDTO = entityToDTO(member);
            return memberDTO;
        } else {
            throw new CustomException(ExceptionCode.INVALID_USERNAME);
        }
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
        memberRepository.save(member);
        return member.getMemNum();
    }

    @Override
    public void validateEmail(String email) {
        if(memberRepository.existsByEmail(email)) {
            throw new CustomException(ExceptionCode.DUPLICATE_USERNAME);
        }
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
        Member member = memberRepository.findByMemNum(memNum);
        Auth auth = authRepository.findByEmail(member.getEmail());
        if (auth != null) {
            authRepository.deleteById(auth.getAuthId());
        }
        memberRepository.deleteById(memNum);
        SecurityContextHolder.clearContext();
    }

    @Override
    public String login(String email, String password) {
        try {
            UserDetails user = userDetailsService.loadUserByUsername(email);
            Member member = memberRepository.findByEmail(user.getUsername());
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new CustomException(ExceptionCode.NOT_MATCHED_LOGIN_INFO);
            }
            String accessToken = jwtTokenProvider.createAccessToken(email, member.getRolesList());
            String refreshToken = jwtTokenProvider.createRefreshToken(email, member.getRolesList());
            RefreshToken token = new RefreshToken(email, refreshToken);
            refreshTokenRepository.save(token);
            return accessToken;
        } catch (UsernameNotFoundException e) {
            throw new CustomException(ExceptionCode.NOT_MATCHED_LOGIN_INFO);
        }
    }

    @Override
    public void logout(String email) {
        Optional<RefreshToken> token = refreshTokenRepository.findById(email);
        if (token.isPresent()) {
            refreshTokenRepository.delete(token.get());
        }
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberDTO> getMembers(Long acaNum, Pageable pageable) {
        Academy academy = academyRepository.findByAcaNum(acaNum);
        List<AcademyMember> academyMemberList = academyMemberRepository.findByAcademy(academy);
        List<Member> memberList = academyMemberList.stream().map(e -> e.getMember()).collect(Collectors.toList());
        List<MemberDTO> memberDTOList = memberList.stream().map(e -> entityToDTO(e, e.getAcademyMemberByAcademy(academy))).collect(Collectors.toList());
        Page<MemberDTO> page = new PageImpl<>(memberDTOList, pageable, memberList.size());
        return page;
    }
}


