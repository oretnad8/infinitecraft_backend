package com.microservice.authentication.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    private SecretKey key;
    private final String secret = "secretKeyMustBeLongEnoughToBeSecureAndSafeForProductionUse1234567890"; // 32+ chars

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createToken(String email, String role) {\n        return createTokenWithUserInfo(email, role, null, null, null, null, null, null);\n    }\n\n    public String createTokenWithUserInfo(String email, String role, Long id, String nombre, String telefono, String region, String comuna, String password) {
        Map<String, Object> claims = new HashMap<>();\n        claims.put("email", email);
        claims.put("role", role);\n        if (id != null) claims.put("id", id);\n        if (nombre != null) claims.put("nombre", nombre);\n        if (telefono != null) claims.put("telefono", telefono);\n        if (region != null) claims.put("region", region);\n        if (comuna != null) claims.put("comuna", comuna);
        Date now = new Date();
        Date exp = new Date(now.getTime() + 3600000); // 1 hour

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getClaimsFromToken(String token) {\n        return Jwts.parser()\n                .verifyWith(key)\n                .build()\n                .parseSignedClaims(token)\n                .getPayload()\n                .get("claims", Map.class);\n    }\n\n    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
