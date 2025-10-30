package com.natalia.relab.controller;

import com.natalia.relab.dto.CompraventaInDto;
import com.natalia.relab.dto.CompraventaOutDto;
import com.natalia.relab.dto.CompraventaUpdateDto;
import com.natalia.relab.service.CompraventaService;
import exception.CompraventaNoEncontradaException;
import exception.ErrorResponse;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
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
    public ResponseEntity<List<CompraventaOutDto>> verTodas() {
        List<CompraventaOutDto> todasCompraventas = compraventaService.listarTodas();
        return ResponseEntity.ok(todasCompraventas);
    }

    @GetMapping("/compraventas/{id}")
    public ResponseEntity<CompraventaOutDto> listarPorId(@PathVariable long id) throws CompraventaNoEncontradaException {
        CompraventaOutDto dto = compraventaService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/compraventas")
    public ResponseEntity<CompraventaOutDto> agregarCompraventa(@RequestBody CompraventaInDto compraventaInDto)
        throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        CompraventaOutDto nuevaCompraventa = compraventaService.agregar(compraventaInDto);
        return new ResponseEntity<>(nuevaCompraventa, HttpStatus.CREATED);
    }

    @PutMapping("/compraventas/{id}")
    public ResponseEntity<CompraventaOutDto> actualizarCompraventa(
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

    @ExceptionHandler(CompraventaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleExcpetion(CompraventaNoEncontradaException ex) {
        ErrorResponse errorResponse = new ErrorResponse(404, "no-encontrado", "El registro de compraventa no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}



