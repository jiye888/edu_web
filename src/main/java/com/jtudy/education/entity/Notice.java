package com.jtudy.education.entity;

import lombok.*;

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
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notNum;

    @Column
    private String title;

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Academy academy;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Image> image = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<FileUpload> files = new ArrayList<>();

    public void changeNotice(String title, String content) {
        this.title = title;
        this.content = content;
    }


}
