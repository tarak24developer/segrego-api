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

## Render

This API is prepared for Render deployment.

- Repo: `https://github.com/tarak24developer/segrego-api`
- Runtime: `Java`
- Build command: `./mvnw clean package -DskipTests`
- Start command: `java -jar target/segrego-backend-0.0.1-SNAPSHOT.jar`

Required environment variables on Render:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_JWT_SECRET`
- `APP_CORS_ALLOWED_ORIGINS`

For Render, set `APP_CORS_ALLOWED_ORIGINS=https://segrego-frontend.vercel.app`.
