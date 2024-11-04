package dat.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import dat.dtos.PackingItemDTO;
import dat.dtos.PackingListDTO;
import dat.exceptions.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PackingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackingService.class);
    private static final String API_BASE_URL = "https://packingapi.cphbusinessapps.dk/packinglist/";
    private static PackingService instance;
    private final HttpClient client;
    private final ObjectMapper objectMapper;

    private PackingService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static PackingService getInstance() {
        if (instance == null) {
            instance = new PackingService();
        }
        return instance;
    }

    public List<PackingItemDTO> getPackingItems(String category) throws ApiException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + category.toLowerCase()))
                .GET()
                .build();

            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LOGGER.error("Error fetching packing items. Status: {}", response.statusCode());
                throw new ApiException(response.statusCode(),
                    "Failed to fetch packing items from external API");
            }

            PackingListDTO packingList = objectMapper.readValue(response.body(),
                PackingListDTO.class);

            return packingList.getItems();
        } catch (Exception e) {
            LOGGER.error("Error fetching packing items: {}", e.getMessage());
            throw new ApiException(500, "Error fetching packing items: " + e.getMessage());
        }
    }

    public double getTotalPackingWeight(List<PackingItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
            .mapToDouble(item -> item.getWeightInGrams() * item.getQuantity())
            .sum();
    }
}