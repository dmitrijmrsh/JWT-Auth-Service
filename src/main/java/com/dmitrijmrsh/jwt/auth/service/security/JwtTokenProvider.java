package com.dmitrijmrsh.jwt.auth.service.security;

import com.dmitrijmrsh.jwt.auth.service.entity.Authority;
import com.dmitrijmrsh.jwt.auth.service.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final MyUserDetailsService userDetailsService;

    @Value("${security.jwt.token.secret}")
    private String secret;

    @Value("${security.jwt.token.timestamp}")
    private int timestamp;

    @PostConstruct
    private void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String username, List<Authority> authorities) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", authorities.stream()
                .map(element -> new SimpleGrantedAuthority(element.getAuthority()))
                .toList());

        Date now = new Date();
        Date validity = new Date(now.getTime() + timestamp);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(
                this.getUsernameFromToken(token)
        );

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new CustomException("JWT token expired or invalid", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
