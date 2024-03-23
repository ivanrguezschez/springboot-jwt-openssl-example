package com.irs.springbootjwtopensslexample.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class RSAPrivateKeyConverter implements Converter<String, RSAPrivateKey> {

    @Override
    public RSAPrivateKey convert(String source) {
        try {
            Resource resource = new ClassPathResource(source);
                        
            byte[] keyBytes = Files.readAllBytes(Paths.get(resource.getURI()));

            String privateKeyPEM = new String(keyBytes, StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PRIVATE KEY-----", "");
            
            byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error convirtiendo la clave privada", e);
        }
    }
    
}
