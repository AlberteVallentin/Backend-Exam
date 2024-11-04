package dat.security.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.JOSEException;
import dat.config.HibernateConfig;
import dat.security.daos.ISecurityDAO;
import dat.security.daos.SecurityDAO;
import dat.security.entities.User;
import dat.security.enums.RoleType;
import dat.security.exceptions.ApiException;
import dat.security.exceptions.NotAuthorizedException;
import dat.security.exceptions.SecurityValidationException;
import dat.security.token.ITokenSecurity;
import dat.security.token.TokenSecurity;
import dat.security.token.TokenVerificationException;
import dat.security.token.UserDTO;
import dat.utils.Utils;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.text.ParseException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class SecurityController implements ISecurityController {
    private final ITokenSecurity tokenSecurity = new TokenSecurity();
    private static ISecurityDAO securityDAO;
    private static SecurityController instance;

    private SecurityController() {}

    public static SecurityController getInstance() {
        if (instance == null) {
            instance = new SecurityController();
            securityDAO = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
        }
        return instance;
    }

    @Override
    public Handler login() {
        return (ctx) -> {
            UserDTO user = ctx.bodyAsClass(UserDTO.class);
            if (user.getEmail() == null || user.getPassword() == null) {
                throw new SecurityValidationException(400, "Email and password are required");
            }

            UserDTO verifiedUser = securityDAO.getVerifiedUser(user.getEmail(), user.getPassword());
            String token = createToken(verifiedUser);

            ctx.status(200).json(Map.of(
                "token", token,
                "email", verifiedUser.getEmail(),
                "role", verifiedUser.getRoleType().toString()
            ));
        };
    }

    @Override
    public Handler register() {
        return (ctx) -> {
            UserDTO userInput = ctx.bodyAsClass(UserDTO.class);
            validateRegistrationInput(userInput);

            RoleType roleType = (userInput.getRoleType() != null) ? userInput.getRoleType() : RoleType.USER;
            User created = securityDAO.createUser(userInput.getName(), userInput.getEmail(), userInput.getPassword(), roleType);
            String token = createToken(new UserDTO(created.getEmail(), created.getRole().getRoleType()));

            ctx.status(201).json(Map.of(
                "token", token,
                "email", created.getEmail(),
                "role", created.getRole().getRoleType().toString()
            ));
        };
    }

    @Override
    public Handler authenticate() {
        return (ctx) -> {
            String header = ctx.header("Authorization");
            if (header == null) {
                throw new SecurityValidationException(401, "Authorization header missing");
            }

            String[] headerParts = header.split(" ");
            if (headerParts.length != 2) {
                throw new SecurityValidationException(401, "Invalid authorization header format");
            }

            String token = headerParts[1];
            UserDTO verifiedTokenUser = verifyToken(token);
            if (verifiedTokenUser == null) {
                throw new SecurityValidationException(401, "Invalid token");
            }

            ctx.attribute("user", verifiedTokenUser);
        };
    }

    @Override
    public boolean authorize(UserDTO userDTO, Set<RouteRole> allowedRoles) throws SecurityValidationException {
        if (userDTO == null) {
            throw new SecurityValidationException(401, "Authentication required");
        }

        return allowedRoles.stream()
            .map(RouteRole::toString)
            .collect(Collectors.toSet())
            .contains(userDTO.getRoleType().toString().toUpperCase());
    }

    private void validateRegistrationInput(UserDTO userInput) throws SecurityValidationException {
        if (userInput.getEmail() == null || userInput.getEmail().trim().isEmpty()) {
            throw new SecurityValidationException(400, "Email is required");
        }
        if (userInput.getPassword() == null || userInput.getPassword().trim().isEmpty()) {
            throw new SecurityValidationException(400, "Password is required");
        }
        if (userInput.getName() == null || userInput.getName().trim().isEmpty()) {
            throw new SecurityValidationException(400, "Name is required");
        }
        if (userInput.getRoleType() == RoleType.ADMIN) {
            throw new SecurityValidationException(403, "Cannot register as ADMIN");
        }
    }

    @Override
    public String createToken(UserDTO user) {
        try {
            boolean isDeployed = System.getenv("DEPLOYED") != null;
            String issuer = isDeployed ? System.getenv("ISSUER") : Utils.getPropertyValue("ISSUER", "config.properties");
            String expireTime = isDeployed ? System.getenv("TOKEN_EXPIRE_TIME") : Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
            String secretKey = isDeployed ? System.getenv("SECRET_KEY") : Utils.getPropertyValue("SECRET_KEY", "config.properties");

            return tokenSecurity.createToken(user, issuer, expireTime, secretKey);
        } catch (Exception e) {
            throw new ApiException(500, "Token creation failed");
        }
    }

    @Override
    public UserDTO verifyToken(String token) throws SecurityValidationException {
        try {
            String secret = System.getenv("DEPLOYED") != null
                ? System.getenv("SECRET_KEY")
                : Utils.getPropertyValue("SECRET_KEY", "config.properties");

            if (!tokenSecurity.tokenIsValid(token, secret) || !tokenSecurity.tokenNotExpired(token)) {
                throw new SecurityValidationException(401, "Token is invalid or expired");
            }

            return tokenSecurity.getUserWithRoleFromToken(token);
        } catch (SecurityValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(500, "Token verification failed");
        }
    }

    public @NotNull Handler addRole() {
        return (ctx) -> {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            if (!body.containsKey("role")) {
                throw new SecurityValidationException(400, "Role must be specified");
            }

            String newRole = body.get("role").toString();
            UserDTO user = ctx.attribute("user");
            securityDAO.addRole(user, newRole);

            ctx.status(200).json(Map.of("message", "Role " + newRole + " added to user"));
        };
    }

    // Health check for the API. Used in deployment
    public void healthCheck(@NotNull Context ctx) {
        ctx.status(200).json("{\"msg\": \"API is up and running\"}");
    }
}
