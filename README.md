# SegreGo API

Spring Boot backend for the SegreGo organic waste collection platform.

## Local Development

1. Configure MySQL access in `src/main/resources/application.properties`.
2. Optionally set `app.seed-data=true` to preload sample users.
3. Start the API:

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot'
.\mvnw.cmd spring-boot:run
```

## Tests

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot'
.\mvnw.cmd test
```

## Required Configuration

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `app.jwt.secret`
- `app.cors.allowed-origins`

Set `app.cors.allowed-origins` to your frontend domain after deployment.
