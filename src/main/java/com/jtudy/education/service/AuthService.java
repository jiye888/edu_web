package com.jtudy.education.service;

import com.jtudy.education.DTO.AuthDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.RequestAuth;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface AuthService {

    void requestAuth(String email, Roles roles);

    AuthDTO getOne(Member member, Roles roles);

    Slice<RequestAuth> requestedAuths(Pageable pageable);

    void acceptAuth(String email, Roles roles);

    void rejectAuth(String email, Roles roles);

    void deleteAuth(String email, Roles roles);

    default AuthDTO entityToDTO(RequestAuth requestAuth, Member member) {
        AuthDTO authDTO = AuthDTO.builder()
                .name(member.getName())
                .email(requestAuth.getEmail())
                .processed(requestAuth.isProcessed())
                .roles(requestAuth.getRoles())
                .build();
        return authDTO;
    }

    default RequestAuth DTOToEntity(AuthDTO authDTO) {
        RequestAuth requestAuth = RequestAuth.builder()
                .email(authDTO.getEmail())
                .roles(authDTO.getRoles())
                .build();
        return  requestAuth;
    }

}
