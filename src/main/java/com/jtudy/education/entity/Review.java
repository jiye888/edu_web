package com.jtudy.education.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rev_num")
    private Long revNum;

    @Column
    private String title;

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Academy academy;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    private Integer grade;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Image> image = new ArrayList<>();

    public void changeReview(String title, String content, Integer grade) {
        this.title = title;
        this.content = content;
        this.grade = grade;
    }

    public void addImage(Image image) {
        this.image.add(image);
    }

    public void removeImage(Image image) {
        this.image.remove(image);
    }

}
