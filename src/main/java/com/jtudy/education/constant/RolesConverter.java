package com.jtudy.education.constant;

import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@Converter
public class RolesConverter implements AttributeConverter<List<Roles>, String> {

    @Override
    public String convertToDatabaseColumn(List<Roles> attribute) {
        StringBuilder sb = new StringBuilder();
        attribute.stream().forEach(e -> sb.append(e.name()+","));
        String result = sb.substring(0, sb.length() -1).toString();
        return result;
    }

    @Override
    public List<Roles> convertToEntityAttribute(String dbData) {
        List<Roles> attribute = new ArrayList<>();
        String[] strings = StringUtils.trimAllWhitespace(dbData).toUpperCase().split(",");
        Arrays.stream(strings).forEach(e -> attribute.add(Roles.valueOf(e)));
        return attribute;
    }

}
