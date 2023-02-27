package com.jtudy.education.repository.specification;

import com.jtudy.education.entity.Notice;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class NoticeSpecification {

    public static Specification<Notice> titleContaining(String title, Long acaNum) {
        return (root, query, builder) -> {
            Predicate titlePredicate = builder.like(builder.lower(root.get("title")), "%"+title.toLowerCase()+"%");
            Predicate numPredicate = builder.equal(root.get("academy").get("acaNum"), acaNum);
            return builder.and(titlePredicate, numPredicate);
        };
    }

    public static Specification<Notice> contentContaining(String content, Long acaNum) {
        return (root, query, builder) -> {
            Predicate contentPredicate = builder.like(builder.lower(root.get("content")), "%"+content.toLowerCase()+"%");
            Predicate numPredicate = builder.equal(root.get("academy").get("acaNum"), acaNum);
            return builder.and(contentPredicate, numPredicate);
        };
    }
}
