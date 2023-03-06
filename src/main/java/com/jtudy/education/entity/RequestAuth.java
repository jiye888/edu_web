package com.jtudy.education.entity;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.constant.RolesConverter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Convert;
import java.time.LocalDateTime;
import java.util.SortedSet;

@Getter
@RedisHash(value = "requestAuth")
public class RequestAuth {

    @Id
    private String email;

    private SortedSet<Integer> processed;

    private Roles roles;

    @CreatedDate
    private LocalDateTime createdAt;

    private String content;

    public RequestAuth(String email, Roles roles, String content) {
        this.email = email;
        this.processed.add(0);
        this.roles = roles;
        this.createdAt = null;
        this.content = content;
    }

    public void modifyRequest(Roles roles, String content) {
        this.roles = roles;
        this.content = content;
    }

    public String acceptAuth(String email, Roles roles) {
        this.email = email;
        this.processed.add(1);
        this.roles = roles;

        return email;
    }

    public void process(boolean processed) {
        this.processed.clear();
        this.processed.add(1);
    }

}
