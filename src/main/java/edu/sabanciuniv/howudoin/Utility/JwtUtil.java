package edu.sabanciuniv.howudoin.Utility;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    /*
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000 * 7)) // 7 days expiration
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    */
    public String generateToken(String userId) {
        System.out.println("Generated Token Subject (userId): " + userId);
        return Jwts.builder()
                .setSubject(userId) // Use userId as the subject
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000 * 7)) // 7 days expiration
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();


    }


    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token, String userId) {
        return userId.equals(extractUserId(token)) && !isTokenExpired(token);
    }


    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public String extractUserId(String token) {
        System.out.println("Token received for parsing: " + token);

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey) // Ensure your secret key is configured properly
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();
            System.out.println("Extracted User ID from Token: " + userId);
            return userId;

        } catch (ExpiredJwtException e) {
            System.err.println("Token expired: " + e.getMessage());
            throw new RuntimeException("Token expired", e);
        } catch (MalformedJwtException e) {
            System.err.println("Malformed token: " + e.getMessage());
            throw new RuntimeException("Invalid token format", e);
        } catch (Exception e) {
            System.err.println("Token validation error: " + e.getMessage());
            throw new RuntimeException("Invalid or expired token", e);
        }
    }

    /*
    public String extractUserId(String token) {
        try {
            System.out.println("Token received for parsing: {}"+ token);
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            System.err.println("Error parsing token: " + e.getMessage());
            throw new RuntimeException("Invalid or expired token", e);
        }
    }
    */
    /* orijinal fonksiyon
    public String extractUserId(String token) {
        return getClaims(token).getSubject(); // Assuming the user ID is stored as the "subject"
    }
    */

    /*
    // New method to extract sender ID from the Authorization header
    public String extractSenderIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        return extractUserId(token); // Reuse existing extractUserId method
    }
    */
    public String extractSenderIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        System.out.println("Extracted Sender ID: " + claims.getSubject()); // Debug log
        return claims.getSubject(); // Assuming the sender's ID is stored in the 'sub' field
    }

}
