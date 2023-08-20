package com.jtudy.education.service;

import com.jtudy.education.DTO.AuthDTO;
import com.jtudy.education.config.exception.CustomException;
import com.jtudy.education.config.exception.ExceptionCode;
import com.jtudy.education.config.exception.GlobalExceptionHandler;
import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.Auth;
import com.jtudy.education.entity.Member;
import com.jtudy.education.repository.AuthRepository;
import com.jtudy.education.repository.MemberRepository;
import com.jtudy.education.security.SecurityMember;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final AuthRepository authRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public boolean validateMember(Long authId, SecurityMember member) {
        try {
            Auth auth = authRepository.findByAuthId(authId);
            Long authNum = auth.getMember().getMemNum();
            Long memNum = member.getMember().getMemNum();
            if (authNum.equals(memNum) || member.getMember().getRolesList().contains(Roles.ADMIN)) {
                return true;
            } else {
                throw new CustomException(ExceptionCode.UNAUTHORIZED_USER);
            }
        } catch (NullPointerException e) {
            return false;
        } catch (Exception e) {
            logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
            return false;
        }
    }

    @Override
    public Long requestAuth(Member member, Roles roles, String content) {
        Auth auth = new Auth(member, roles, content);
        auth = authRepository.save(auth);
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
        Slice<AuthDTO> authDTO = auth.map(e -> {
            try {
                return entityToDTO(e);
            } catch (Exception exc) {
                logger.error(GlobalExceptionHandler.exceptionStackTrace(exc));
                return null;
            }
        });
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
        Auth auth = authRepository.findByEmail(email);
        if (auth != null) {
            auth.process();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Roles> getRoles(Long memNum) {
        Member member = memberRepository.findByMemNum(memNum);
        List<Roles> roles = member.getRolesList();
        return roles;
    }

    @Override
    public void removeRoles(Long memNum, Roles roles) {
        Member member = memberRepository.findByMemNum(memNum);
        try {
            member.removeRoles(roles);
        } catch (IndexOutOfBoundsException e) {
            logger.error(GlobalExceptionHandler.exceptionStackTrace(e));
            return;
        }
    }

    @Override
    public void addRoles(Long memNum, Roles roles) {
        Member member = memberRepository.findByMemNum(memNum);
        member.addRoles(roles);
    }

}
