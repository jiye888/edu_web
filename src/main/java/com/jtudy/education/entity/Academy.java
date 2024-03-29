package com.jtudy.education.entity;

import com.jtudy.education.constant.Subject;
import com.jtudy.education.constant.SubjectConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Academy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aca_num")
    private Long acaNum;

    @Column(name = "aca_name")
    private String acaName;

    @Convert(converter = SubjectConverter.class)
    private EnumSet<Subject> subject;

    @ManyToOne
    private Member manager;

    private String location;

    private String introduction;

    @OneToMany(mappedBy = "academy", orphanRemoval = true)
    @Builder.Default
    private List<AcademyMember> academyMember = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "academy")
    @Builder.Default
    private List<Review> review = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "academy")
    @Builder.Default
    private List<Notice> notice = new ArrayList<>();

    @OneToOne
    private Image image;

    public void changeAcademy(String acaName, EnumSet<Subject> subject, String location, String introduction) {
        this.acaName = acaName;
        this.subject = subject;
        this.location = location;
        this.introduction = introduction;
    }

    public void removeNotices() {
        this.notice = null;
    }

    public void removeReviews() {
        this.review = null;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
