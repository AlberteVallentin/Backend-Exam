package dat.config;

import dat.entities.Trip;
import dat.entities.Guide;
import dat.enums.TripCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;

public class Populator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Populator.class);

    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        populate(emf);
    }

    public static void populate(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            LOGGER.info("Starting database population check...");

            // Check if data already exists
            TypedQuery<Long> guideCountQuery = em.createQuery(
                "SELECT COUNT(g) FROM Guide g WHERE g.email IN :emails", Long.class);
            guideCountQuery.setParameter("emails",
                List.of("john.smith@guides.com", "sarah.j@guides.com"));
            long existingGuides = guideCountQuery.getSingleResult();

            if (existingGuides > 0) {
                LOGGER.info("Database already populated. Found {} existing guides.", existingGuides);
                return;
            }

            LOGGER.info("No existing data found. Starting population...");
            em.getTransaction().begin();

            // Create guides
            LOGGER.info("Creating guides...");
            Guide guide1 = new Guide();
            guide1.setFirstName("John");
            guide1.setLastName("Smith");
            guide1.setEmail("john.smith@guides.com");
            guide1.setPhone("+45 12345678");
            guide1.setYearsOfExperience(5);

            Guide guide2 = new Guide();
            guide2.setFirstName("Sarah");
            guide2.setLastName("Johnson");
            guide2.setEmail("sarah.j@guides.com");
            guide2.setPhone("+45 87654321");
            guide2.setYearsOfExperience(8);

            // Create trips
            LOGGER.info("Creating trips...");
            Trip trip1 = new Trip();
            trip1.setName("Copenhagen City Walk");
            trip1.setStartTime(ZonedDateTime.now().plusDays(1).withHour(10).withMinute(0));
            trip1.setEndTime(ZonedDateTime.now().plusDays(1).withHour(12).withMinute(0));
            trip1.setLongitude(12.5683);
            trip1.setLatitude(55.6761);
            trip1.setPrice(299.99);
            trip1.setCategory(TripCategory.CITY);
            guide1.addTrip(trip1);
            LOGGER.debug("Created trip: {} with guide: {}", trip1.getName(), guide1.getFirstName());

            Trip trip2 = new Trip();
            trip2.setName("Amager Beach Experience");
            trip2.setStartTime(ZonedDateTime.now().plusDays(2).withHour(14).withMinute(0));
            trip2.setEndTime(ZonedDateTime.now().plusDays(2).withHour(17).withMinute(0));
            trip2.setLongitude(12.6347);
            trip2.setLatitude(55.6582);
            trip2.setPrice(399.99);
            trip2.setCategory(TripCategory.BEACH);
            guide1.addTrip(trip2);
            LOGGER.debug("Created trip: {} with guide: {}", trip2.getName(), guide1.getFirstName());

            Trip trip3 = new Trip();
            trip3.setName("Dyrehaven Forest Tour");
            trip3.setStartTime(ZonedDateTime.now().plusDays(3).withHour(9).withMinute(0));
            trip3.setEndTime(ZonedDateTime.now().plusDays(3).withHour(13).withMinute(0));
            trip3.setLongitude(12.5693);
            trip3.setLatitude(55.7832);
            trip3.setPrice(449.99);
            trip3.setCategory(TripCategory.FOREST);
            guide2.addTrip(trip3);
            LOGGER.debug("Created trip: {} with guide: {}", trip3.getName(), guide2.getFirstName());

            Trip trip4 = new Trip();
            trip4.setName("Øresund Sea Adventure");
            trip4.setStartTime(ZonedDateTime.now().plusDays(4).withHour(11).withMinute(0));
            trip4.setEndTime(ZonedDateTime.now().plusDays(4).withHour(15).withMinute(0));
            trip4.setLongitude(12.6298);
            trip4.setLatitude(55.7069);
            trip4.setPrice(599.99);
            trip4.setCategory(TripCategory.SEA);
            guide2.addTrip(trip4);
            LOGGER.debug("Created trip: {} with guide: {}", trip4.getName(), guide2.getFirstName());

            // Persist guides (will cascade to trips)
            LOGGER.info("Persisting guides and their associated trips...");
            em.persist(guide1);
            em.persist(guide2);

            em.getTransaction().commit();
            LOGGER.info("Successfully populated database with {} guides and {} trips", 2, 4);

        } catch (Exception e) {
            LOGGER.error("Error populating database: {}", e.getMessage(), e);
            throw new RuntimeException("Could not populate database", e);
        }
    }
}