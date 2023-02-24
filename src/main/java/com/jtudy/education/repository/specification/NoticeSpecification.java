package com.jtudy.education.repository.specification;

import com.jtudy.education.entity.Notice;
import org.springframework.data.jpa.domain.Specification;

public class NoticeSpecification {

    public static Specification<Notice> titleContaining(String title) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("title")), "%"+title.toLowerCase()+"%");
    }

    public static Specification<Notice> contentContaining(String content) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("content")), "%"+content.toLowerCase()+"%");
    }
}
