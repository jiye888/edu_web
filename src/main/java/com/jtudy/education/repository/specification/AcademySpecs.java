package com.jtudy.education.repository.specification;

import com.jtudy.education.constant.Subject;
import com.jtudy.education.entity.Academy;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

public class AcademySpecs {

    public static Specification<Academy> nameContaining(String name) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("acaName")), "%"+name.toLowerCase()+"%");
    }

    public static Specification<Academy> subjectAllContaining(List<Subject> subject) {//EnumSet<Subject> subject) {
        return (root, query, builder) -> {
            List<Predicate> predicate = new ArrayList<>();
            for (Subject sub : subject) {
                predicate.add(builder.like(root.get("subject").as(String.class), "%" + sub.name() + "%"));
            }
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }

    public static Set<String> enumSetToString(EnumSet<Subject> subjects) {
        return subjects.stream()
                .map(Subject::toString)
                .collect(Collectors.toSet());
    }


    public static Specification<Academy> locationContaining(String location) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("location")), "%"+location.toLowerCase()+"%");
    }

}
