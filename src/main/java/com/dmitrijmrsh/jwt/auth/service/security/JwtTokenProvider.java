package com.dmitrijmrsh.jwt.auth.service.security;

import com.dmitrijmrsh.jwt.auth.service.entity.User;
import com.dmitrijmrsh.jwt.auth.service.exception.CustomException;
import com.dmitrijmrsh.jwt.auth.service.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final MyUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Value("${security.jwt.token.secret}")
    private String secret;

    @Value("${security.jwt.token.timestamp}")
    private int timestamp;

    @PostConstruct
    private void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String email) {
        Optional<User> mayBeUser = userRepository.findUserByEmail(email);

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("auth", mayBeUser.map(user -> user.getRole().getRoleInString())
                .orElseThrow(() -> new CustomException(this.messageSource.getMessage(
                        "user.auth.errors.user.not.found.by.email", null, Locale.getDefault()
                ), HttpStatus.NOT_FOUND))
        );

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + timestamp);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(
                this.getEmailFromToken(token)
        );

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");

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
            throw new CustomException(this.messageSource.getMessage(
                    "user.auth.errors.token.expired.or.invalid", null, Locale.getDefault())
            , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
