package dat.config;

import dat.dtos.DoctorDTO;
import dat.entities.Doctor;
import dat.entities.Appointment;
import dat.enums.Speciality;
import dat.security.entities.User;
import dat.security.entities.Role;
import dat.security.token.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static dat.security.enums.RoleType.ADMIN;
import static dat.security.enums.RoleType.USER;

public class PopulatorTest {

    public static UserDTO[] populateUsers(EntityManagerFactory emf) {
        User user, admin;
        Role userRole, adminRole;

        // Definer rollerne USER og ADMIN
        userRole = new Role(USER);
        adminRole = new Role(ADMIN);

        // Opret brugere med hash'et adgangskode og tildel roller
        user = new User("User Test", "user@test.com", "user123", userRole);
        admin = new User("Admin Test", "admin@test.com", "admin123", adminRole);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // Persist roller og brugere
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();
        }

        // Returner DTO'er for testformål
        UserDTO userDTO = new UserDTO(user.getEmail(), "user123");
        UserDTO adminDTO = new UserDTO(admin.getEmail(), "admin123");
        return new UserDTO[]{userDTO, adminDTO};
    }


    public static DoctorDTO[] populateDoctors(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Opret første læge
            Doctor doctor1 = new Doctor();
            doctor1.setName("Dr. Alice Smith");
            doctor1.setDateOfBirth(LocalDate.of(1975, 4, 12));
            doctor1.setYearOfGraduation(2000);
            doctor1.setNameOfClinic("City Health Clinic");
            doctor1.setSpeciality(Speciality.FAMILY_MEDICINE);

            // Tilføj aftaler til doctor1
            Appointment app1 = new Appointment();
            app1.setClientName("John Smith");
            app1.setDate(LocalDate.now().plusDays(1));
            app1.setTime(LocalTime.of(9, 45));
            app1.setComment("First visit");
            doctor1.addAppointment(app1);

            Appointment app2 = new Appointment();
            app2.setClientName("Alice Johnson");
            app2.setDate(LocalDate.now().plusDays(2));
            app2.setTime(LocalTime.of(10, 30));
            app2.setComment("Follow up");
            doctor1.addAppointment(app2);

            // Opret anden læge
            Doctor doctor2 = new Doctor();
            doctor2.setName("Dr. Bob Johnson");
            doctor2.setDateOfBirth(LocalDate.of(1980, 8, 5));
            doctor2.setYearOfGraduation(2005);
            doctor2.setNameOfClinic("Downtown Medical Center");
            doctor2.setSpeciality(Speciality.SURGERY);

            // Tilføj aftaler til doctor2
            Appointment app3 = new Appointment();
            app3.setClientName("Emily White");
            app3.setDate(LocalDate.now().plusDays(3));
            app3.setTime(LocalTime.of(14, 0));
            app3.setComment("General check");
            doctor2.addAppointment(app3);

            Appointment app4 = new Appointment();
            app4.setClientName("David Martinez");
            app4.setDate(LocalDate.now().plusDays(4));
            app4.setTime(LocalTime.of(11, 0));
            app4.setComment("Consultation");
            doctor2.addAppointment(app4);

            // Persist læger (vil kaskadere til aftaler)
            em.persist(doctor1);
            em.persist(doctor2);

            em.getTransaction().commit();

            System.out.println("Database populated with doctors and appointments!");

            // Returner DTO'er for testformål
            return new DoctorDTO[]{new DoctorDTO(doctor1), new DoctorDTO(doctor2)};
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not populate database: " + e.getMessage());
            return new DoctorDTO[0];
        }
    }
}