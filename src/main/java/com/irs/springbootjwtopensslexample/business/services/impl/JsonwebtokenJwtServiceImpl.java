package com.irs.springbootjwtopensslexample.business.services.impl;

import com.irs.springbootjwtopensslexample.business.services.JwtService;
import com.irs.springbootjwtopensslexample.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.core.userdetails.UserDetails;

@Service("jsonwebtokenJwtService")
@AllArgsConstructor
public class JsonwebtokenJwtServiceImpl implements JwtService {
    
    /** Nombre del claim del JWT 'roles'. */
    public static final String ROLES = "roles";
    
    private static final String ROLE_PREFIX = "ROLE_";
    
    private final JwtProperties jwtProperties;

    @Override
    public String generate(Authentication authentication) {
        List<String> roles = authentication.getAuthorities()
                .stream()
                //.map(GrantedAuthority::getAuthority)
                //.collect(Collectors.joining(" "));
                .map((ga) -> ga.getAuthority().replaceFirst(ROLE_PREFIX, ""))
                .collect(Collectors.toList());
        
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(ROLES, roles);
        
        return buildToken(extraClaims, authentication);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    @Override
    public boolean validate(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username != null && username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }
    
    private String buildToken(Map<String, Object> extraClaims, Authentication authentication) {
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts
                .builder()
                .setHeader(Map.of(Header.TYPE,Header.JWT_TYPE))
                .setClaims(extraClaims)
                .setSubject(authentication.getName())
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(currentTimeMillis + jwtProperties.getExpiration()))
                .signWith(jwtProperties.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(jwtProperties.getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
