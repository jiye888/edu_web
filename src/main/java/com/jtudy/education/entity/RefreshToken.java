package com.jtudy.education.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 24*7*60*60*1000L)
public class RefreshToken {

    @Id
    private String refreshToken;
    private Long memNum;

    public RefreshToken(final String refreshToken, final Long memNum) {
        this.refreshToken = refreshToken;
        this.memNum = memNum;
    }

}
