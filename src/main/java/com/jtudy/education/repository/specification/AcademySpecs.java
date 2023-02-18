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
        /*return (root, query, builder) -> {
            EnumSet<Subject> enumSet = EnumSet.noneOf(Subject.class);
            Path<Subject> subjectPath = root.get("subject");
            if (subjectPath.getJavaType().equals(EnumSet.class)) {
                for (String sub : subject) {
                    enumSet.add(Subject.valueOf(sub));
                }
                System.out.println("Enum Set: "+enumSet);
                Predicate predicate = subjectPath.in(enumSet);
                System.out.println("Predicate: "+predicate);
                return predicate;
            } else {
                return builder.disjunction();
            }
        };/*
        return (root, query, builder) -> {
            //Predicate predicate = root.in(root.get("subject"));
            List<Predicate> predicate = new ArrayList<>();
            for (Subject sub : subject) {
                predicate.add(root.get("subject").in(sub));
                //predicate.equals(sub);
                //predicate.like;
                System.out.println(sub);
                System.out.println("type: "+sub.getClass());
            }
            return builder.and(predicate.toArray(new Predicate[0]));
            //return builder.and(predicate);
        };
        return (root, query, builder) -> {
            List<Predicate> predicate = new ArrayList<>();
            for (Subject sub : subject) {
                predicate.add(builder.like(root.get("subject"), sub.toString()));

            }
            return builder.and(predicate.toArray(new Predicate[0]));
        };
        return (root, query, builder) -> {
            List<Expression<Boolean>> expressions = new ArrayList<>();
            for (Subject sub : subject) {
                expressions.add(root.get("subject").as(EnumSet.class).in(sub));
            }
            return builder.and(expressions.toArray(new Predicate[0]));
        };*/
        return (root, query, builder) -> {
            List<Predicate> predicate = new ArrayList<>();
            for (Subject s : subject) {
                predicate.add(builder.like(root.get("subject").as(String.class), "%" + s.toString() + "%"));
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
