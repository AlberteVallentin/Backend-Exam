package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.DoctorDTO;
import dat.enums.Speciality;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class DoctorMockDAO implements IDAO<DoctorDTO, Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorMockDAO.class);
    private static final Map<Integer, DoctorDTO> doctorMap = new HashMap<>();
    private static int idCounter = 1;
    private static DoctorMockDAO instance;

    private DoctorMockDAO() {
        populate();
    }

    public static DoctorMockDAO getInstance() {
        if (instance == null) {
            instance = new DoctorMockDAO();
        }
        return instance;
    }

    @Override
    public List<DoctorDTO> readAll() {
        if (doctorMap.isEmpty()) {
            throw new EntityNotFoundException("No doctors found in the database");
        }
        return new ArrayList<>(doctorMap.values());
    }

    @Override
    public DoctorDTO read(Integer id) {
        DoctorDTO doctor = doctorMap.get(id);
        if (doctor == null) {
            throw new EntityNotFoundException("Doctor not found with id: " + id);
        }
        return doctor;
    }

    @Override
    public DoctorDTO create(DoctorDTO doctor) {
        doctor.setId(idCounter++);
        doctorMap.put(doctor.getId(), doctor);
        LOGGER.info("Created doctor with id: {}", doctor.getId());
        return doctor;
    }

    @Override
    public DoctorDTO update(Integer id, DoctorDTO doctor) {
        if (!doctorMap.containsKey(id)) {
            throw new EntityNotFoundException("Doctor not found with id: " + id);
        }
        doctor.setId(id);
        doctorMap.replace(id, doctor);
        LOGGER.info("Updated doctor with id: {}", id);
        return doctor;
    }

    @Override
    public void delete(Integer id) {
        if (doctorMap.remove(id) == null) {
            throw new EntityNotFoundException("Doctor not found with id: " + id);
        }
        LOGGER.info("Deleted doctor with id: {}", id);
    }

    public List<DoctorDTO> doctorBySpeciality(Speciality speciality) {
        List<DoctorDTO> doctors = doctorMap.values().stream()
            .filter(doctor -> doctor.getSpeciality() == speciality)
            .collect(Collectors.toList());

        if (doctors.isEmpty()) {
            throw new EntityNotFoundException("No doctors found with speciality: " + speciality);
        }
        return doctors;
    }

    public List<DoctorDTO> doctorByBirthdateRange(LocalDate from, LocalDate to) {
        List<DoctorDTO> doctors = doctorMap.values().stream()
            .filter(doctor -> !doctor.getDateOfBirth().isBefore(from) && !doctor.getDateOfBirth().isAfter(to))
            .collect(Collectors.toList());

        if (doctors.isEmpty()) {
            throw new EntityNotFoundException(
                String.format("No doctors found with birth dates between %s and %s", from, to));
        }
        return doctors;
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return doctorMap.containsKey(id);
    }

    private void populate() {
        try {
            create(new DoctorDTO("Dr. Alice Smith", LocalDate.of(1975, 4, 12), 2000, "City Health Clinic", Speciality.FAMILY_MEDICINE));
            create(new DoctorDTO("Dr. Bob Johnson", LocalDate.of(1980, 8, 5), 2005, "Downtown Medical Center", Speciality.SURGERY));
            create(new DoctorDTO("Dr. Clara Lee", LocalDate.of(1983, 7, 22), 2008, "Green Valley Hospital", Speciality.PEDIATRICS));
            create(new DoctorDTO("Dr. David Park", LocalDate.of(1978, 11, 15), 2003, "Hillside Medical Practice", Speciality.PSYCHIATRY));
            create(new DoctorDTO("Dr. Emily White", LocalDate.of(1982, 9, 30), 2007, "Metro Health Center", Speciality.GERIATRICS));
            create(new DoctorDTO("Dr. Fiona Martinez", LocalDate.of(1985, 2, 17), 2010, "Riverside Wellness Clinic", Speciality.SURGERY));
            create(new DoctorDTO("Dr. George Kim", LocalDate.of(1979, 5, 29), 2004, "Summit Health Institute", Speciality.FAMILY_MEDICINE));
            LOGGER.info("Successfully populated mock database with {} doctors", doctorMap.size());
        } catch (Exception e) {
            LOGGER.error("Error populating mock database: {}", e.getMessage());
        }
    }
}