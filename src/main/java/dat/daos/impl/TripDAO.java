package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.daos.IDAO;
import dat.daos.ITripGuideDAO;
import dat.dtos.TripDTO;
import dat.entities.Trip;
import dat.entities.Guide;
import dat.enums.TripCategory;
import dat.exceptions.ApiException;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TripDAO implements IDAO<TripDTO, Integer>, ITripGuideDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(TripDAO.class);
    private static TripDAO instance;
    private static EntityManagerFactory emf;

    public static TripDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new TripDAO();
        }
        return instance;
    }

    @Override
    public TripDTO getById(Integer id) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                throw new ApiException(404, "Trip not found with id: " + id);
            }
            LOGGER.info("Retrieved trip with id: {}", id);
            return new TripDTO(trip);
        } catch (PersistenceException e) {
            LOGGER.error("Database error during read operation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during read operation");
        }
    }

    @Override
    public List<TripDTO> getAll() throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t", Trip.class);
            List<TripDTO> trips = query.getResultList().stream()
                .map(TripDTO::new)
                .collect(Collectors.toList());

            if (trips.isEmpty()) {
                throw new ApiException(404, "No trips found in the database");
            }

            LOGGER.info("Retrieved {} trips", trips.size());
            return trips;
        } catch (PersistenceException e) {
            LOGGER.error("Database error during readAll operation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during readAll operation");
        }
    }

    @Override
    public TripDTO create(TripDTO tripDTO) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = new Trip();
            updateTripFields(trip, tripDTO);
            em.persist(trip);
            em.getTransaction().commit();

            LOGGER.info("Created trip with id: {}", trip.getId());
            return new TripDTO(trip);
        } catch (PersistenceException e) {
            LOGGER.error("Database error during create operation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during create operation");
        }
    }

    @Override
    public TripDTO update(Integer id, TripDTO tripDTO) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                throw new ApiException(404, "Trip not found with id: " + id);
            }

            updateTripFields(trip, tripDTO);
            Trip updatedTrip = em.merge(trip);
            em.getTransaction().commit();

            LOGGER.info("Updated trip with id: {}", id);
            return new TripDTO(updatedTrip);
        } catch (PersistenceException e) {
            LOGGER.error("Database error during update operation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during update operation");
        }
    }

    public void delete(Integer id) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            // Find turen først, udenfor transaktionen
            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                throw new ApiException(404, "Trip not found with id: " + id);
            }

            em.getTransaction().begin();
            try {
                if (trip.getGuide() != null) {
                    Guide guide = trip.getGuide();
                    guide.getTrips().remove(trip);
                    trip.setGuide(null);
                    em.merge(guide);
                }

                em.remove(trip);
                em.getTransaction().commit();
                LOGGER.info("Deleted trip with id: {}", id);
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Error deleting trip: " + e.getMessage());
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Database error during delete operation: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred during delete operation");
        }
    }


    @Override
    public void addGuideToTrip(int tripId, int guideId) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Trip trip = em.find(Trip.class, tripId);
            if (trip == null) {
                throw new ApiException(404, "Trip not found with id: " + tripId);
            }

            Guide guide = em.find(Guide.class, guideId);
            if (guide == null) {
                throw new ApiException(404, "Guide not found with id: " + guideId);
            }

            trip.setGuide(guide);
            guide.addTrip(trip);

            em.merge(trip);
            em.merge(guide);
            em.getTransaction().commit();

            LOGGER.info("Added guide {} to trip {}", guideId, tripId);
        } catch (PersistenceException e) {
            LOGGER.error("Database error while adding guide to trip: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred while adding guide to trip");
        }
    }

    @Override
    public Set<TripDTO> getTripsByGuide(int guideId) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, guideId);
            if (guide == null) {
                throw new ApiException(404, "Guide not found with id: " + guideId);
            }

            return guide.getTrips().stream()
                .map(TripDTO::new)
                .collect(Collectors.toSet());
        } catch (PersistenceException e) {
            LOGGER.error("Database error while fetching trips by guide: {}", e.getMessage());
            throw new ApiException(500, "Database error occurred while fetching trips by guide");
        }
    }

    public List<TripDTO> getTripsByCategory(TripCategory category) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery(
                "SELECT t FROM Trip t WHERE t.category = :category",
                Trip.class);
            query.setParameter("category", category);

            List<TripDTO> trips = query.getResultList().stream()
                .map(TripDTO::new)
                .collect(Collectors.toList());

            if (trips.isEmpty()) {
                throw new ApiException(404, "No trips found for category: " + category);
            }

            LOGGER.info("Retrieved {} trips for category {}", trips.size(), category);
            return trips;
        } catch (Exception e) {
            LOGGER.error("Error getting trips by category: {}", e.getMessage());
            throw new ApiException(500, "Error fetching trips by category: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getGuidesTotalTripPrices() throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            // Direct JPQL query that returns the exact format we need
            String jpql = """
                SELECT NEW map(
                    g.id as guideId,
                    COALESCE(SUM(t.price), 0.0) as totalPrice
                )
                FROM Guide g 
                LEFT JOIN g.trips t 
                GROUP BY g.id
                """;

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = em.createQuery(jpql).getResultList();

            if (results.isEmpty()) {
                throw new ApiException(404, "No guide price data found");
            }

            LOGGER.info("Retrieved price totals for {} guides", results.size());
            return results;
        } catch (Exception e) {
            LOGGER.error("Error calculating guide totals: {}", e.getMessage());
            throw new ApiException(500, "Error calculating guide total prices: " + e.getMessage());
        }
    }

    private void updateTripFields(Trip trip, TripDTO tripDTO) {
        trip.setName(tripDTO.getName());
        trip.setStartTime(tripDTO.getStartTime());
        trip.setEndTime(tripDTO.getEndTime());
        trip.setLongitude(tripDTO.getLongitude());
        trip.setLatitude(tripDTO.getLatitude());
        trip.setPrice(tripDTO.getPrice());
        trip.setCategory(tripDTO.getCategory());
    }
}
