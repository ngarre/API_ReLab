package com.natalia.relab.controller;

import com.natalia.relab.dto.ServiciosInDto;
import com.natalia.relab.dto.ServiciosOutDto;
import com.natalia.relab.service.ServiciosService;
import exception.ServicioNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ServiciosController {

    private static final Logger log = LoggerFactory.getLogger(ServiciosController.class);

    @Autowired
    private ServiciosService serviciosService;

    @GetMapping("/servicios")
    public List<ServiciosOutDto> listarTodos(){
        log.info("GET /servicios - solicitando todos los servicios");
        return serviciosService.listar();
    }

    @GetMapping("/servicios/{idUsuario}")
    public List<ServiciosOutDto> listarPorIdUsuario(@PathVariable Long idUsuario){
        log.info("GET /servicios/{} - solicitando servicios por ID de usuario", idUsuario);
        return serviciosService.buscarPorIdUsuario(idUsuario);
    }

    @PostMapping("/servicios")
    public ResponseEntity<ServiciosOutDto> agregarServicio(@RequestBody ServiciosInDto serviciosInDto){
        log.info("Agregando servicio {}", serviciosInDto);
        ServiciosOutDto nuevoServicio = serviciosService.agregar(serviciosInDto);
        log.info("POST /servicios - creando nuevo servicio con ID {}", nuevoServicio.getId());
        return new ResponseEntity<>(nuevoServicio, HttpStatus.CREATED);
    }

    // DELETE por ID de usuario
    @DeleteMapping("/servicios/{idUsuario}")
    public ResponseEntity<Void> eliminarPorIdUsuario(@PathVariable Long idUsuario) throws ServicioNoEncontradoException {
        log.warn("DELETE /servicios/{} - eliminación solicitada para el servicio", idUsuario);
        serviciosService.eliminarPorIdUsuario(idUsuario);
        log.info("DELETE /servicios/{} - servicios eliminados correctamente", idUsuario);
        return ResponseEntity.noContent().build();
    }

    // DELETE por ID de servicio
    // Es el que llamaremos desde la app de android
    @DeleteMapping("/servicios/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable Long id) throws ServicioNoEncontradoException {
        log.warn("DELETE /servicios/{} - eliminación solicitada para servicio", id);
        serviciosService.eliminar(id);
        log.info("DELETE /servicios/{} - servicio eliminado correctamente", id);
        return ResponseEntity.noContent().build();
    }

}
