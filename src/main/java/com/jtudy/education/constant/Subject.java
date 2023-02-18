package com.jtudy.education.constant;

import java.util.EnumSet;

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

    @Override
    public String toString() {
        return this.name();
    }

    private String description;

    Subject(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public static String enumSetToString(EnumSet<Subject> subjects) {
        StringBuilder sb = new StringBuilder();
        for (Subject subject : subjects) {
            sb.append(subject.toString()).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

}
