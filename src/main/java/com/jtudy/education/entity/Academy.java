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

    @Column
    private String location;

    @OneToMany(mappedBy = "academy", orphanRemoval = true)
    @Builder.Default
    private List<AcademyMember> academyMember = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Review> review = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Notice> notice = new ArrayList<>();

    @OneToOne(orphanRemoval = true)
    private FileUpload file;

    public void changeAcademy(String acaName, EnumSet<Subject> subject, String location) {
        this.acaName = acaName;
        this.subject = subject;
        this.location = location;
    }

    public void setFile(FileUpload file) {
        this.file = file;
    }

}
