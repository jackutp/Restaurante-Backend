package com.microservicio.eventos.Mapper;

import com.microservicio.eventos.Entities.EventoRequest;
import com.microservicio.eventos.dto.EventoRequestDTO;
import com.microservicio.eventos.dto.EventoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class EventoMapper {

    public EventoRequest toEntity(EventoRequestDTO dto) {
        EventoRequest entity = new EventoRequest();
        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setCompany(dto.getCompany());
        entity.setDate(dto.getDate());
        entity.setAttendees(dto.getAttendees());
        entity.setComments(dto.getComments());
        entity.setAgeConfirmed(dto.getAgeConfirmed());
        entity.setPrivacyAccepted(dto.getPrivacyAccepted());
        entity.setMarketingAccepted(dto.getMarketingAccepted());
        return entity;
    }

    public EventoResponseDTO toResponseDTO(EventoRequest entity) {
        EventoResponseDTO dto = new EventoResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setCompany(entity.getCompany());
        dto.setDate(entity.getDate());
        dto.setAttendees(entity.getAttendees());
        dto.setComments(entity.getComments());
        dto.setStatus(entity.getStatus().name());
        dto.setSource(entity.getSource().name());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}