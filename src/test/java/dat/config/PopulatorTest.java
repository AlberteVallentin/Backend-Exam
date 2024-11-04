package dat.config;

import dat.dtos.TripDTO;
import dat.entities.Trip;
import dat.entities.Guide;
import dat.enums.TripCategory;
import dat.security.entities.User;
import dat.security.entities.Role;
import dat.security.token.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.ZonedDateTime;

import static dat.security.enums.RoleType.ADMIN;
import static dat.security.enums.RoleType.USER;

public class PopulatorTest {

    public static UserDTO[] populateUsers(EntityManagerFactory emf) {
        User user, admin;
        Role userRole, adminRole;

        // Define USER and ADMIN roles
        userRole = new Role(USER);
        adminRole = new Role(ADMIN);

        // Create users with hashed passwords and assign roles
        user = new User("User Test", "user@test.com", "user123", userRole);
        admin = new User("Admin Test", "admin@test.com", "admin123", adminRole);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // Persist roles and users
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();
        }

        // Return DTOs for testing purposes
        UserDTO userDTO = new UserDTO(user.getEmail(), "user123");
        UserDTO adminDTO = new UserDTO(admin.getEmail(), "admin123");
        return new UserDTO[]{userDTO, adminDTO};
    }

    public static TripDTO[] populateTrips(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Create first guide
            Guide guide1 = new Guide();
            guide1.setFirstName("John");
            guide1.setLastName("Smith");
            guide1.setEmail("john.smith@guides.com");
            guide1.setPhone("+45 12345678");
            guide1.setYearsOfExperience(5);

            // Create second guide
            Guide guide2 = new Guide();
            guide2.setFirstName("Sarah");
            guide2.setLastName("Johnson");
            guide2.setEmail("sarah.j@guides.com");
            guide2.setPhone("+45 87654321");
            guide2.setYearsOfExperience(8);

            // Create first trip
            Trip trip1 = new Trip();
            trip1.setName("Copenhagen City Walk");
            trip1.setStartTime(ZonedDateTime.now().plusDays(1).withHour(10).withMinute(0));
            trip1.setEndTime(ZonedDateTime.now().plusDays(1).withHour(12).withMinute(0));
            trip1.setLongitude(12.5683);
            trip1.setLatitude(55.6761);
            trip1.setPrice(299.99);
            trip1.setCategory(TripCategory.CITY);
            guide1.addTrip(trip1);

            // Create second trip
            Trip trip2 = new Trip();
            trip2.setName("Amager Beach Experience");
            trip2.setStartTime(ZonedDateTime.now().plusDays(2).withHour(14).withMinute(0));
            trip2.setEndTime(ZonedDateTime.now().plusDays(2).withHour(17).withMinute(0));
            trip2.setLongitude(12.6347);
            trip2.setLatitude(55.6582);
            trip2.setPrice(399.99);
            trip2.setCategory(TripCategory.BEACH);
            guide1.addTrip(trip2);

            // Create third trip
            Trip trip3 = new Trip();
            trip3.setName("Dyrehaven Forest Tour");
            trip3.setStartTime(ZonedDateTime.now().plusDays(3).withHour(9).withMinute(0));
            trip3.setEndTime(ZonedDateTime.now().plusDays(3).withHour(13).withMinute(0));
            trip3.setLongitude(12.5693);
            trip3.setLatitude(55.7832);
            trip3.setPrice(449.99);
            trip3.setCategory(TripCategory.FOREST);
            guide2.addTrip(trip3);

            // Create fourth trip
            Trip trip4 = new Trip();
            trip4.setName("Øresund Sea Adventure");
            trip4.setStartTime(ZonedDateTime.now().plusDays(4).withHour(11).withMinute(0));
            trip4.setEndTime(ZonedDateTime.now().plusDays(4).withHour(15).withMinute(0));
            trip4.setLongitude(12.6298);
            trip4.setLatitude(55.7069);
            trip4.setPrice(599.99);
            trip4.setCategory(TripCategory.SEA);
            guide2.addTrip(trip4);

            // Persist guides (will cascade to trips)
            em.persist(guide1);
            em.persist(guide2);

            em.getTransaction().commit();

            System.out.println("Test database populated with trips and guides!");

            // Return DTOs for test purposes
            return new TripDTO[]{
                new TripDTO(trip1),
                new TripDTO(trip2),
                new TripDTO(trip3),
                new TripDTO(trip4)
            };
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not populate test database: " + e.getMessage());
            return new TripDTO[0];
        }
    }
}