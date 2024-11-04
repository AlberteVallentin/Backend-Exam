package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.config.PopulatorTest;
import dat.dtos.TripDTO;
import dat.enums.TripCategory;
import dat.security.controllers.SecurityController;
import dat.security.daos.SecurityDAO;
import dat.security.token.UserDTO;
import io.javalin.Javalin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import io.restassured.common.mapper.TypeRef;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TripRoutesTest {

    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private final static SecurityController securityController = SecurityController.getInstance();
    private final static SecurityDAO securityDAO = new SecurityDAO(emf);
    private static Javalin app;
    private static TripDTO[] trips;
    private static TripDTO trip1, trip2, trip3, trip4;
    private static UserDTO userDTO, adminDTO;
    private static String userToken, adminToken;
    private static final String BASE_URL = "http://localhost:7070/api";

    @BeforeAll
    void setUpAll() {
        HibernateConfig.setTest(true);
        app = ApplicationConfig.startServer(7070);
    }

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Trip").executeUpdate();
            em.createQuery("DELETE FROM Guide").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        }

        // Populate test data using PopulatorTest
        UserDTO[] users = PopulatorTest.populateUsers(emf);
        trips = PopulatorTest.populateTrips(emf);

        // Assign test data to instance variables
        userDTO = users[0];
        adminDTO = users[1];
        trip1 = trips[0];
        trip2 = trips[1];
        trip3 = trips[2];
        trip4 = trips[3];

        try {
            UserDTO verifiedUser = securityDAO.getVerifiedUser(userDTO.getEmail(), userDTO.getPassword());
            UserDTO verifiedAdmin = securityDAO.getVerifiedUser(adminDTO.getEmail(), adminDTO.getPassword());

            userToken = "Bearer " + securityController.createToken(verifiedUser);
            adminToken = "Bearer " + securityController.createToken(verifiedAdmin);
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup security tokens", e);
        }
    }

    @AfterEach
    void tearDown() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Trip").executeUpdate();
            em.createQuery("DELETE FROM Guide").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @AfterAll
    void tearDownAll() {
        ApplicationConfig.stopServer(app);
    }

    @Test
    void getAllTrips() {
        List<TripDTO> returnedTrips = given()
            .contentType("application/json")
            .when()
            .get(BASE_URL + "/trips")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<TripDTO>>() {});

        assertThat(returnedTrips, hasSize(4));
        assertThat(returnedTrips, hasItems(
            hasProperty("name", equalTo("Copenhagen City Walk")),
            hasProperty("name", equalTo("Amager Beach Experience")),
            hasProperty("name", equalTo("Dyrehaven Forest Tour")),
            hasProperty("name", equalTo("Øresund Sea Adventure"))
        ));

        // Test at vi har de rigtige kategorier repræsenteret
        assertThat(returnedTrips.stream()
                .map(TripDTO::getCategory)
                .collect(Collectors.toList()),
            containsInAnyOrder(
                TripCategory.CITY,
                TripCategory.BEACH,
                TripCategory.FOREST,
                TripCategory.SEA
            ));
    }

    @Test
    void getTripById() {
        TripDTO returnedTrip = given()
            .contentType("application/json")
            .when()
            .get(BASE_URL + "/trips/" + trip1.getId())
            .then()
            .statusCode(200)
            .extract()
            .as(TripDTO.class);

        assertThat(returnedTrip.getId(), equalTo(trip1.getId()));
        assertThat(returnedTrip.getName(), equalTo(trip1.getName()));
        assertThat(returnedTrip.getPackingItems(), notNullValue());
    }

    @Test
    void createTrip() {
        TripDTO newTrip = new TripDTO();
        newTrip.setName("Test Trip");
        newTrip.setStartTime(ZonedDateTime.now().plusDays(1));
        newTrip.setEndTime(ZonedDateTime.now().plusDays(1).plusHours(2));
        newTrip.setLongitude(12.5683);
        newTrip.setLatitude(55.6761);
        newTrip.setPrice(299.99);
        newTrip.setCategory(TripCategory.CITY);

        TripDTO createdTrip = given()
            .contentType("application/json")
            .header("Authorization", userToken)
            .body(newTrip)
            .when()
            .post(BASE_URL + "/trips")
            .then()
            .statusCode(201)
            .extract()
            .as(TripDTO.class);

        assertThat(createdTrip.getId(), notNullValue());
        assertThat(createdTrip.getName(), equalTo("Test Trip"));
    }

    @Test
    void updateTrip() {
        trip1.setName("Updated Trip Name");
        trip1.setPrice(499.99);

        TripDTO updatedTrip = given()
            .contentType("application/json")
            .header("Authorization", userToken)
            .body(trip1)
            .when()
            .put(BASE_URL + "/trips/" + trip1.getId())
            .then()
            .statusCode(200)
            .extract()
            .as(TripDTO.class);

        assertThat(updatedTrip.getName(), equalTo("Updated Trip Name"));
        assertThat(updatedTrip.getPrice(), equalTo(499.99));
    }

    @Test
    void deleteTrip() {
        given()
            .contentType("application/json")
            .header("Authorization", adminToken)
            .when()
            .delete(BASE_URL + "/trips/" + trip1.getId())
            .then()
            .statusCode(204);

        // Verify the trip is deleted
        given()
            .contentType("application/json")
            .when()
            .get(BASE_URL + "/trips/" + trip1.getId())
            .then()
            .statusCode(404);
    }

    @Test
    void testGetTripsByCategory() {
        List<TripDTO> cityTrips = given()
            .contentType("application/json")
            .when()
            .get(BASE_URL + "/trips/category/CITY")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<TripDTO>>() {});

        assertThat(cityTrips, hasSize(1));
        assertThat(cityTrips.get(0).getCategory(), equalTo(TripCategory.CITY));
    }

    @Test
    void testGuidesTotalPrices() {
        List<Map<String, Object>> guideTotals = given()
            .contentType("application/json")
            .when()
            .get(BASE_URL + "/trips/guides/totalprice")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<Map<String, Object>>>() {});

        assertThat(guideTotals, hasSize(2));
        assertThat(guideTotals.get(0), hasKey("guideId"));
        assertThat(guideTotals.get(0), hasKey("totalPrice"));
    }

    @Test
    void testUnauthorizedAccess() {
        TripDTO newTrip = new TripDTO();
        newTrip.setName("Unauthorized Trip");
        newTrip.setStartTime(ZonedDateTime.now().plusDays(1));
        newTrip.setEndTime(ZonedDateTime.now().plusDays(1).plusHours(2));
        newTrip.setLongitude(12.5683);
        newTrip.setLatitude(55.6761);
        newTrip.setPrice(299.99);
        newTrip.setCategory(TripCategory.CITY);

        given()
            .contentType("application/json")
            .body(newTrip)
            .when()
            .post(BASE_URL + "/trips")
            .then()
            .statusCode(401);
    }
}