package com.jtudy.education.service;

public interface AcademyMemberService {

    Long join(Long memNum, Long acaNum);

    void withdraw(Long memNum, Long acaNum);

}
