package dat.routes;

import dat.controllers.impl.DoctorControllerDB;
import dat.security.enums.RoleType;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;

public class DoctorRoute {
    private final DoctorControllerDB doctorController = new DoctorControllerDB();

    protected EndpointGroup getRoutes() {
        return () -> {
            // Basic CRUD operations
            get("/", doctorController::readAll, RoleType.ANYONE);
            post("/", doctorController::create, RoleType.USER);
            get("/{id}", doctorController::read, RoleType.ANYONE);
            put("/{id}", doctorController::update, RoleType.USER);
            delete("/{id}", doctorController::delete, RoleType.ADMIN);

            // Specialized endpoints
            get("/speciality/{speciality}", doctorController::readBySpeciality, RoleType.ANYONE);
            get("/birthdate/range", doctorController::readByBirthdateRange, RoleType.ANYONE);
        };
    }
}