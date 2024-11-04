package dat.security.controllers;

import dat.security.enums.RoleType;
import dat.security.exceptions.SecurityValidationException;
import dat.security.token.UserDTO;
import io.javalin.http.Context;
import io.javalin.security.RouteRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class AccessController implements IAccessController {
    private static final Logger logger = LoggerFactory.getLogger(AccessController.class);
    private final SecurityController securityController = SecurityController.getInstance();

    @Override
    public void accessHandler(Context ctx) throws SecurityValidationException {
        if (ctx.routeRoles().isEmpty() || ctx.routeRoles().contains(RoleType.ANYONE)) {
            return;
        }

        if (ctx.method().toString().equals("OPTIONS")) {
            ctx.status(200);
            return;
        }

        String header = ctx.header("Authorization");
        if (header == null || header.trim().isEmpty()) {
            throw new SecurityValidationException(401, "You need to log in!");
        }

        try {
            securityController.authenticate().handle(ctx);
        } catch (Exception e) {
            throw new SecurityValidationException(401, "You need to log in! Or your token is invalid.");
        }

        UserDTO user = ctx.attribute("user");
        Set<RouteRole> allowedRoles = ctx.routeRoles();
        if (!securityController.authorize(user, allowedRoles)) {
            throw new SecurityValidationException(403,
                String.format("You don't have permission to access this resource. Your role: %s, Required roles: %s",
                    user.getRoleType(), allowedRoles));
        }
    }
}