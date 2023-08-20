package com.jtudy.education.service;

import org.springframework.transaction.annotation.Transactional;

public interface AcademyMemberService {

    Long join(Long memNum, Long acaNum);

    void withdraw(Long memNum, Long acaNum);

    @Transactional(readOnly = true)
    Long getOne(Long memNum, Long acaNum);

    @Transactional(readOnly = true)
    boolean isPresent(Long memNum, Long acaNum);
}
