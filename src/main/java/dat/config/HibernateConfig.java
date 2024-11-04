package dat.config;

import dat.entities.Appointment;
import dat.entities.Doctor;
import dat.exceptions.ApiException;
import dat.security.entities.Role;
import dat.security.entities.User;
import dat.utils.Utils;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

/**
 * Configuration class for Hibernate/JPA setup
 * Provides EntityManagerFactory for both production and test environments
 */
public class HibernateConfig {
    private static EntityManagerFactory emf;
    private static EntityManagerFactory emfTest;
    private static Boolean isTest = false;

    // Test mode configuration
    public static void setTest(Boolean test) {
        isTest = test;
    }

    public static Boolean getTest() {
        return isTest;
    }

    /**
     * Get EntityManagerFactory for production environment
     * Creates new EMF if none exists
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null)
            emf = createEMF(getTest());
        return emf;
    }

    /**
     * Get EntityManagerFactory specifically for testing
     * Creates new test EMF if none exists
     */
    public static EntityManagerFactory getEntityManagerFactoryForTest() {
        if (emfTest == null) {
            setTest(true);
            emfTest = createEMF(getTest());
        }
        return emfTest;
    }

    /**
     * Register all entity classes here
     * This method is called during EMF creation
     */
    private static void getAnnotationConfiguration(Configuration configuration) {
        configuration.addAnnotatedClass(Doctor.class);
        configuration.addAnnotatedClass(Appointment.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
    }

    /**
     * Create EntityManagerFactory with appropriate configuration
     * @param forTest whether to create test or production EMF
     */
    private static EntityManagerFactory createEMF(boolean forTest) {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();

            // Set base properties for Hibernate
            setBaseProperties(props);

            // Configure environment-specific properties
            if (forTest) {
                props = setTestProperties(props);
            } else if (System.getenv("DEPLOYED") != null) {
                props = setDeployedProperties(props);
            } else {
                props = setDevProperties(props);
            }

            configuration.setProperties(props);
            getAnnotationConfiguration(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

            SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
            return sf.unwrap(EntityManagerFactory.class);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Configure base Hibernate properties
     * These are common across all environments
     */
    private static Properties setBaseProperties(Properties props) {
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.hbm2ddl.auto", "update"); // Automatically update schema
        props.put("hibernate.current_session_context_class", "thread");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.use_sql_comments", "true");
        // Configure proper character encoding for database connections
        props.put("hibernate.connection.CharSet", "utf8");
        props.put("hibernate.connection.characterEncoding", "utf8");
        props.put("hibernate.connection.useUnicode", "true");
        props.put("hibernate.connection.defaultNChar", "true");
        return props;
    }

    /**
     * Configure development environment properties
     */
    private static Properties setDevProperties(Properties props) throws ApiException {
        String DBName = Utils.getPropertyValue("DB_NAME", "config.properties");
        String connectionString = "jdbc:postgresql://localhost:5432/" + DBName;
        connectionString += "?characterEncoding=utf8&useUnicode=true";

        props.put("hibernate.connection.url", connectionString);
        props.put("hibernate.connection.username", "postgres");
        props.put("hibernate.connection.password", "postgres");
        return props;
    }

    /**
     * Configure production/deployed environment properties
     */
    private static Properties setDeployedProperties(Properties props) {
        String DBName = System.getenv("DB_NAME");
        String connectionString = System.getenv("CONNECTION_STR") + DBName;

        if (!connectionString.contains("?")) {
            connectionString += "?";
        } else {
            connectionString += "&";
        }
        connectionString += "characterEncoding=utf8&useUnicode=true";

        props.setProperty("hibernate.connection.url", connectionString);
        props.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        props.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
        return props;
    }

    /**
     * Configure test environment properties using TestContainers
     */
    private static Properties setTestProperties(Properties props) {
        props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
        props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test_db?characterEncoding=utf8&useUnicode=true");
        props.put("hibernate.connection.username", "postgres");
        props.put("hibernate.connection.password", "postgres");
        props.put("hibernate.archive.autodetection", "class");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "create-drop"); // Clean database after each test
        return props;
    }
}