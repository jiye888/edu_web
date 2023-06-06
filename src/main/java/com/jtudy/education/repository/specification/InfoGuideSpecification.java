package com.jtudy.education.repository.specification;

import com.jtudy.education.entity.InfoGuide;
import org.springframework.data.jpa.domain.Specification;

public class InfoGuideSpecification {

    public static Specification<InfoGuide> titleContaining(String title) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("title")), "%"+title.toLowerCase()+"%");
    }

    public static Specification<InfoGuide> contentContaining(String content) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("content")), "%"+content.toLowerCase()+"%");
    }

}
