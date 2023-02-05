package com.jtudy.education.security;

import com.jtudy.education.entity.RefreshToken;
import com.jtudy.education.repository.RefreshTokenRepository;
import com.jtudy.education.service.MemberService;
import groovy.util.logging.Log4j2;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import redis.embedded.RedisServer;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class RedisConfigTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private MemberService memberService;

    @Test
    public void refreshToken() {
        memberService.login("22@gmail.com", "123123123");
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findById("22@gmail.com");
        System.out.println(refreshToken);
        System.out.println(refreshToken.isPresent());

    }

    @Test
    public void connection() {

        System.setProperty("lettuce.client.debug", "true");

        RedisClient redisClient = RedisClient.create("redis://localhost:6379");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        if(connection.isOpen()) {
            System.out.println("Connected to Embedded Redis Server");
        } else {
            System.out.println("Not connected to Embedded Redis Server");
        }
        connection.close();
        redisClient.shutdown();

    }


}