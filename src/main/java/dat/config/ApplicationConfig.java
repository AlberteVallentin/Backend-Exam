package dat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat.exceptions.ApiException;
import dat.routes.Routes;
import dat.security.controllers.AccessController;
import dat.security.controllers.SecurityController;
import dat.security.enums.RoleType;
import dat.security.exceptions.NotAuthorizedException;

import dat.exceptions.ValidationException;
import dat.security.exceptions.SecurityValidationException;
import dat.security.routes.SecurityRoutes;
import dat.utils.Utils;
import dat.controllers.ExceptionController;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {

    private static Routes routes = new Routes();
    private static ObjectMapper jsonMapper = new Utils().getObjectMapper();
    private static SecurityController securityController = SecurityController.getInstance();
    private static AccessController accessController = new AccessController();
    private static Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static int count = 1;

    public static void configuration(JavalinConfig config) {
        config.showJavalinBanner = false;
        config.bundledPlugins.enableRouteOverview("/routes", RoleType.ANYONE);
        config.router.contextPath = "/api"; // base path for all endpoints
        config.router.apiBuilder(routes.getRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecuredRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecurityRoutes());
    }

    public static Javalin startServer(int port) {
        Javalin app = Javalin.create(ApplicationConfig::configuration);

        app.beforeMatched(accessController::accessHandler);
        app.after(ApplicationConfig::afterRequest);

        // Exception handling setup
        ExceptionController exceptionController = new ExceptionController();

        app.exception(EntityNotFoundException.class, exceptionController::entityNotFoundExceptionHandler);
        app.exception(ApiException.class, exceptionController::apiExceptionHandler);
        app.exception(dat.security.exceptions.ApiException.class, exceptionController::apiSecurityExceptionHandler);
        app.exception(NotAuthorizedException.class, exceptionController::notAuthorizedExceptionHandler);
        app.exception(UnauthorizedResponse.class, exceptionController::unauthorizedResponseHandler);
        app.exception(ValidationException.class, exceptionController::validationExceptionHandler);
        app.exception(SecurityValidationException.class, exceptionController::securityValidationExceptionHandler);
        app.exception(EntityExistsException.class, exceptionController::entityExistsHandler);
        app.exception(Exception.class, exceptionController::generalExceptionHandler);

        app.start(port);
        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }

    public static void afterRequest(Context ctx) {
        String requestInfo = ctx.req().getMethod() + " " + ctx.req().getRequestURI();
        logger.info("Request {} - {} was handled with status code {}", count++, requestInfo, ctx.status());
    }

}
