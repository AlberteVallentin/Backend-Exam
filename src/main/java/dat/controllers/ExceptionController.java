package dat.controllers;

import dat.exceptions.ApiException;
import dat.exceptions.ValidationException;
import dat.security.exceptions.NotAuthorizedException;
import dat.security.exceptions.SecurityValidationException;
import dat.utils.Utils;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    // Method to handle ApiException (general API errors)
    public void apiExceptionHandler(ApiException e, Context ctx) {
        ctx.status(e.getStatusCode());
        logger.error("API Error at {}: {}", ctx.path(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "error", e.getMessage() + " - " + ctx.path()));
    }

    // Method to handle ApiException specifically for security issues
    public void apiSecurityExceptionHandler(dat.security.exceptions.ApiException e, Context ctx) {
        ctx.status(e.getCode());
        logger.warn("Security API Error at {}: {}", ctx.path(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "error", e.getMessage() + " - " + ctx.path()));
    }

    // Method to handle NotAuthorizedException (for authorization errors)
    public void notAuthorizedExceptionHandler(NotAuthorizedException e, Context ctx) {
        ctx.status(e.getStatusCode());
        logger.warn("Authorization Error at {}: {}", ctx.path(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "error", e.getMessage() + " - " + ctx.path()));
    }

    // Method to handle UnauthorizedResponse (missing or malformed auth headers)
    public void unauthorizedResponseHandler(UnauthorizedResponse e, Context ctx) {
        ctx.status(401);
        logger.warn("Unauthorized access attempt at {}: {}", ctx.path(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "error", e.getMessage()));
    }

    // Method to handle business ValidationException
    public void validationExceptionHandler(ValidationException e, Context ctx) {
        ctx.status(e.getStatusCode());
        logger.warn("Validation Error at {}: {}", ctx.path(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "error", e.getMessage()));
    }

    // Method to handle security ValidationException
    public void securityValidationExceptionHandler(SecurityValidationException e, Context ctx) {
        ctx.status(e.getStatusCode());
        logger.warn("Security validation error at {}: {}", ctx.path(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "error", e.getMessage()));
    }


    // Handler for duplicate entity creation attempts
    public void entityExistsHandler(EntityExistsException e, Context ctx) {
        ctx.status(409);
        logger.warn("Entity Exists Error at {}: {}", ctx.path(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "warning", "Entity already exists: " + e.getMessage()));
    }

    // Method to handle EntityNotFoundException
    public void entityNotFoundExceptionHandler(EntityNotFoundException e, Context ctx) {
        ctx.status(404);
        logger.warn("Entity Not Found at {}: {}", ctx.path(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "error", e.getMessage()));
    }


    // General exception handler for unexpected errors
    public void generalExceptionHandler(Exception e, Context ctx) {
        ctx.status(500);
        logger.error("Unexpected error at {}: {}", ctx.path(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "error", "Internal server error - " + ctx.path()));
    }
}
