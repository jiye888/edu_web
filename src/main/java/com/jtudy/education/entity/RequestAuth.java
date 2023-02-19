package com.jtudy.education.entity;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.constant.RolesConverter;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Convert;

@Getter
@Builder
@RedisHash(value = "requestAuth")
public class RequestAuth {

    @Id
    private String email;

    private boolean processed;

    private Roles roles;

    public RequestAuth(String email, Roles roles) {
        this.email = email;
        this.processed = false;
        this.roles = roles;
    }

    public String acceptAuth(String email, Roles roles) {
        this.email = email;
        this.processed = true;
        this.roles = roles;

        return email;
    }

    public void process(boolean processed) {
        this.processed = processed;
    }

}
