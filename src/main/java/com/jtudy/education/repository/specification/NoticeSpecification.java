package com.jtudy.education.repository.specification;

import com.jtudy.education.entity.Notice;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

public class NoticeSpecification {

    public static Specification<Notice> titleContaining(String title) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("title")), "%"+title.toLowerCase()+"%");
    }

    public static Specification<Notice> contentContaining(String content) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("content")), "%"+content.toLowerCase()+"%");
    }

    public static Specification<Notice> findByAcademy(Long acaNum) {
        return (root, query, builder) -> builder.equal(root.get("academy").get("acaNum"), acaNum);
    }
}
