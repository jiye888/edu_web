package com.jtudy.education.security;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private Long tokenValidTime = 6 * 60 * 60 * 1000L; // 6시간

    private final UserDetailsServiceImpl userDetailsService;

    StringKeyGenerator keyGenerator = KeyGenerators.string();
    String secretKey = keyGenerator.generateKey();

/*
    private static SecretKey getSecureRandomKey(String cipher, int keySize) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomKeyBytes = new byte[keySize];
        secureRandom.nextBytes(randomKeyBytes);
        return new SecretKeySpec(randomKeyBytes, cipher);

    }
    SecretKey secretKey = getSecureRandomKey("AES", 8);
    String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
 */

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String email, List<Roles> roles) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        SecurityMember securityMember = userDetailsService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(securityMember, "", securityMember.getAuthorities());
    }

    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    //Jws? Jwts?

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

}
