package com.jtudy.education.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "image_name", nullable = false)
    private String name;

    @Column(name = "image_path", nullable = false)
    private String path;

    @ManyToOne
    private Member uploader;

    @Column(name = "image_index")
    private String index;

    @Column(name = "pre_text")
    private String preText;

    @Column(name = "post_text")
    private String postText;

    @OneToOne
    private Academy academy;

    @ManyToOne
    private Notice notice;

    @ManyToOne
    private Review review;

    public void setAcademy(Academy academy) {
        this.academy = academy;
    }

    public void setNotice(Notice notice, String index, String preText, String postText) {
        this.notice = notice;
        this.index = index;
        this.preText = preText;
        this.postText = postText;
    }

    public void setReview(Review review, String index, String preText, String postText) {
        this.review = review;
        this.index = index;
        this.preText = preText;
        this.postText = postText;
    }

    public Image changeInfo(String index, String preText, String postText) {
        this.index = index;
        this.preText = preText;
        this.postText = postText;
        return this;
    }

    public void changeOriginalName(String name) {
        this.originalName = name;
    }

}
