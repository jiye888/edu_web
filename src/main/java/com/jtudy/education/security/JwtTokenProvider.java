package com.jtudy.education.security;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.RefreshToken;
import com.jtudy.education.repository.RefreshTokenRepository;
import com.jtudy.education.service.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.management.relation.Role;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.SignatureException;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private Long accessTokenValidTime = 60 * 60 * 1000L;
    private Long refreshTokenValidTime = 24 * 7 * 60 * 60 * 1000L;

    private final UserDetailsServiceImpl userDetailsService;

    private final RefreshTokenRepository refreshTokenRepository;

    StringKeyGenerator keyGenerator = KeyGenerators.string();
    String secretKey = keyGenerator.generateKey();

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String email, List<Roles> roles, Long validTime) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createAccessToken(String email, List<Roles> roles) {
        String accessToken = createToken(email, roles, accessTokenValidTime);
        return accessToken;
    }

    public String createRefreshToken(String email, List<Roles> roles) {
        String refreshToken = createToken(email, roles, refreshTokenValidTime);
        return refreshToken;
    }

    public Authentication getAuthentication(String token) {
        SecurityMember securityMember = userDetailsService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(securityMember, "", securityMember.getAuthorities());
    }

    public String getEmail(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            return claims.get("sub").toString();
        } catch (ExpiredJwtException e) {
            Claims expired = e.getClaims();
            return expired.get("sub").toString();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    public String issueAccessToken(String email) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(email);
        if (refreshToken.isPresent()) {
            SecurityMember member = userDetailsService.loadUserByUsername(email);
            List<Roles> roles = member.getMember().getRolesList();
            String accessToken = createAccessToken(email, roles);
            return accessToken;
        } else {
            return null;
        }
    }

}
