package com.microservicio.eventos;

import com.microservicio.eventos.Services.EventoService;
import com.microservicio.eventos.dto.EventoRequestDTO;
import com.microservicio.eventos.dto.EventoResponseDTO;
import com.microservicio.eventos.dto.EventoStatusUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ActiveProfiles("tests")
@Transactional
class MicroservicioEventosIntegrationTests {

	@Autowired
	private EventoService eventoService;

    @Test
	void contextLoads() {
	}
	private EventoRequestDTO buildEventoRequestDto(){
		EventoRequestDTO dto = new EventoRequestDTO();
		dto.setName("Juan");
		dto.setLastName("Perez");
		dto.setEmail("juan@test.com");
		dto.setPhone("+51999999999");
		dto.setCompany("Acme");
		dto.setDate(LocalDate.now().plusDays(5));
		dto.setAttendees(50);
		dto.setComments("Evento empresarial importante.");
		dto.setAgeConfirmed(true);
		dto.setPrivacyAccepted(true);
		dto.setMarketingAccepted(false);
		return dto;
	}

	@Test
	void shouldCreateEvento(){
		EventoRequestDTO dto = buildEventoRequestDto();
		EventoResponseDTO saved = eventoService.createEvento(dto);
		Assertions.assertThat(saved).isNotNull();
		Assertions.assertThat(saved.getId()).isNotNull();
		Assertions.assertThat(saved.getEmail()).isEqualTo("juan@test.com");
	}
	@Test
	void shouldFindEventoById(){
		EventoRequestDTO dto = buildEventoRequestDto();
		EventoResponseDTO saved = eventoService.createEvento(dto);
		EventoResponseDTO found = eventoService.getEventoById(saved.getId());
		Assertions.assertThat(found).isNotNull();
		Assertions.assertThat(found.getId()).isEqualTo(saved.getId());
	}
	@Test
	void shouldReturnAllEventos(){
		EventoRequestDTO dto = buildEventoRequestDto();
		EventoResponseDTO saved = eventoService.createEvento(dto);
		Pageable pageable = PageRequest.of(0,10,Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<EventoResponseDTO> eventos = eventoService.getAllEventos(pageable);
		Assertions.assertThat(eventos.getContent()).isNotEmpty();
	}

	@Test
	void shouldUpdateEventosStatus(){
		EventoRequestDTO dto = buildEventoRequestDto();
		EventoResponseDTO saved = eventoService.createEvento(dto);
		EventoStatusUpdateDTO updateDTO = new EventoStatusUpdateDTO();
		updateDTO.setStatus("PENDIENTE");
		updateDTO.setReason("Evento Aprobado");

		EventoResponseDTO updated = eventoService.updateEventoStatus(saved.getId(), updateDTO);
		Assertions.assertThat(updated.getStatus()).isEqualTo("PENDIENTE");
	}
}
