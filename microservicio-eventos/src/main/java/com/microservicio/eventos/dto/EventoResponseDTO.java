package com.microservicio.eventos.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EventoResponseDTO {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String company;
    private LocalDate date;
    private Integer attendees;
    private String comments;
    private String status;
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}