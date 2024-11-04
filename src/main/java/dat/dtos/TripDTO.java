package dat.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripDTO {
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    private Double longitude;
    private Double latitude;
    private String name;
    private Double price;
    private TripCategory category;
    private GuideDTO guide;
    private List<PackingItemDTO> packingItems;

    public TripDTO(Trip trip) {
        this.id = trip.getId();
        this.startTime = trip.getStartTime();
        this.endTime = trip.getEndTime();
        this.longitude = trip.getLongitude();
        this.latitude = trip.getLatitude();
        this.name = trip.getName();
        this.price = trip.getPrice();
        this.category = trip.getCategory();
        if (trip.getGuide() != null) {
            this.guide = new GuideDTO(trip.getGuide());
        }
    }
}
