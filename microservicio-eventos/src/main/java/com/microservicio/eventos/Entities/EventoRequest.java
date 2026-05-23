package com.microservicio.eventos.Entities;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "evento_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Datos personales
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 100)
    private String lastName;
    @Column(nullable = false, length = 255)
    private String email;
    @Column(nullable = false, length = 20)
    private String phone;
    // Datos evento
    @Column(length = 255)
    private String company;
    @Column(name = "event_date", nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private Integer attendees;
    @Column(nullable = false, length = 1000)
    private String comments;

    // Metadata del sistema
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventoStatus status = EventoStatus.PENDIENTE;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventoSource source = EventoSource.WEB_FORM;

    // Consentimientos legales
    @Column(name = "age_confirmed", nullable = false)
    private Boolean ageConfirmed = false;
    @Column(name = "privacy_accepted", nullable = false)
    private Boolean privacyAccepted = false;
    @Column(name = "marketing_accepted")
    private Boolean marketingAccepted = false;
}