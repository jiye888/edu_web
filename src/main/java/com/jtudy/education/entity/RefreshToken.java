package com.jtudy.education.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 24*7*60*60*1000L)
public class RefreshToken {

    @Id
    private String email;
    private String refreshToken;

    public RefreshToken(final String email, final String refreshToken) {
        this.email = email;
        this.refreshToken = refreshToken;
    }

}
