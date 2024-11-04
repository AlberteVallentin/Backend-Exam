import dat.config.HibernateConfig;
import dat.daos.impl.DoctorDAO;
import dat.dtos.DoctorDTO;
import dat.entities.Doctor;
import dat.enums.Speciality;
import dat.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DoctorDAOTest {
    private static EntityManagerFactory emf;
    private static DoctorDAO dao;
    private Doctor testDoctor1;
    private Doctor testDoctor2;

    private static final LocalDate TEST_DOC1_BIRTH = LocalDate.of(1975, 4, 12);
    private static final LocalDate TEST_DOC2_BIRTH = LocalDate.of(1980, 8, 5);
    private static final String TEST_DOC1_NAME = "Dr. Test One";
    private static final String TEST_DOC2_NAME = "Dr. Test Two";

    @BeforeAll
    void setUpClass() {
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        dao = DoctorDAO.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Doctor").executeUpdate();

            testDoctor1 = new Doctor();
            testDoctor1.setName(TEST_DOC1_NAME);
            testDoctor1.setDateOfBirth(TEST_DOC1_BIRTH);
            testDoctor1.setYearOfGraduation(2000);
            testDoctor1.setNameOfClinic("Test Clinic One");
            testDoctor1.setSpeciality(Speciality.FAMILY_MEDICINE);
            em.persist(testDoctor1);

            testDoctor2 = new Doctor();
            testDoctor2.setName(TEST_DOC2_NAME);
            testDoctor2.setDateOfBirth(TEST_DOC2_BIRTH);
            testDoctor2.setYearOfGraduation(2005);
            testDoctor2.setNameOfClinic("Test Clinic Two");
            testDoctor2.setSpeciality(Speciality.SURGERY);
            em.persist(testDoctor2);

            em.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDown() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Doctor").executeUpdate();
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
    void testGetByIdExistingDoctor() throws ApiException {
        DoctorDTO found = dao.getById(testDoctor1.getId());
        assertNotNull(found);
        assertEquals(TEST_DOC1_NAME, found.getName());
    }

    @Test
    void testGetByIdNonExistentDoctor() {
        ApiException exception = assertThrows(ApiException.class, () -> dao.getById(999999));
        assertEquals("Doctor not found with id: 999999", exception.getMessage());
    }

    @Test
    void testGetByIdAll() throws ApiException {
        List<DoctorDTO> doctors = dao.getAll();
        assertNotNull(doctors);
        assertEquals(2, doctors.size());
    }

    @Test
    void testCreateDoctor() throws ApiException {
        DoctorDTO newDoctor = new DoctorDTO();
        newDoctor.setName("Dr. New");
        newDoctor.setDateOfBirth(LocalDate.of(1985, 1, 1));
        newDoctor.setYearOfGraduation(2010);
        newDoctor.setNameOfClinic("New Clinic");
        newDoctor.setSpeciality(Speciality.PEDIATRICS);

        DoctorDTO created = dao.create(newDoctor);
        assertNotNull(created);
        assertEquals("Dr. New", created.getName());
    }

    @Test
    void testUpdateDoctor() throws ApiException {
        String updatedClinic = "Updated Clinic";
        DoctorDTO updateData = new DoctorDTO();
        updateData.setName(testDoctor1.getName());
        updateData.setDateOfBirth(testDoctor1.getDateOfBirth());
        updateData.setYearOfGraduation(2001);
        updateData.setNameOfClinic(updatedClinic);
        updateData.setSpeciality(testDoctor1.getSpeciality());

        DoctorDTO updated = dao.update(testDoctor1.getId(), updateData);
        assertNotNull(updated);
        assertEquals(updatedClinic, updated.getNameOfClinic());
    }

    @Test
    void testDeleteDoctor() throws ApiException {
        dao.delete(testDoctor2.getId());
        ApiException exception = assertThrows(ApiException.class, () -> dao.getById(testDoctor2.getId()));
        assertEquals("Doctor not found with id: " + testDoctor2.getId(), exception.getMessage());
    }

    @Test
    void testFindDoctorsBySpeciality() throws ApiException {
        List<DoctorDTO> surgeons = dao.doctorBySpeciality(Speciality.SURGERY);
        assertEquals(1, surgeons.size());
        assertEquals(TEST_DOC2_NAME, surgeons.get(0).getName());
    }

    @Test
    void testFindDoctorsByBirthdateRange() throws ApiException {
        List<DoctorDTO> doctors = dao.doctorByBirthdateRange(LocalDate.of(1970, 1, 1), LocalDate.of(1979, 12, 31));
        assertEquals(1, doctors.size());
        assertEquals(TEST_DOC1_NAME, doctors.get(0).getName());
    }

    @Test
    void testValidatePrimaryKey() throws ApiException {
        assertTrue(dao.validatePrimaryKey(testDoctor1.getId()));
        assertFalse(dao.validatePrimaryKey(999999));
    }
}
