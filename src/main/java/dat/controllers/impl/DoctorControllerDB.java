package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.DoctorDAO;
import dat.dtos.DoctorDTO;
import dat.enums.Speciality;
import dat.exceptions.ApiException;
import dat.exceptions.ValidationException;
import io.javalin.http.Context;

import java.time.LocalDate;

public class DoctorControllerDB implements IController<DoctorDTO, Integer> {
    private final DoctorDAO doctorDAO = DoctorDAO.getInstance(HibernateConfig.getEntityManagerFactory());

    @Override
    public void read(Context ctx) throws ValidationException, ApiException {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            DoctorDTO doctor = doctorDAO.getById(id);
            ctx.status(200).json(doctor);
        } catch (NumberFormatException e) {
            throw new ValidationException(400, "Invalid ID format: must be a number");
        }
    }

    @Override
    public void readAll(Context ctx) throws ApiException {
        ctx.json(doctorDAO.getAll());
        ctx.status(200);
    }

    @Override
    public void create(Context ctx) throws ApiException, ValidationException {
        try {
            DoctorDTO doctorDTO = validateEntity(ctx);
            DoctorDTO created = doctorDAO.create(doctorDTO);
            ctx.status(201).json(created);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(400, "Invalid doctor data: " + e.getMessage());
        }
    }

    public void readBySpeciality(Context ctx) throws ApiException, ValidationException {
        try {
            String specialityStr = ctx.pathParam("speciality").toUpperCase();
            Speciality speciality = Speciality.valueOf(specialityStr);
            ctx.json(doctorDAO.doctorBySpeciality(speciality));
        } catch (IllegalArgumentException e) {
            throw new ValidationException(400, "Invalid speciality: " + ctx.pathParam("speciality"));
        }
    }

    public void readByBirthdateRange(Context ctx) throws ApiException, ValidationException {
        try {
            LocalDate from = LocalDate.parse(ctx.queryParam("from"));
            LocalDate to = LocalDate.parse(ctx.queryParam("to"));

            if (from.isAfter(to)) {
                throw new ValidationException(400, "'from' date must be before 'to' date");
            }

            ctx.json(doctorDAO.doctorByBirthdateRange(from, to));
        } catch (IllegalArgumentException e) {
            throw new ValidationException(400, "Invalid date format. Use format YYYY-MM-DD");
        }
    }

    @Override
    public void update(Context ctx) throws ApiException, ValidationException {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            DoctorDTO doctorDTO = validateEntity(ctx);
            DoctorDTO updated = doctorDAO.update(id, doctorDTO);
            ctx.status(200).json(updated);
        } catch (NumberFormatException e) {
            throw new ValidationException(400, "Invalid ID format: must be a number");
        } catch (IllegalArgumentException e) {
            throw new ValidationException(400, "Invalid doctor data: " + e.getMessage());
        }
    }

    @Override
    public void delete(Context ctx) throws ValidationException, ApiException {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            doctorDAO.delete(id);
            ctx.status(204);
        } catch (NumberFormatException e) {
            throw new ValidationException(400, "Invalid ID format: must be a number");
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) throws ApiException {
        return doctorDAO.validatePrimaryKey(id);
    }

    @Override
    public DoctorDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(DoctorDTO.class)
            .check(d -> d.getName() != null && !d.getName().trim().isEmpty(), "Doctor name is required")
            .check(d -> d.getDateOfBirth() != null, "Date of birth is required")
            .check(d -> d.getYearOfGraduation() > 1900, "Year of graduation must be after 1900")
            .check(d -> d.getNameOfClinic() != null && !d.getNameOfClinic().trim().isEmpty(), "Clinic name is required")
            .check(d -> d.getSpeciality() != null, "Speciality is required")
            .get();
    }
}
