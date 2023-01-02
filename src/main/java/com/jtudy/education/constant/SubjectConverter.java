package com.jtudy.education.constant;

import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.EnumSet;

@Converter
public class SubjectConverter implements AttributeConverter<EnumSet<Subject>, String> {

    @Override
    public String convertToDatabaseColumn(EnumSet<Subject> attribute) {
        StringBuilder sb = new StringBuilder();
        attribute.stream().forEach(e -> sb.append(e.name()+","));
        String result = sb.substring(0, sb.length() -1);
        return result;
    }

    @Override
    public EnumSet<Subject> convertToEntityAttribute(String dbData) {
        EnumSet<Subject> attribute = EnumSet.noneOf(Subject.class);
        String[] strings = StringUtils.trimAllWhitespace(dbData).toUpperCase().split(",");
        Arrays.stream(strings).forEach(e -> attribute.add(Subject.valueOf(e)));
        return attribute;
    }

}
