package com.jtudy.education.service;

import com.jtudy.education.DTO.MemberDTO;
import com.jtudy.education.DTO.MemberFormDTO;
import com.jtudy.education.entity.AcademyMember;
import com.jtudy.education.entity.Member;
import com.jtudy.education.security.SecurityMember;
import javassist.bytecode.DuplicateMemberException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface MemberService {

    boolean validateMember(Long memNum, SecurityMember securityMember);

    MemberDTO getOne(Long memNum);

    Member findByEmail(String email);

    Long createMember(MemberFormDTO memberFormDTO);

    void validateEmail(String email) throws DuplicateMemberException;

    Long updateMember(MemberFormDTO memberFormDTO);

    void withdraw(Long memNum);

    String login(String email, String password);

    void logout(String email);

    Page<MemberDTO> getMembers(Long acaNum, Pageable pageable);

    default MemberDTO entityToDTO(Member member) {
        MemberDTO memberDTO = MemberDTO.builder()
                .memNum(member.getMemNum())
                .email(member.getEmail())
                .name(member.getName())
                .address(member.getAddress())
                .rolesList(member.getRolesList())
                .createdAt(member.getCreatedAt())
                .build();

        return memberDTO;
    }

    default MemberDTO entityToDTO(Member member, AcademyMember am) {
        MemberDTO memberDTO = MemberDTO.builder()
                .memNum(member.getMemNum())
                .email(member.getEmail())
                .name(member.getName())
                .address(member.getAddress())
                .joinedDate(am.getCreatedAt())
                .createdAt(member.getCreatedAt())
                .build();

        return memberDTO;
    }

}
