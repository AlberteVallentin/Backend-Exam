package dat.controllers.impl;

import dat.controllers.IController;
import dat.daos.impl.DoctorMockDAO;
import dat.dtos.DoctorDTO;
import dat.enums.Speciality;
import dat.exceptions.ValidationException;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoctorMockController implements IController<DoctorDTO, Integer> {
    private final DoctorMockDAO dao;

    public DoctorMockController() {
        this.dao = DoctorMockDAO.getInstance();
    }

    @Override
    public void read(Context ctx) throws ValidationException {
        int id = validateAndGetId(ctx);
        DoctorDTO doctor = dao.getById(id);
        ctx.json(doctor);
        ctx.status(200);
    }

    @Override
    public void readAll(Context ctx) {
        List<DoctorDTO> doctors = dao.getAll();
        ctx.json(doctors);
        ctx.status(200);
    }

    @Override
    public void create(Context ctx) throws ValidationException {
        DoctorDTO doctorDTO = validateEntity(ctx);
        validateBusinessRules(doctorDTO);

        DoctorDTO createdDoctor = dao.create(doctorDTO);
        ctx.json(createdDoctor);
        ctx.status(201);
    }

    @Override
    public void update(Context ctx) throws ValidationException {
        int id = validateAndGetId(ctx);
        DoctorDTO doctorDTO = validateEntity(ctx);
        validateBusinessRules(doctorDTO);

        DoctorDTO updatedDoctor = dao.update(id, doctorDTO);
        ctx.json(updatedDoctor);
        ctx.status(200);
    }

    @Override
    public void delete(Context ctx) throws ValidationException {
        int id = validateAndGetId(ctx);
        dao.delete(id);
        ctx.status(204);
    }

    public void readBySpeciality(Context ctx) throws ValidationException {
        Speciality speciality = validateAndGetSpeciality(ctx.pathParam("speciality"));
        List<DoctorDTO> doctors = dao.doctorBySpeciality(speciality);
        ctx.json(doctors);
        ctx.status(200);
    }

    public void readByBirthdateRange(Context ctx) throws ValidationException {
        LocalDate[] dates = validateAndGetDateRange(ctx);
        List<DoctorDTO> doctors = dao.doctorByBirthdateRange(dates[0], dates[1]);
        ctx.json(doctors);
        ctx.status(200);
    }

    private int validateAndGetId(Context ctx) throws ValidationException {
        String idStr = ctx.pathParam("id");
        try {
            int id = Integer.parseInt(idStr);
            if (id <= 0) {
                throw new ValidationException("ID must be a positive number");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid ID format: " + idStr);
        }
    }

    private Speciality validateAndGetSpeciality(String specialityStr) throws ValidationException {
        try {
            return Speciality.valueOf(specialityStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            String validSpecialities = Arrays.toString(Speciality.values());
            throw new ValidationException("Invalid speciality. Valid values are: " + validSpecialities);
        }
    }

    private LocalDate[] validateAndGetDateRange(Context ctx) throws ValidationException {
        String fromStr = ctx.queryParam("from");
        String toStr = ctx.queryParam("to");

        if (fromStr == null || toStr == null) {
            throw new ValidationException("Both 'from' and 'to' dates are required");
        }

        try {
            LocalDate fromDate = LocalDate.parse(fromStr);
            LocalDate toDate = LocalDate.parse(toStr);

            if (fromDate.isAfter(toDate)) {
                throw new ValidationException("From date cannot be after to date");
            }

            return new LocalDate[]{fromDate, toDate};
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format. Use yyyy-MM-dd");
        }
    }

    private void validateBusinessRules(DoctorDTO doctor) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (doctor.getDateOfBirth() != null) {
            LocalDate minBirthDate = LocalDate.now().minusYears(25);
            if (doctor.getDateOfBirth().isAfter(minBirthDate)) {
                errors.add("Doctor must be at least 25 years old");
            }
        }

        if (doctor.getYearOfGraduation() > 0) {
            if (doctor.getDateOfBirth() != null) {
                int minGradYear = doctor.getDateOfBirth().getYear() + 25;
                if (doctor.getYearOfGraduation() < minGradYear) {
                    errors.add("Graduation year must be at least 25 years after birth year");
                }
            }
            if (doctor.getYearOfGraduation() > LocalDate.now().getYear()) {
                errors.add("Graduation year cannot be in the future");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Business validation failed: " + String.join(", ", errors));
        }
    }

    @Override
    public DoctorDTO validateEntity(Context ctx) throws ValidationException {
        DoctorDTO doctor = ctx.bodyAsClass(DoctorDTO.class);
        List<String> errors = new ArrayList<>();

        // Basis validering
        if (doctor.getName() == null || doctor.getName().trim().isEmpty()) {
            errors.add("Doctor name is required");
        }
        if (doctor.getDateOfBirth() == null) {
            errors.add("Date of birth is required");
        }
        if (doctor.getYearOfGraduation() <= 1900) {
            errors.add("Invalid graduation year (must be after 1900)");
        }
        if (doctor.getNameOfClinic() == null || doctor.getNameOfClinic().trim().isEmpty()) {
            errors.add("Clinic name is required");
        }
        if (doctor.getSpeciality() == null) {
            errors.add("Speciality is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join(", ", errors));
        }

        return doctor;
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return dao.validatePrimaryKey(id);
    }
}