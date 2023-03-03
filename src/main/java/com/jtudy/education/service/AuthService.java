package com.jtudy.education.service;

import com.jtudy.education.DTO.AuthDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Member;
import com.jtudy.education.entity.RequestAuth;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface AuthService {

    void requestAuth(String email, Roles roles, String content);

    void modifyRequest(String email, Roles roles, String content);

    AuthDTO getOne(Member member);

    Slice<AuthDTO> requestedAuths(Pageable pageable);

    void acceptAuth(String email, Roles roles);

    void rejectAuth(String email, Roles roles);

    void deleteAuth(String email, Roles roles);

    default AuthDTO entityToDTO(RequestAuth requestAuth) {
        AuthDTO authDTO = AuthDTO.builder()
                .email(requestAuth.getEmail())
                .processed(requestAuth.isProcessed())
                .roles(requestAuth.getRoles())
                .createdAt(requestAuth.getCreatedAt())
                .content(requestAuth.getContent())
                .build();
        return authDTO;
    }

    default RequestAuth DTOToEntity(AuthDTO authDTO) {
        RequestAuth requestAuth = new RequestAuth(authDTO.getEmail(), authDTO.getRoles(), authDTO.getContent());
        return  requestAuth;
    }

}
