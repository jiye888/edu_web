package com.jtudy.education.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileUpload extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @ManyToOne
    private Member uploader;

    @ManyToOne
    private Notice notice;

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public void changeOriginalName(String name) {
        this.originalName = name;
    }

}
