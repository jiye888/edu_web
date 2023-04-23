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

    private Integer position;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Lob
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;

    @ManyToOne
    private Member uploader;

    @OneToOne
    private Academy academy;

    @ManyToOne
    private Notice notice;

    @ManyToOne
    private Review review;

    public void setAcademy(Academy academy) {
        this.academy = academy;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public void setPosition(Integer position) {this.position = position;}

}
