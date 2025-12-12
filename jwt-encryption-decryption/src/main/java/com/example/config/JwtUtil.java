package com.example.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.Function;

import javax.annotation.PostConstruct;

@Component
public class JwtUtil {
	
	public static final long TOKEN_VALIDITY = 4 * 60 * 60;
	
	@Value("${jwt.issuer}")
    public String issuer;
	
    private final Environment env;
	private PrivateKey privateKey;
    private PublicKey publicKey;

    public JwtUtil(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() throws Exception {
        this.privateKey = getPrivateKey(env.getProperty("private.key"));
        this.publicKey = getPublicKey(env.getProperty("public.key"));
    }
    
    private PrivateKey getPrivateKey(String key) throws Exception {
		if (key == null) {
			throw new IllegalArgumentException("Private key not configured");
		}
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        System.err.println("PrivateKey : " + keyFactory.generatePrivate(spec));
        return keyFactory.generatePrivate(spec);
    }

    private PublicKey getPublicKey(String key) throws Exception {
		if (key == null) {
			throw new IllegalArgumentException("Public key not configured");
		}
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        System.err.println("PublicKey : " + keyFactory.generatePublic(spec));
        return keyFactory.generatePublic(spec);
    }

    public String generateJwtToken(String userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("issuer", env.getProperty("jwt.issuer"));
       
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
//                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

	public Boolean validateJwtToken(String token, String userDetails) {
		final String username = getUsernameFromToken(token);
        return (username.equals(userDetails) && !isTokenExpired(token) && validateTokenIssuer(token));
	}

	public String getUsernameFromToken(String token) {

        return extractClaim(token, Claims::getSubject);
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
	
	private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
//                .setSigningKey(getSignKey())
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

	public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
	
	public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
	
	public void expireToken(String token) {
		Claims claims = extractAllClaims(token);
	    claims.setExpiration(new Date());
	}
	
	private boolean validateTokenIssuer(String token) {
		final Claims claims = extractAllClaims(token);
	    final String issuer = claims.get("issuer", String.class);
	    return issuer != null && issuer.equals(this.issuer);
	}
}
