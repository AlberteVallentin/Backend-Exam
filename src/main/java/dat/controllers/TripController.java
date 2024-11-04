package dat.controllers;

import dat.config.HibernateConfig;
import dat.config.Populator;
import dat.daos.impl.TripDAO;
import dat.dtos.PackingItemDTO;
import dat.dtos.TripDTO;
import dat.enums.TripCategory;
import dat.exceptions.ApiException;
import dat.exceptions.ValidationException;
import dat.services.PackingService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TripController {
    private final TripDAO tripDAO = TripDAO.getInstance(HibernateConfig.getEntityManagerFactory());
    private final PackingService packingService = PackingService.getInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger(TripController.class);

    public void getAll(Context ctx) throws ApiException {
        List<TripDTO> trips = tripDAO.getAll();
        ctx.json(trips);
        ctx.status(200);
    }

    public void getById(Context ctx) throws ApiException, ValidationException {
        int id = validateId(ctx);
        TripDTO trip = tripDAO.getById(id);

        // Hent pakkeliste for turens kategori
        try {
            List<PackingItemDTO> packingItems = packingService.getPackingItems(
                trip.getCategory().toString());
            trip.setPackingItems(packingItems);
        } catch (ApiException e) {
            LOGGER.error("Error fetching packing items: {}", e.getMessage());
            // Vi fortsætter uden pakkeliste hvis der er fejl
        }
        ctx.json(trip);
        ctx.status(200);
    }

    public void getTripPackingWeight(Context ctx) throws ApiException, ValidationException {
        int id = validateId(ctx);
        TripDTO trip = tripDAO.getById(id);

        List<PackingItemDTO> packingItems = packingService.getPackingItems(
            trip.getCategory().toString());
        double totalWeight = packingService.getTotalPackingWeight(packingItems);

        ctx.json(Map.of(
            "tripId", id,
            "totalWeightInGrams", totalWeight
        ));
        ctx.status(200);
    }

    public void create(Context ctx) throws ApiException, ValidationException {
        TripDTO tripDTO = validateEntity(ctx);
        TripDTO created = tripDAO.create(tripDTO);
        ctx.json(created);
        ctx.status(201);
    }

    public void update(Context ctx) throws ApiException, ValidationException {
        int id = validateId(ctx);
        TripDTO tripDTO = validateEntity(ctx);
        TripDTO updated = tripDAO.update(id, tripDTO);
        ctx.json(updated);
        ctx.status(200);
    }

    public void delete(Context ctx) throws ApiException, ValidationException {
        int id = validateId(ctx);
        tripDAO.delete(id);
        ctx.status(204);
    }

    public void addGuideToTrip(Context ctx) throws ApiException, ValidationException {
        int tripId = Integer.parseInt(ctx.pathParam("tripId"));
        int guideId = Integer.parseInt(ctx.pathParam("guideId"));
        tripDAO.addGuideToTrip(tripId, guideId);
        ctx.status(200);
    }

    private int validateId(Context ctx) throws ValidationException {
        try {
            return Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            throw new ValidationException(400, "Invalid ID format");
        }
    }

    public void getTripsByCategory(Context ctx) throws ApiException, ValidationException {
        try {
            String categoryStr = ctx.pathParam("category").toUpperCase();
            TripCategory category = TripCategory.valueOf(categoryStr);

            List<TripDTO> trips = tripDAO.getTripsByCategory(category);
            ctx.json(trips);
            ctx.status(200);

        } catch (IllegalArgumentException e) {
            String validCategories = Arrays.stream(TripCategory.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));

            throw new ValidationException(400,
                "Invalid category. Valid categories are: " + validCategories);
        }
    }

    public void getGuidesTotalPrices(Context ctx) throws ApiException {
        try {
            List<Map<String, Object>> guideTotals = tripDAO.getGuidesTotalTripPrices();
            ctx.json(guideTotals);
            ctx.status(200);
        } catch (Exception e) {
            throw new ApiException(500, "Error retrieving guide total prices");
        }
    }







    private TripDTO validateEntity(Context ctx) throws ValidationException {
        TripDTO trip = ctx.bodyAsClass(TripDTO.class);
        List<String> errors = new ArrayList<>();

        if (trip.getName() == null || trip.getName().trim().isEmpty()) {
            errors.add("Trip name is required");
        }
        if (trip.getStartTime() == null) {
            errors.add("Start time is required");
        }
        if (trip.getEndTime() == null) {
            errors.add("End time is required");
        }
        if (trip.getLongitude() == null) {
            errors.add("Longitude is required");
        }
        if (trip.getLatitude() == null) {
            errors.add("Latitude is required");
        }
        if (trip.getPrice() == null || trip.getPrice() <= 0) {
            errors.add("Valid price is required (must be greater than 0)");
        }
        if (trip.getCategory() == null) {
            errors.add("Category is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(400, String.join(", ", errors));
        }

        return trip;
    }
}


