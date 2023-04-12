package com.jtudy.education.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

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
    @Size(min = 20)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Academy academy;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<FileUpload> files = new ArrayList<>();

    public void changeNotice(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
