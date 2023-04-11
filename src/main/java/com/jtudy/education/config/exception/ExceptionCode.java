package com.jtudy.education.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionCode {

    INVALID_USERNAME(404, "올바르지 않은 사용자입니다."),
    DUPLICATE_USERNAME(409, "사용중인 이메일입니다."),
    NOT_MATCHED_LOGIN_INFO(401, "잘못된 아이디, 또는 비밀번호입니다."),
    UNAUTHORIZED_USER(401, "권한이 없습니다."),
    NOT_MANAGER(403, "관리자 권한이 없습니다.");

    private int status;
    private String message;

}
