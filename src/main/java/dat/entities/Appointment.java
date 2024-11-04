package dat.entities;

import dat.dtos.AppointmentDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Entity representing a medical appointment
 * Links to Doctor entity in a ManyToOne relationship
 */
@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "doctor")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(length = 1000)
    private String comment;

    // Temporal audit fields
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Many-to-One relationship with Doctor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    /**
     * Constructor for creating an appointment from DTO
     * @param dto The AppointmentDTO containing the data
     */
    public Appointment(AppointmentDTO dto) {
        updateFromDTO(dto);
    }

    /**
     * Update entity data from DTO
     * @param dto The AppointmentDTO containing the new data
     */
    public void updateFromDTO(AppointmentDTO dto) {
        this.clientName = dto.getClientName();
        this.date = dto.getDate();
        this.time = dto.getTime();
        this.comment = dto.getComment();
    }

    /**
     * Sets the creation timestamp before persisting
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the modification timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}