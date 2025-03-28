package com.bankdemo.account.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtTestUtil {

    public static String generateTestToken(String username, String secret) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("test-issuer")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora de validez
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}