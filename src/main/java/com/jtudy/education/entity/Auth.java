package com.jtudy.education.entity;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.constant.RolesConverter;
import com.jtudy.education.constant.SubjectConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    private boolean isProcessed;

    @ManyToOne
    private Member member;

    @Convert(converter = RolesConverter.class)
    @Builder.Default
    private List<Roles> roles = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    private String content;

    public Auth(Member member, Roles roles, String content) {
        this.member = member;
        this.roles = new ArrayList<>();
        this.roles.add(roles);
        this.content = content;
        this.isProcessed = false;
    }

    public void changeRequest(Roles roles, String content) {
        this.roles.clear();
        this.roles.add(roles);
        this.content = content;
    }

    public void process() {
        this.isProcessed = true;
    }

}
