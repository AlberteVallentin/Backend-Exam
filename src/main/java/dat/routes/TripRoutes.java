package dat.routes;

import dat.config.HibernateConfig;
import dat.controllers.TripController;
import dat.security.enums.RoleType;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {
    private final TripController tripController = new TripController();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/trips", () -> {
                // Basic CRUD operations
                get("/", tripController::getAll, RoleType.ANYONE);
                get("/{id}", tripController::getById, RoleType.ANYONE);
                post("/", tripController::create, RoleType.USER, RoleType.ADMIN);
                put("/{id}", tripController::update, RoleType.USER, RoleType.ADMIN);
                delete("/{id}", tripController::delete, RoleType.ADMIN, RoleType.ADMIN);

                // Special operations
                put("/{tripId}/guides/{guideId}", tripController::addGuideToTrip, RoleType.USER, RoleType.ADMIN);

                // Population endpoint
                post("/populate", ctx -> {
                    dat.config.Populator.populate(HibernateConfig.getEntityManagerFactory());
                    ctx.status(201).json("{\"message\": \"Database populated successfully\"}");
                }, RoleType.ADMIN);

                get("/category/{category}", tripController::getTripsByCategory, RoleType.ANYONE);
                get("/guides/totalprice", tripController::getGuidesTotalPrices, RoleType.ANYONE);
                get("/{id}/weight", tripController::getTripPackingWeight, RoleType.ANYONE);

            });
        };
    }
}