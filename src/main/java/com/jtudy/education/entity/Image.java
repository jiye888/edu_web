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

    @Column(name = "image_original_name", nullable = false)
    private String originalName;

    @Column(name = "image_name", nullable = false)
    private String name;

    @Column(name = "image_path", nullable = false)
    private String path;

    @ManyToOne
    private Member uploader;

    @Column(name = "image_order")
    private Integer order;

    @Column(name = "image_index")
    private String index;

    @OneToOne
    private Academy academy;

    @ManyToOne
    private Notice notice;

    @ManyToOne
    private Review review;

    public void setAcademy(Academy academy) {
        this.academy = academy;
    }

    public void setNotice(Notice notice, Integer order, String index) {
        this.notice = notice;
        this.order = order;
        this.index = index;
    }

    public void setReview(Review review, Integer order, String index) {
        this.review = review;
        this.order = order;
        this.index = index;
    }

    public void changeInfo(Integer order, String index) {
        this.order = order;
        this.index = index;
    }

}
