# Holger OAuth2 with Keycloak

This is a Spring Boot application that demonstrates OAuth2 login with Keycloak.

## Prerequisites

- Java 17 or higher
- Maven
- Keycloak server (at least version 17.0.0)

## Setup Keycloak

1. Download and start Keycloak server:
   ```
   docker run -p 8180:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:latest start-dev
   ```

2. Configure Keycloak:
   - Log in to the Keycloak Admin Console at http://localhost:8180/admin
   - Create a new realm named `myrealm`
   - Create a new client:
     - Client ID: `holger-app`
     - Client Protocol: `openid-connect`
     - Access Type: `confidential`
     - Valid Redirect URIs: `http://localhost:8080/*`
     - Web Origins: `http://localhost:8080`
   - After client creation, go to the "Credentials" tab to get the client secret
   - Update the `spring.security.oauth2.client.registration.keycloak.client-secret` in `application.properties`
   - Create a test user with a password

## Configuration

Update the `application.properties` file with your Keycloak configuration:

```properties
# Keycloak configuration
spring.security.oauth2.client.registration.keycloak.client-id=holger-app
spring.security.oauth2.client.registration.keycloak.client-secret=your-client-secret
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8180/realms/myrealm
```

## Running the Application

1. Build the project:
   ```
   mvn clean install
   ```

2. Run the Spring Boot application:
   ```
   mvn spring-boot:run
   ```

3. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

4. Click on "Login with Keycloak" and use your Keycloak credentials to log in.

## Features

- OAuth2 login with Keycloak
- User profile page showing user attributes
- Role-based access control
- Sample pages for demonstration

## Security Configuration

The application uses Spring Security with OAuth2 client support. The main security configuration is in the `SecurityConfig.java` file, which sets up:

- Public access to the home page
- Protected access to user profile
- OAuth2 login configuration
- Role mapping from Keycloak