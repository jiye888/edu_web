package com.jtudy.education.config;

import com.jtudy.education.entity.Auth;
import com.jtudy.education.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

public class AuthScheduling {

    @Autowired
    AuthRepository authRepository;

    @Scheduled(cron = "0 30 3 * * ?")
    public void clearAuthRequest() {
        LocalDateTime date = LocalDateTime.now().minusWeeks(1);
        List<Auth> authList = authRepository.findByIsProcessedAndProcessedDate(true, date);
        for (Auth auth : authList) {
            authRepository.delete(auth);
        }
    }

}
