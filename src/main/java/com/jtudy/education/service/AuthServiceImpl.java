package com.jtudy.education.service;

import com.jtudy.education.DTO.AuthDTO;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Auth;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.AuthRepository;
import com.jtudy.education.repository.MemberRepository;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final AuthRepository authRepository;

    @Override
    public boolean validateMember(Long authId, SecurityMember member) {
        try {
            Auth auth = authRepository.findByAuthId(authId);
            Long authNum = auth.getMember().getMemNum();
            Long memNum = member.getMember().getMemNum();
            return authNum.equals(memNum) || member.getMember().getRolesList().contains(Roles.ADMIN);
        } catch (NullPointerException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Long requestAuth(Member member, Roles roles, String content) {
        Auth auth = new Auth(member, roles, content);
        auth = authRepository.save(auth);
        System.out.println("Roles!!!!!!1"+auth);
        return auth.getAuthId();
    }

    @Override
    public void modifyRequest(Member member, Roles roles, String content) {
        Auth auth = authRepository.findByEmail(member.getEmail());
        if (auth != null) {
            auth.changeRequest(roles, content);
            authRepository.save(auth);
        }
    }

    @Override
    public void cancelRequest(Long authId) {
        authRepository.deleteById(authId);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDTO getOne(Member member) {
        Auth auth = authRepository.findByEmail(member.getEmail());
        if (auth == null) {
            return null;
        }
        AuthDTO authDTO = entityToDTO(auth);
        return authDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDTO getOneByAuthId(Long authId) {
        Auth auth = authRepository.findByAuthId(authId);
        AuthDTO authDTO = entityToDTO(auth);
        return authDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<AuthDTO> requestedAuths(Pageable pageable) {
        Slice<Auth> auth = authRepository.findByIsProcessed(false, pageable);
        Slice<AuthDTO> authDTO = auth.map(e -> entityToDTO(e));
        return authDTO;
    }

    @Override
    public void acceptAuth(String email, Roles roles){
        Member member = memberRepository.findByEmail(email);
        Auth auth = authRepository.findByEmail(email);
        if (auth != null) {
            member.addRoles(roles);
            auth.process();
        }
    }

    @Override
    public void rejectAuth(String email, Roles roles) {
        Member member = memberRepository.findByEmail(email);
        Auth auth = authRepository.findByEmail(email);
        if (auth != null) {
            auth.process();
        }
    }

    @Override
    public List<Roles> getRoles(Long memNum) {
        Member member = memberRepository.findByMemNum(memNum);
        List<Roles> roles = member.getRolesList();
        return roles;
    }

    @Override
    public void removeRoles(Long memNum, Roles roles) {
        Member member = memberRepository.findByMemNum(memNum);
        member.removeRoles(roles);
    }

    @Override
    public void addRoles(Long memNum, Roles roles) {
        Member member = memberRepository.findByMemNum(memNum);
        member.addRoles(roles);
    }

}
