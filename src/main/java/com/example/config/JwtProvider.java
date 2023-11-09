package com.example.config;

import com.example.constant.JwtConstant;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JwtConstant.SECRET_KEY));
    }

    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 604800000L))
                .claim("email", authentication.getName())
                .signWith(key(), SignatureAlgorithm.HS256).compact();
    }

    public ResponseCookie generateTokenCookie(Authentication authentication) {
        String token = generateToken(authentication);

        return ResponseCookie.from(JwtConstant.JWT_COOKIE, token)
                .path("/api")
                .maxAge(24 * 60 * 60 * 7)
                .httpOnly(true).secure(true).build();
    }

    public String getTokenFromCookie(HttpServletRequest request) {
        Cookie tokenCookie = WebUtils.getCookie(request, JwtConstant.JWT_COOKIE);

        if (tokenCookie != null) {
            return tokenCookie.getValue();
        } else {
            return null;
        }
    }

    public ResponseCookie cleanTokenCookie() {
        return ResponseCookie.from(JwtConstant.JWT_COOKIE, "").path("/api").maxAge(0).build();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT claims string is empty: " + e.getMessage());
        }
    }

    public Claims getClaimsFormToken(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
