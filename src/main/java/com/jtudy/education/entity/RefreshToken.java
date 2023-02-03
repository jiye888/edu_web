package com.jtudy.education.entity;

import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@RedisHash("refreshToken")
public class RefreshToken {

    @Id
    private String email;

}
