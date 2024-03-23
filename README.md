# springboot-jwt-openssl-example
Ejemplo de aplicación Spring Boot con autenticación mediante JWT.

Se emplea OpenSSL para generar las claves privada y pública.

### OpenSSL
Comando para generar la clave privada

$ openssl genrsa -out private-key.pem 2048

Comando para generar la clave pública

$ openssl rsa -pubout -in private-key.pem -out public-key.pem

Los archivos private-key.pem y public-key.pem deben generarse en el directorio 'src/main/resources/keys', tal y como marcan el valor de las propiedades definidas en 'application.properties'.
