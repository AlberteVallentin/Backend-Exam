package dat.security.daos;

import dat.config.HibernateConfig;
import dat.security.entities.Role;
import dat.security.entities.User;
import dat.security.enums.RoleType;
import dat.security.exceptions.SecurityValidationException;
import dat.security.token.UserDTO;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityDAOTest {
    private static EntityManagerFactory emf;
    private static SecurityDAO securityDAO;
    private static final String TEST_USER_EMAIL = "test@test.dk";
    private static final String TEST_USER_PASSWORD = "test123";
    private static final String TEST_USER_NAME = "Test User";

    @BeforeAll
    void setUpClass() {
        // Use test database
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        securityDAO = new SecurityDAO(emf);
    }

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // Clean up existing data
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();

            // Create test roles
            Role userRole = new Role(RoleType.USER);
            em.persist(userRole);

            // Create test user
            User user = new User(TEST_USER_NAME, TEST_USER_EMAIL, TEST_USER_PASSWORD, userRole);
            em.persist(user);

            em.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDown() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @AfterAll
    void tearDownClass() {
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    @DisplayName("Test successful user verification")
    void testGetVerifiedUser() throws SecurityValidationException {
        // Act
        UserDTO verifiedUser = securityDAO.getVerifiedUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

        // Assert
        assertNotNull(verifiedUser, "Verified user should not be null");
        assertEquals(TEST_USER_EMAIL, verifiedUser.getEmail(), "Email should match");
        assertEquals(RoleType.USER, verifiedUser.getRoleType(), "Role should match");
    }

    @Test
    @DisplayName("Test verification with invalid password")
    void testGetVerifiedUserInvalidPassword() {
        // Arrange
        String wrongPassword = "wrongPassword";

        // Act & Assert
        SecurityValidationException thrown = assertThrows(
            SecurityValidationException.class,
            () -> securityDAO.getVerifiedUser(TEST_USER_EMAIL, wrongPassword),
            "Should throw ValidationException for invalid password"
        );

        assertTrue(thrown.getMessage().contains("Invalid email or password"));
    }

    @Test
    @DisplayName("Test create user success")
    void testCreateUser() throws SecurityValidationException {
        // Arrange
        String newEmail = "new@test.dk";

        // Act
        User createdUser = securityDAO.createUser("New User", newEmail, "password123", RoleType.USER);

        // Assert
        assertNotNull(createdUser, "Created user should not be null");
        assertEquals(newEmail, createdUser.getEmail(), "Email should match");
        assertEquals(RoleType.USER, createdUser.getRole().getRoleType(), "Role should match");
    }

    @Test
    @DisplayName("Test create user with existing email")
    void testCreateUserExistingEmail() {
        // Act & Assert
        EntityExistsException thrown = assertThrows(
            EntityExistsException.class,
            () -> securityDAO.createUser(TEST_USER_NAME, TEST_USER_EMAIL, TEST_USER_PASSWORD, RoleType.USER),
            "Should throw EntityExistsException for existing email"
        );

        assertTrue(thrown.getMessage().contains("User with email: " + TEST_USER_EMAIL + " already exists"));
    }

    @Test
    @DisplayName("Test add role to user")
    void testAddRole() throws SecurityValidationException {
        // Arrange
        UserDTO userDTO = new UserDTO(TEST_USER_EMAIL, RoleType.USER);

        // First ensure role exists
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Role adminRole = new Role(RoleType.ADMIN);
            em.persist(adminRole);
            em.getTransaction().commit();
        }

        // Act
        User updatedUser = securityDAO.addRole(userDTO, "ADMIN");

        // Assert
        assertNotNull(updatedUser, "Updated user should not be null");
        assertEquals(RoleType.ADMIN, updatedUser.getRole().getRoleType(), "Role should be updated to ADMIN");
    }
}