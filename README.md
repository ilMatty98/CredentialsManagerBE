# Credentials Manager BackEnd

## Requirements
- Postgres 15
- Jdk 17
- Maven 3
- SSL certificate

## Installation

```bash
mvn clean install
```

## Run

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## SSL certificate

```bash
keytool -genkey -alias ssl_certificate -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore com.credentialsmanager -validity 3650
```

## Utility

- Swagger: http://localhost:8080/swagger-ui/index.html

## Contributing

ilMattaty98 & TwoTimeScotti

## License

Personal