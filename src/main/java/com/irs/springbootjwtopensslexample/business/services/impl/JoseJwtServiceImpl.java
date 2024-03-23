package com.irs.springbootjwtopensslexample.business.services.impl;

import com.irs.springbootjwtopensslexample.business.services.JwtService;
import com.irs.springbootjwtopensslexample.config.JwtProperties;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

@Service("joseJwtService")
@AllArgsConstructor
@Slf4j
public class JoseJwtServiceImpl implements JwtService {
    
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
        return extractClaim(token, JWTClaimsSet::getSubject);
    }
    
    @Override
    public boolean validate(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username != null && username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
        
    private String buildToken(Map<String, Object> extraClaims, Authentication authentication) {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            
            JWSSigner signer = new RSASSASigner(jwtProperties.getPrivateKey());
            
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .claim(ROLES, extraClaims.get(ROLES))
                    .subject(authentication.getName())
                    .issueTime(new Date(currentTimeMillis))
                    .expirationTime(new Date(currentTimeMillis + jwtProperties.getExpiration()))
                    .build();
            
            SignedJWT signed = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
            
            signed.sign(signer);
            
            return signed.serialize();
        } catch (JOSEException ex) {
            log.error("Error generando el jwt", ex);
            throw new RuntimeException("Error generando jwt", ex);
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, JWTClaimsSet::getExpirationTime);
    }
    
    private <T> T extractClaim(String token, Function<JWTClaimsSet, T> claimsResolver) {
        final JWTClaimsSet claimsSet = extractAllClaims(token);

        return claimsResolver.apply(claimsSet);
    }
    
    private JWTClaimsSet extractAllClaims(String token) {
        try {
            SignedJWT signed = SignedJWT.parse(token);

            JWSVerifier verifier = new RSASSAVerifier(jwtProperties.getPublicKey());

            signed.verify(verifier);
                        
            return signed.getJWTClaimsSet();
        } catch (ParseException ex) {
            log.error("Error parseando el jwt", ex);
            throw new RuntimeException("Error parseando jwt", ex);
        } catch (JOSEException ex) {
            log.error("Error verificando el jwt", ex);
            throw new RuntimeException("Error verificando jwt", ex);
        }
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
