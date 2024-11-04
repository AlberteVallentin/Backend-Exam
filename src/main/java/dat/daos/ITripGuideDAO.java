package dat.daos;

import dat.dtos.TripDTO;
import dat.exceptions.ApiException;
import java.util.Set;

public interface ITripGuideDAO {
    /**
     * Adds a guide to a trip
     * @param tripId The ID of the trip
     * @param guideId The ID of the guide
     * @throws ApiException if either trip or guide is not found, or if database error occurs
     */
    void addGuideToTrip(int tripId, int guideId) throws ApiException;

    /**
     * Gets all trips for a specific guide
     * @param guideId The ID of the guide
     * @return Set of trips associated with the guide
     * @throws ApiException if guide is not found or if database error occurs
     */
    Set<TripDTO> getTripsByGuide(int guideId) throws ApiException;
}
