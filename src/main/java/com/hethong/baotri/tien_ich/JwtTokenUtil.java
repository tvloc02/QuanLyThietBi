package com.hethong.baotri.tien_ich;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenUtil {

    // ✅ SỬA: Tạo secret key đủ mạnh cho HS512
    private static final String JWT_SECRET = "BaoTriThietBiSecretKeyForHS512AlgorithmMustBeAtLeast512BitsLongToEnsureSecurityAndCompliance2024";

    @Value("${app.jwt.expiration:86400000}") // 24 hours
    private int jwtExpiration;

    @Value("${app.jwt.refresh-expiration:604800000}") // 7 days
    private int refreshExpiration;

    // ✅ SỬA: Sử dụng SecretKey thay vì String
    private SecretKey getSigningKey() {
        // Đảm bảo key đủ dài cho HS512 (tối thiểu 64 bytes)
        String secretKey = JWT_SECRET;
        if (secretKey.length() < 64) {
            // Pad với thêm ký tự để đủ 64 bytes
            secretKey = secretKey + "PaddingTextToEnsure64BytesLengthForHS512AlgorithmCompliance2024";
        }
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ THÊM: Method để tạo secure key tự động (alternative)
    public static SecretKey generateSecureKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, userDetails.getUsername(), refreshExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, int expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512) // ✅ SỬA: Sử dụng SecretKey
                    .compact();
        } catch (Exception e) {
            log.error("Error creating JWT token: {}", e.getMessage());
            throw new RuntimeException("Could not create JWT token", e);
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT token validation error: {}", e.getMessage());
        }
        return false;
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.debug("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String refreshToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String username = claims.getSubject();

            // Tạo token mới với thời gian expire mới
            Map<String, Object> newClaims = new HashMap<>();
            return createToken(newClaims, username, jwtExpiration);

        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw new RuntimeException("Could not refresh token", e);
        }
    }

    // ✅ THÊM: Getter methods for configuration
    public int getExpirationTime() {
        return jwtExpiration;
    }

    public int getRefreshExpirationTime() {
        return refreshExpiration;
    }

    // ✅ THÊM: Method để kiểm tra token type
    public String getTokenType(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return (String) claims.get("type");
        } catch (Exception e) {
            return null;
        }
    }

    // ✅ THÊM: Method để extract custom claims
    public Object getCustomClaim(String token, String claimName) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.get(claimName);
        } catch (Exception e) {
            log.debug("Could not extract claim '{}' from token: {}", claimName, e.getMessage());
            return null;
        }
    }
}