package com.natalia.relab.controller;

import com.natalia.relab.dto.CompraventaInDto;
import com.natalia.relab.dto.CompraventaOutDto;
import com.natalia.relab.dto.CompraventaUpdateDto;
import com.natalia.relab.service.CompraventaService;
import com.natalia.relab.service.ProductoService;
import com.natalia.relab.service.UsuarioService;
import exception.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class CompraventaController {

    @Autowired
    private CompraventaService compraventaService;

    @GetMapping("/compraventas")
    public ResponseEntity<?> verTodas(
            @RequestParam(value="compradorId", required = false) Long compradorId,
            @RequestParam(value="vendedorId", required = false) Long vendedorId,
            @RequestParam(value="productoId", required = false) Long productoId)
        throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        List<CompraventaOutDto> compraventas = compraventaService.listarConFiltros(compradorId, vendedorId, productoId);
        return ResponseEntity.ok(compraventas);
    }

    @GetMapping("/compraventas/{id}")
    public ResponseEntity<CompraventaOutDto> listarPorId(@PathVariable long id) throws CompraventaNoEncontradaException {
        CompraventaOutDto dto = compraventaService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/compraventas")
    public ResponseEntity<CompraventaOutDto> agregarCompraventa(@Valid @RequestBody CompraventaInDto compraventaInDto)
        throws UsuarioNoEncontradoException, ProductoNoEncontradoException, ProductoYaVendidoException {

        CompraventaOutDto nuevaCompraventa = compraventaService.agregar(compraventaInDto);
        return new ResponseEntity<>(nuevaCompraventa, HttpStatus.CREATED);
    }

    @PutMapping("/compraventas/{id}")
    public ResponseEntity<CompraventaOutDto> actualizarCompraventa(
            @Valid
            @RequestBody CompraventaUpdateDto compraventaUpdateDto,
            @PathVariable long id) throws
            CompraventaNoEncontradaException {

                CompraventaOutDto actualizada = compraventaService.modificar(id, compraventaUpdateDto);
                return ResponseEntity.ok(actualizada);
            }


    @DeleteMapping("/compraventas/{id}")
    public ResponseEntity<Void>  eliminarCompraventa(@PathVariable long id) throws CompraventaNoEncontradaException {
        compraventaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Me llevo excepciones a GlobalExceptionHandler
}



