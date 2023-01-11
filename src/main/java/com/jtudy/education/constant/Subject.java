package com.jtudy.education.constant;

public enum Subject {
    KOREAN("국어"),
    ENGLISH("영어"),
    MATH("수학"),
    SOCIOLOGY("사회"),
    SCIENCE("과학"),
    LANGUAGE("외국어"),
    PE("체육"),
    ART("미술"),
    MUSIC("음악");

    private String description;

    Subject(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
