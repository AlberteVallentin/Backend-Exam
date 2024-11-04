package dat.controllers.impl;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.config.PopulatorTest;
import dat.daos.impl.DoctorDAO;
import dat.dtos.DoctorDTO;
import dat.enums.Speciality;
import dat.security.controllers.SecurityController;
import dat.security.daos.SecurityDAO;
import dat.security.entities.User;
import dat.security.exceptions.SecurityValidationException;
import dat.security.token.UserDTO;
import io.javalin.Javalin;
import io.restassured.common.mapper.TypeRef;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DoctorControllerDBTest {

    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private final static SecurityController securityController = SecurityController.getInstance();
    private final static SecurityDAO securityDAO = new SecurityDAO(emf);
    private static Javalin app;
    private static DoctorDTO[] doctors;
    private static DoctorDTO doctor1, doctor2;
    private static UserDTO userDTO, adminDTO;
    private static String userToken, adminToken;
    private static final String BASE_URL = "http://localhost:7070/api";

    @BeforeAll
    void setUpAll() {
        HibernateConfig.setTest(true);

        // Start API
        app = ApplicationConfig.startServer(7070);
    }

    @BeforeEach
    void setUp() {
        // Populate the database with doctors and users
        System.out.println("Populating database with doctors and users");
        doctors = PopulatorTest.populateDoctors(emf);
        doctor1 = doctors[0];
        doctor2 = doctors[1];
        UserDTO[] users = PopulatorTest.populateUsers(emf);
        userDTO = users[0];
        adminDTO = users[1];

        try {
            UserDTO verifiedUser = securityDAO.getVerifiedUser(userDTO.getEmail(), userDTO.getPassword());
            UserDTO verifiedAdmin = securityDAO.getVerifiedUser(adminDTO.getEmail(), adminDTO.getPassword());

            // Generer tokens for brugeren og administratoren
            userToken = "Bearer " + securityController.createToken(verifiedUser);
            adminToken = "Bearer " + securityController.createToken(verifiedAdmin);
        } catch (SecurityValidationException e) {
            throw new RuntimeException("Failed to verify users", e);
        }
    }


        @AfterEach
    void tearDown() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Appointment").executeUpdate();
            em.createQuery("DELETE FROM Doctor").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    void tearDownAll() {
        ApplicationConfig.stopServer(app);
    }

    @Test
    void readAll() {
        List<DoctorDTO> doctorDTOs =
            given()
                .when()
                .header("Authorization", userToken)
                .get(BASE_URL + "/doctors")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .log().all()
                .extract()
                .as(new TypeRef<List<DoctorDTO>>() {});

        assertThat(doctorDTOs.size(), is(2));
        assertThat(doctorDTOs.get(0).getName(), is("Dr. Alice Smith"));
        assertThat(doctorDTOs.get(1).getName(), is("Dr. Bob Johnson"));
    }

    @Test
    void readBySpeciality() {
        List<DoctorDTO> doctorDTOs =
            given()
                .when()
                .header("Authorization", userToken)
                .get(BASE_URL + "/doctors/speciality/FAMILY_MEDICINE")
                .then()
                .statusCode(200)
                .log().all()
                .extract()
                .as(new TypeRef<List<DoctorDTO>>() {});

        assertThat(doctorDTOs.size(), is(1));
        assertThat(doctorDTOs.get(0).getSpeciality(), is(Speciality.FAMILY_MEDICINE));
    }

    @Test
    void createNewDoctor() {
        // JSON payload for at oprette en ny doctor
        String doctorJson = """
    {
      "name": "Dr. Sophus Olsson",
      "dateOfBirth": "1980-05-21",
      "yearOfGraduation": 2008,
      "nameOfClinic": "Green Valley Hospital",
      "speciality": "PEDIATRICS"
    }
    """;

        // Send POST-anmodning til at oprette en ny doctor
        DoctorDTO createdDoctor =
            given()
                .contentType("application/json")
                .header("Authorization", userToken)
                .body(doctorJson)
                .when()
                .post(BASE_URL + "/doctors")
                .then()
                .statusCode(201)
                .extract()
                .as(DoctorDTO.class);

        assertThat(createdDoctor.getName(), is("Dr. Sophus Olsson"));
        assertThat(createdDoctor.getDateOfBirth(), is(LocalDate.of(1980, 5, 21)));
        assertThat(createdDoctor.getYearOfGraduation(), is(2008));
        assertThat(createdDoctor.getNameOfClinic(), is("Green Valley Hospital"));
        assertThat(createdDoctor.getSpeciality(), is(Speciality.PEDIATRICS));
    }

    @Test
    void createNewDoctorWithAdminTokenShouldFail() {
        // JSON payload for at oprette en ny doctor
        String doctorJson = """
    {
      "name": "Dr. Sophus Olsson",
      "dateOfBirth": "1980-05-21",
      "yearOfGraduation": 2008,
      "nameOfClinic": "Green Valley Hospital",
      "speciality": "PEDIATRICS"
    }
    """;

        // Send POST-anmodning med adminToken og forvent, at det fejler
        given()
            .contentType("application/json")
            .header("Authorization", adminToken) // Bruger adminToken i stedet for userToken
            .body(doctorJson)
            .when()
            .post(BASE_URL + "/doctors")
            .then()
            .statusCode(403) // Forvent, at statuskoden er 403 Forbidden
            .log().all(); // Log detaljer om svaret til fejlsøgning
    }

    @Test
    void createNewDoctorWithoutTokenShouldFail() {
        // JSON payload for at oprette en ny doctor
        String doctorJson = """
    {
      "name": "Dr. Sophus Olsson",
      "dateOfBirth": "1980-05-21",
      "yearOfGraduation": 2008,
      "nameOfClinic": "Green Valley Hospital",
      "speciality": "PEDIATRICS"
    }
    """;

        // Send POST-anmodning uden autorisationstoken og forvent, at det fejler
        given()
            .contentType("application/json")
            .body(doctorJson)
            .when()
            .post(BASE_URL + "/doctors")
            .then()
            .statusCode(401) // Forvent, at statuskoden er 401 Unauthorized
            .log().all(); // Log detaljer om svaret til fejlsøgning
    }



}
