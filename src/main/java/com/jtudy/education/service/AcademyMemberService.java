package com.jtudy.education.service;

public interface AcademyMemberService {

    Long join(Long memNum, Long acaNum);

    void withdraw(Long memNum, Long acaNum);

    Long getOne(Long memNum, Long acaNum);

    boolean isPresent(Long memNum, Long acaNum);
}
