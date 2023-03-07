package com.jtudy.education.entity;

import com.jtudy.education.constant.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    private boolean isProcessed;

    @ManyToOne
    private Member member;

    private Roles roles;

    @CreatedDate
    private LocalDateTime createdAt;

    private String content;

    public Auth(Member member, Roles roles, String content) {
        this.member = member;
        this.roles = roles;
        this.content = content;
        this.isProcessed = false;
    }

    public void changeRequest(Roles roles, String content) {
        this.roles = roles;
        this.content = content;
    }

    public void process() {
        this.isProcessed = true;
    }

}
