package com.jtudy.education.service;

import com.jtudy.education.DTO.AuthDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Auth;
import com.jtudy.education.entity.Member;
import com.jtudy.education.security.SecurityMember;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {

    boolean validateMember(Long authId, SecurityMember member);

    Long requestAuth(Member member, Roles roles, String content);

    void modifyRequest(Member member, Roles roles, String content);

    void cancelRequest(Long authId);

    AuthDTO getOne(Member member);

    @Transactional(readOnly = true)
    AuthDTO getOneByAuthId(Long authId);

    Slice<AuthDTO> requestedAuths(Pageable pageable);

    void acceptAuth(String email, Roles roles);

    void rejectAuth(String email, Roles roles);

    void deleteAuth(String email, Roles roles);

    default AuthDTO entityToDTO(Auth auth) {
        AuthDTO authDTO = AuthDTO.builder()
                .authId(auth.getAuthId())
                .name(auth.getMember().getName())
                .email(auth.getMember().getEmail())
                .processed(auth.isProcessed())
                .roles(auth.getRoles())
                .createdAt(auth.getCreatedAt())
                .content(auth.getContent())
                .build();
        return authDTO;
    }

    default Auth DTOToEntity(AuthDTO authDTO, Member member) {
        Auth auth = new Auth(member, authDTO.getRoles(), authDTO.getContent());
        return auth;
    }

}
