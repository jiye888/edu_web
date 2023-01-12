package com.jtudy.education.entity;

import com.jtudy.education.security.SecurityMember;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoginUserAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication || !authentication.isAuthenticated()) {
            return null;
        }
        SecurityMember member = (SecurityMember) authentication.getPrincipal();
        return Optional.of(member.getMember().getMemNum());
    }

}
