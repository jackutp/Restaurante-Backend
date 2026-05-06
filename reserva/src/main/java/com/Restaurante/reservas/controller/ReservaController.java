package com.Restaurante.reservas.controller;

import com.Restaurante.reservas.dto.ReservaRequestDTO;
import com.Restaurante.reservas.dto.ReservaRespuestaDTO;
import com.Restaurante.reservas.entities.Reserva;
import com.Restaurante.reservas.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    @Autowired
    private ReservaService reservaService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveReserva(@RequestBody ReservaRequestDTO reserva){
        reservaService.save(reserva);
    }
    @GetMapping("/search/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return ResponseEntity.ok(reservaService.findReservaById(id));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        return ResponseEntity.ok(reservaService.deleteReservation(id));
    }
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateReserva(@PathVariable Long id, @RequestBody ReservaRequestDTO reserva){
        return ResponseEntity.ok(reservaService.updateReserva(id, reserva));
    }
    @GetMapping("/all")
    public ResponseEntity<?> findAllReservas(){
        return ResponseEntity.ok(reservaService.findAll());
    }
}
