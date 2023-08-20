package com.jtudy.education.service;

import com.jtudy.education.DTO.AuthDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Auth;
import com.jtudy.education.entity.Member;
import com.jtudy.education.security.SecurityMember;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AuthService {

    @Transactional(readOnly = true)
    boolean validateMember(Long authId, SecurityMember member);

    Long requestAuth(Member member, Roles roles, String content);

    void modifyRequest(Member member, Roles roles, String content);

    void cancelRequest(Long authId);

    @Transactional(readOnly = true)
    AuthDTO getOne(Member member);

    @Transactional(readOnly = true)
    AuthDTO getOneByAuthId(Long authId);

    @Transactional(readOnly = true)
    Slice<AuthDTO> requestedAuths(Pageable pageable);

    void acceptAuth(String email, Roles roles);

    void rejectAuth(String email, Roles roles);

    @Transactional(readOnly = true)
    List<Roles> getRoles(Long memNum);

    void removeRoles(Long memNum, Roles roles);

    void addRoles(Long memNum, Roles roles);

    default Map<Roles, String> rolesMap(List<Roles> rolesList) {
        Map<Roles, String> rolesMap = new HashMap<>();
        rolesMap.put(Roles.STUDENT, "학생");
        rolesMap.put(Roles.MANAGER, "학원 관리자");
        rolesMap.put(Roles.ADMIN, "운영자");
        Map<Roles, String> map = new HashMap<>();
        if (rolesList == null) {
            return map;
        }
        for (Roles roles : rolesList) {
            if (rolesMap.get(roles) != null) {
                map.put(roles, rolesMap.get(roles));
            }
        }
        return map;
    }

    default AuthDTO entityToDTO(Auth auth) {
        AuthDTO authDTO = AuthDTO.builder()
                .authId(auth.getAuthId())
                .name(auth.getMember().getName())
                .email(auth.getMember().getEmail())
                .processed(auth.isProcessed())
                .roles(auth.getRoles().iterator().next())
                .createdAt(auth.getCreatedAt())
                .content(auth.getContent())
                .rolesString(rolesMap(auth.getRoles()).get(auth.getRoles().get(0)))
                .build();
        return authDTO;
    }

    default Auth DTOToEntity(AuthDTO authDTO, Member member) {
        Auth auth = new Auth(member, authDTO.getRoles(), authDTO.getContent());
        return auth;
    }

}
