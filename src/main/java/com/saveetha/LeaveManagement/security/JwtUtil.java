package com.saveetha.LeaveManagement.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")  // Load from application.properties
    private String SECRET_KEY;

    private final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    // Generate JWT Token with empId & role
    public String generateToken(String empId, String role) {
        return JWT.create()
                .withSubject(empId) // Using empId as subject
                .withClaim("role", role)  // Include role in JWT
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    // Extract empId from Token
    public String extractEmpId(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired JWT token", e);
        }
    }

    // Extract role from Token
    public String extractRole(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("role").asString();  // Extract role
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired JWT token");
        }
    }

    // Validate Token
    public boolean isTokenValid(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }


}
