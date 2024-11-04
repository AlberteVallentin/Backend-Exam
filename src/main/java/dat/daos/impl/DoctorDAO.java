package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.DoctorDTO;
import dat.entities.Doctor;
import dat.enums.Speciality;
import dat.exceptions.ApiException;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DoctorDAO implements IDAO<DoctorDTO, Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorDAO.class);
    private static DoctorDAO instance;
    private static EntityManagerFactory emf;

    public static DoctorDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new DoctorDAO();
        }
        return instance;
    }

    @Override
    public DoctorDTO read(Integer id) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            Doctor doctor = em.find(Doctor.class, id);
            if (doctor == null) {
                throw new ApiException(400, "Doctor not found with id: " + id);
            }
            LOGGER.info("Retrieved doctor with id: {}", id);
            return new DoctorDTO(doctor);
        } catch (PersistenceException e) {
            LOGGER.error("Database error during read operation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during read operation");
        }
    }

    @Override
    public List<DoctorDTO> readAll() throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Doctor> query = em.createQuery("SELECT d FROM Doctor d", Doctor.class);
            List<DoctorDTO> doctors = query.getResultList().stream()
                .map(DoctorDTO::new)
                .collect(Collectors.toList());

            if (doctors.isEmpty()) {
                throw new ApiException(400, "No doctors found in the database");
            }

            LOGGER.info("Retrieved {} doctors", doctors.size());
            return doctors;
        } catch (PersistenceException e) {
            LOGGER.error("Database error during readAll operation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during readAll operation");
        }
    }

    @Override
    public DoctorDTO create(DoctorDTO doctorDTO) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Doctor> query = em.createQuery(
                "SELECT d FROM Doctor d WHERE d.name = :name AND d.nameOfClinic = :clinic",
                Doctor.class);
            query.setParameter("name", doctorDTO.getName());
            query.setParameter("clinic", doctorDTO.getNameOfClinic());

            if (!query.getResultList().isEmpty()) {
                throw new ApiException(400, "Doctor already exists with name: " + doctorDTO.getName() +
                    " at clinic: " + doctorDTO.getNameOfClinic());
            }

            em.getTransaction().begin();
            Doctor doctor = new Doctor(doctorDTO);
            em.persist(doctor);
            em.getTransaction().commit();

            LOGGER.info("Created doctor with id: {}", doctor.getId());
            return new DoctorDTO(doctor);
        } catch (PersistenceException e) {
            LOGGER.error("Database error during create operation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during create operation");
        }
    }

    @Override
    public DoctorDTO update(Integer id, DoctorDTO doctorDTO) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Doctor doctor = em.find(Doctor.class, id);
            if (doctor == null) {
                throw new ApiException(400, "Doctor not found with id: " + id);
            }

            doctor.setName(doctorDTO.getName());
            doctor.setDateOfBirth(doctorDTO.getDateOfBirth());
            doctor.setYearOfGraduation(doctorDTO.getYearOfGraduation());
            doctor.setNameOfClinic(doctorDTO.getNameOfClinic());
            doctor.setSpeciality(doctorDTO.getSpeciality());

            Doctor mergedDoctor = em.merge(doctor);
            em.getTransaction().commit();

            LOGGER.info("Updated doctor with id: {}", id);
            return new DoctorDTO(mergedDoctor);
        } catch (PersistenceException e) {
            LOGGER.error("Database error during update operation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during update operation");
        }
    }

    @Override
    public void delete(Integer id) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Doctor doctor = em.find(Doctor.class, id);
            if (doctor == null) {
                throw new ApiException(400, "Doctor not found with id: " + id);
            }
            em.remove(doctor);
            em.getTransaction().commit();
            LOGGER.info("Deleted doctor with id: {}", id);
        } catch (PersistenceException e) {
            LOGGER.error("Database error during delete operation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during delete operation");
        }
    }


    @Override
    public boolean validatePrimaryKey(Integer id) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Doctor.class, id) != null;
        } catch (PersistenceException e) {
            LOGGER.error("Database error during primary key validation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during primary key validation");
        }
    }

    public List<DoctorDTO> doctorBySpeciality(Speciality speciality) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Doctor> query = em.createQuery(
                "SELECT d FROM Doctor d WHERE d.speciality = :speciality",
                Doctor.class);
            query.setParameter("speciality", speciality);

            List<DoctorDTO> doctors = query.getResultList().stream()
                .map(DoctorDTO::new)
                .collect(Collectors.toList());

            if (doctors.isEmpty()) {
                throw new ApiException(400, "No doctors found with speciality: " + speciality);
            }

            LOGGER.info("Found {} doctors with speciality: {}", doctors.size(), speciality);
            return doctors;
        } catch (PersistenceException e) {
            LOGGER.error("Database error while fetching doctors by speciality: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred while fetching doctors by speciality");
        }
    }

    public List<DoctorDTO> doctorByBirthdateRange(LocalDate from, LocalDate to) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Doctor> query = em.createQuery(
                "SELECT d FROM Doctor d WHERE d.dateOfBirth BETWEEN :from AND :to",
                Doctor.class);
            query.setParameter("from", from);
            query.setParameter("to", to);

            List<DoctorDTO> doctors = query.getResultList().stream()
                .map(DoctorDTO::new)
                .collect(Collectors.toList());

            if (doctors.isEmpty()) {
                throw new ApiException(400, String.format("No doctors found with birth dates between %s and %s", from, to));
            }

            LOGGER.info("Found {} doctors in birthdate range {} to {}", doctors.size(), from, to);
            return doctors;
        } catch (PersistenceException e) {
            LOGGER.error("Database error while fetching doctors by birthdate range: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred while fetching doctors by birthdate range");
        }
    }
}