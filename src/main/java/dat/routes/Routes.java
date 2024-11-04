
package dat.routes;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final DoctorRoute doctorRoute = new DoctorRoute();
    private final DoctorMockRoute doctorMockRoute = new DoctorMockRoute();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/doctors", doctorRoute.getRoutes());
            path("/mock/doctors", doctorMockRoute.getRoutes());
        };
    }
}