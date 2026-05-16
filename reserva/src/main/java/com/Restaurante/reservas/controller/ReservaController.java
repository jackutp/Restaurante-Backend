package com.Restaurante.reservas.controller;

import com.Restaurante.reservas.dto.ReservaRequestDTO;
import com.Restaurante.reservas.dto.ReservaRespuestaDTO;
import com.Restaurante.reservas.entities.Reserva;
import com.Restaurante.reservas.service.ReservaReadService;
import com.Restaurante.reservas.service.ReservaService;
import com.Restaurante.reservas.service.ReservaWriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    @Autowired
    ReservaReadService reservaReadService;
    @Autowired
    ReservaWriteService reservaWriteService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveReserva(@RequestBody ReservaRequestDTO reserva){
        reservaWriteService.save(reserva);
    }
    @GetMapping("/search/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return ResponseEntity.ok(reservaReadService.findReservaById(id));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        return ResponseEntity.ok(reservaWriteService.deleteReservation(id));
    }
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateReserva(@PathVariable Long id, @RequestBody ReservaRequestDTO reserva){
        return ResponseEntity.ok(reservaWriteService.updateReserva(id, reserva));
    }
    @GetMapping("/all")
    public ResponseEntity<?> findAllReservas(){
        return ResponseEntity.ok(reservaReadService.findAll());
    }
}
