package com.jtudy.education.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class InfoGuide extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_num")
    private Long infoNum;

    private String title;

    private String content;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "infoGuide")
    @Builder.Default
    private List<Image> image = new ArrayList<>();

    public void addImage(Image image) {
        this.image.add(image);
    }

    public void removeImage(Image image) {
        this.image.remove(image);
    }

    public void changeInfoGuide(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
