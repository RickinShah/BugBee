package com.app.BugBee.security;

import com.app.BugBee.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "account";

    final private SecretKey key;
    final private JwtParser parser;

    private JwtTokenProvider() {
        this.key = Keys.hmacShaKeyFor("fdc79832cbea9abf77bb854adae69935d9493ce1c5810c76da876b379a7b9199".getBytes());
        this.parser = Jwts.parser().verifyWith(this.key).build();
    }

    public String createToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }

    public String getUsername(String token) {
        Claims claims = parser.parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = parser.parseSignedClaims(token);

            return !claims.getPayload().getExpiration().before(Date.from(Instant.now()));

        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token!");
        }

        return false;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parser.parseSignedClaims(token).getPayload();

//        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get(AUTHORITIES_KEY).toString());

        User principal = new User(claims.getSubject(), "", null);

        return new UsernamePasswordAuthenticationToken(principal, token, null);

//                AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get(AUTHORITIES_KEY, authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","))));
    }



}
