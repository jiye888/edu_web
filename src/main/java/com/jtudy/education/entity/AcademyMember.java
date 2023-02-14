package com.jtudy.education.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AcademyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "am_num")
    private Long amNum;

    @ManyToOne
    private Academy academy;

    @ManyToOne
    private Member member;

    @CreatedBy
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

}
