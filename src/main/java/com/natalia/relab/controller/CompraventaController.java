package com.natalia.relab.controller;

import com.natalia.relab.dto.CompraventaInDto;
import com.natalia.relab.dto.CompraventaOutDto;
import com.natalia.relab.dto.CompraventaUpdateDto;
import com.natalia.relab.service.CompraventaService;
import exception.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class CompraventaController {

    private static final Logger log = LoggerFactory.getLogger(CompraventaController.class);

    @Autowired
    private CompraventaService compraventaService;

    @GetMapping("/compraventas")
    public ResponseEntity<?> verTodas(
            @RequestParam(value="compradorId", required = false) Long compradorId,
            @RequestParam(value="vendedorId", required = false) Long vendedorId,
            @RequestParam(value="productoId", required = false) Long productoId)
        throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        log.info("GET /compraventas - Filtros recibidos -> compradorId={}, vendedorId={}, productoId={}",
                compradorId, vendedorId, productoId);

        List<CompraventaOutDto> compraventas = compraventaService.listarConFiltros(compradorId, vendedorId, productoId);

        log.info("Resultado: {} compraventas encontradas", compraventas.size());
        return ResponseEntity.ok(compraventas);
    }

    @GetMapping("/compraventas/{id}")
    public ResponseEntity<CompraventaOutDto> listarPorId(@PathVariable long id) throws CompraventaNoEncontradaException {
        log.info("GET /compraventas/{} solicitado", id);
        CompraventaOutDto dto = compraventaService.buscarPorId(id);
        log.info("Compraventa con id {} encontrada", id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/compraventas")
    public ResponseEntity<CompraventaOutDto> agregarCompraventa(@Valid @RequestBody CompraventaInDto compraventaInDto)
        throws UsuarioNoEncontradoException, ProductoNoEncontradoException, ProductoYaVendidoException {

        log.info("POST /compraventas - creando compraventa para productoId {} entre compradorId {} y vendedorId {}",
                compraventaInDto.getProductoId(),
                compraventaInDto.getCompradorId(),
                compraventaInDto.getVendedorId());

        CompraventaOutDto nuevaCompraventa = compraventaService.agregar(compraventaInDto);

        log.info("Compraventa creada con id {}", nuevaCompraventa.getId());
        return new ResponseEntity<>(nuevaCompraventa, HttpStatus.CREATED);
    }

    @PutMapping("/compraventas/{id}")
    public ResponseEntity<CompraventaOutDto> actualizarCompraventa(
            @Valid
            @RequestBody CompraventaUpdateDto compraventaUpdateDto,
            @PathVariable long id) throws
            CompraventaNoEncontradaException {

        log.info("PUT /compraventas/{} actualizando", id);
        CompraventaOutDto actualizada = compraventaService.modificar(id, compraventaUpdateDto);
        log.info("Compraventa con id {} actualizada correctamente", id);
        return ResponseEntity.ok(actualizada);
    }


    @DeleteMapping("/compraventas/{id}")
    public ResponseEntity<Void>  eliminarCompraventa(@PathVariable long id) throws CompraventaNoEncontradaException {
        log.warn("DELETE /compraventas/{} solicitado", id);
        compraventaService.eliminar(id);
        log.info("Compraventa con id {} eliminada correctamente", id);
        return ResponseEntity.noContent().build();
    }

    // --- Me llevo excepciones a GlobalExceptionHandler
}



