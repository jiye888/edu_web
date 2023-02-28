package com.jtudy.education.security;

import com.jtudy.education.repository.RefreshTokenRepository;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String accessToken = jwtTokenProvider.resolveToken(request);

        if(accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if(accessToken != null && !jwtTokenProvider.validateToken(accessToken)){
            try {
                String email = jwtTokenProvider.getEmail(accessToken);
                Optional refreshToken = refreshTokenRepository.findById(email);
                if (refreshToken.isPresent()) {
                    String reIssued = jwtTokenProvider.issueAccessToken(email);
                    Authentication authentication = jwtTokenProvider.getAuthentication(reIssued);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch(SignatureException e) {
                System.out.println("JWT couldn't be matched. "+e.getMessage());
                Cookie cookie = new Cookie("Matched_Token", "not_matched");
                cookie.setMaxAge(60*60*6);
                response.addCookie(cookie);
            } /*catch (Exception e) {
                System.out.println(e.getMessage());
                Cookie cookie = new Cookie("Matched_Token", "not_matched");
                cookie.setMaxAge(60*60*6);
                response.addCookie(cookie);
            }*/

        }

        filterChain.doFilter(request, response);
    }

}
