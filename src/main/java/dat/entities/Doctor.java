package dat.entities;

import dat.dtos.DoctorDTO;
import dat.enums.Speciality;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "doctors")
@Getter
@NoArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Setter
    @Column(nullable = false)
    private int yearOfGraduation;

    @Setter
    @Column(nullable = false)
    private String nameOfClinic;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Speciality speciality;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Appointment> appointments = new ArrayList<>();

    // Constructor fra DTO
    public Doctor(DoctorDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.dateOfBirth = dto.getDateOfBirth();
        this.yearOfGraduation = dto.getYearOfGraduation();
        this.nameOfClinic = dto.getNameOfClinic();
        this.speciality = dto.getSpeciality();
    }

    // Bi-directional relationship management
    public void addAppointment(Appointment appointment) {
        if (appointment != null) {
            this.appointments.add(appointment);
            appointment.setDoctor(this);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor doctor)) return false;
        return yearOfGraduation == doctor.yearOfGraduation && Objects.equals(id, doctor.id) && Objects.equals(name, doctor.name) && Objects.equals(dateOfBirth, doctor.dateOfBirth) && Objects.equals(nameOfClinic, doctor.nameOfClinic) && speciality == doctor.speciality && Objects.equals(createdAt, doctor.createdAt) && Objects.equals(updatedAt, doctor.updatedAt) && Objects.equals(appointments, doctor.appointments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dateOfBirth, yearOfGraduation, nameOfClinic, speciality, createdAt, updatedAt, appointments);
    }
}