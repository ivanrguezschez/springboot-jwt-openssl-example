package com.irs.springbootjwtopensslexample.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
    Comandos ejecutados desde la terminal de git (Git Bash) que tiene el comando openssl
    
    Comando para generar el par de claves (privada y publica)
    $> openssl genrsa -out keypair.pem 2048

    Comando para generar la clave publica (formato PEM) basada en el par de claves generadas anteriormente.
    $> openssl rsa -pubout -in keypair.pem -out public-key.pem
    
    Comando para generar la clave publica (formato DER, java puede leerlo) basada en el par de claves generadas anteriormente.
    $> openssl rsa -pubout -outform DER -in keypair.pem -out public-key.der

    Comando para generar la clave privada (formato PKCS#8) basada en el par de claves generadas anteriormente.
    $> openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private-key.pem
	
    Una vez generadas las claves pública y privada podemos eliminar el archivo keypair.pem, también podemos 
    generar la clave pública en base a la privada y no generar el par de claves.
*/

@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    
    private RSAPrivateKey privateKey;

    private RSAPublicKey publicKey;

    private Long expiration;
}

