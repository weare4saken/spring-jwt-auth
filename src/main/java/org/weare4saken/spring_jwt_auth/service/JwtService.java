package org.weare4saken.spring_jwt_auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.weare4saken.spring_jwt_auth.entity.User;
import org.weare4saken.spring_jwt_auth.entity.Role;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;

    private final Duration expiration;

    public JwtService(@Value("${jwt.token.expiration:24h}") Duration expiration) {
        this.secretKey = this.getSigningKey();
        this.expiration = expiration;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("email", customUserDetails.getEmail());
            claims.put("roles", customUserDetails.getRoles().stream().map(Role::getType).toArray());
        }
        return this.generateToken(claims, userDetails);
    }

    private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        final Date date = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(date)
                .expiration(new Date(date.getTime() + this.expiration.toMillis()))
                .signWith(this.secretKey)
                .compact();
    }

    public boolean isTokenValid(String token) {
        return !this.isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return this.extractExpiration(token).before(new Date());
    }

    public String extractUserName(String token) {
        return this.extractClaim(token, Claims::getSubject);
    }

    public List<?> extractRoles(String token) {
        return this.extractAllClaims(token).get("roles", List.class);
    }

    public Date extractExpiration(String token) {
        return this.extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = this.extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Jwts.SIG.HS256.key().build();
    }
}
