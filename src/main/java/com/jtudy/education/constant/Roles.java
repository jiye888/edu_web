package com.jtudy.education.constant;

public enum Roles {
    ANONYMOUS("ROLE_ANONYMOUS"),
    USER("ROLE_USER"),
    STUDENT("ROLE_STUDENT"),
    MANAGER("ROLE_MANAGER"),
    ADMIN("ROLE_ADMIN");

    String roles;

    Roles(String roles) {
        this.roles = roles;
    }

    public String value() {
        return roles;
    }
}
