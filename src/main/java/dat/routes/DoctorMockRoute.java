package dat.routes;

import dat.controllers.impl.DoctorMockController;
import dat.exceptions.ApiException;
import dat.security.enums.RoleType;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class DoctorMockRoute {
    private final DoctorMockController doctorController = new DoctorMockController();

    protected EndpointGroup getRoutes() {
        return () -> {
            // Basic CRUD operations
            get("/", doctorController::readAll);
            post("/", doctorController::create);
            get("/{id}", doctorController::read);
            put("/{id}", doctorController::update);
            delete("/{id}", doctorController::delete);

            // Specialized endpoints
            get("/speciality/{speciality}", doctorController::readBySpeciality);
            get("/birthdate/range", doctorController::readByBirthdateRange);
        };
    }
}