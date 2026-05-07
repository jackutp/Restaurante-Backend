package com.Restaurante.reservas;

import com.Restaurante.reservas.dto.ReservaRequestDTO;
import com.Restaurante.reservas.dto.ReservaRespuestaDTO;
import com.Restaurante.reservas.entities.Reserva;
import com.Restaurante.reservas.service.ReservaService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@SpringBootTest
class ReservasApplicationTests {
	@Autowired
	private ReservaService reservaService;

	@Test
	void contextLoads() {
	}

	@Test
	public void Repository_Save_ReturnSavePromotion(){
		ReservaRequestDTO reservaRequest = new ReservaRequestDTO(3L,2, 4, Date.valueOf("2026-05-24"), Time.valueOf("15:30:00"), Reserva.menuTipo.CARTA, null); ReservaRespuestaDTO savedReserva = reservaService.save(reservaRequest);
		Assertions.assertThat(savedReserva).isNotNull();
		Assertions.assertThat(savedReserva.cantidadClientes()).isNotNull();
		Assertions.assertThat(savedReserva.fecha()).isNotNull();
		Assertions.assertThat(savedReserva.hora()).isNotNull();
		Assertions.assertThat(savedReserva.menu()).isNotNull();
		System.out.println(savedReserva);
	}
	@Test
	public void shouldFindReservaById(){
		ReservaRequestDTO reservaRequest = new ReservaRequestDTO(3L,2, 4, Date.valueOf("2026-05-24"), Time.valueOf("15:30:00"), Reserva.menuTipo.CARTA, null);
		ReservaRespuestaDTO r = reservaService.save(reservaRequest);
		ReservaRespuestaDTO found = reservaService.findReservaById(r.reservaId());
		Assertions.assertThat(found).isNotNull();
	}
	@Test
	public void shouldReturnAllReservas(){
		List<ReservaRespuestaDTO> list = reservaService.findAll();
		Assertions.assertThat(list).isNotEmpty();
	}
	@Test
	public void shouldDeleteReserva(){
		ReservaRequestDTO reservaRequest = new ReservaRequestDTO(3L,2, 4, Date.valueOf("2026-05-24"), Time.valueOf("15:30:00"), Reserva.menuTipo.CARTA, null);
		ReservaRespuestaDTO r = reservaService.save(reservaRequest);
		boolean deleted = reservaService.deleteReservation(r.reservaId());
		Assertions.assertThat(deleted).isTrue();
	}
	@Test
	public void shouldThrowExceptionWhenMesaNotFound(){
		ReservaRequestDTO reservaRequest = new ReservaRequestDTO(999L,2, 4, Date.valueOf("2026-05-24"), Time.valueOf("15:30:00"), Reserva.menuTipo.DEGUSTACION, null);
		Assertions.assertThatThrownBy(() -> reservaService.save(reservaRequest)).isInstanceOf(RuntimeException.class);
	}
	@Test
	public void shouldUpdateReserva(){
		ReservaRequestDTO reservaRequest = new ReservaRequestDTO(3L,2, 4, Date.valueOf("2026-05-24"), Time.valueOf("15:30:00"), Reserva.menuTipo.CARTA, null);
		ReservaRespuestaDTO r = reservaService.save(reservaRequest);
		Assertions.assertThat(r.cantidadClientes()).isEqualTo(4);
		Assertions.assertThat(r.hora()).isEqualTo(Time.valueOf("15:30:00"));
		ReservaRequestDTO reservaUpdate = new ReservaRequestDTO(3L,2, 6, Date.valueOf("2026-05-24"), Time.valueOf("20:00:00"), Reserva.menuTipo.CARTA, null);
		r = reservaService.updateReserva(r.reservaId(), reservaUpdate);
		Assertions.assertThat(r.cantidadClientes()).isEqualTo(6);
		Assertions.assertThat(r.hora()).isEqualTo(Time.valueOf("20:00:00"));
	}
}
