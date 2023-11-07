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
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Academy academy;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "notice")
    @Builder.Default
    private List<Image> image = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "notice")
    @Builder.Default
    private List<FileUpload> files = new ArrayList<>();

    public void changeNotice(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addFile(FileUpload fileUpload) {
        this.files.add(fileUpload);
    }

    public void removeFile(FileUpload fileUpload) {
        this.files.remove(fileUpload);
    }

    public void addImage(Image image) {
        this.image.add(image);
    }

    public void removeImage(Image image) {
        this.image.remove(image);
    }

    public void detach() {
        this.academy = null;
    }

}
