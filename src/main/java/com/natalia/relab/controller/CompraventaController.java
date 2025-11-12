package com.natalia.relab.controller;

import com.natalia.relab.dto.CompraventaInDto;
import com.natalia.relab.dto.CompraventaOutDto;
import com.natalia.relab.dto.CompraventaUpdateDto;
import com.natalia.relab.service.CompraventaService;
import com.natalia.relab.service.ProductoService;
import com.natalia.relab.service.UsuarioService;
import exception.CompraventaNoEncontradaException;
import exception.ErrorResponse;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CompraventaController {

    @Autowired
    private CompraventaService compraventaService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProductoService productoService;

    @GetMapping("/compraventas")
    public ResponseEntity<?> verTodas(
            @RequestParam(value="compradorId", required = false) Long compradorId,
            @RequestParam(value="vendedorId", required = false) Long vendedorId,
            @RequestParam(value="productoId", required = false) Long productoId)
        throws UsuarioNoEncontradoException, ProductoNoEncontradoException, CompraventaNoEncontradaException {

        if (compradorId !=null){
            // Se verifica que el usuario comprador exista
            usuarioService.buscarPorId(compradorId);

            List<CompraventaOutDto> compraventas = compraventaService.buscarPorCompradorId(compradorId);
            return ResponseEntity.ok(compraventas);
        }

        if (vendedorId !=null){
            // Se verifica que el usuario vendedor exista
            usuarioService.buscarPorId(vendedorId);

            List<CompraventaOutDto> compraventas = compraventaService.buscarPorVendedorId(vendedorId);
            return ResponseEntity.ok(compraventas);
        }

        if (productoId !=null){
            // Se verifica que el producto exista
            productoService.buscarPorId(productoId);

            CompraventaOutDto compraventa = compraventaService.buscarPorProductoId(productoId);
            return ResponseEntity.ok(compraventa);
        }

        List<CompraventaOutDto> todasCompraventas = compraventaService.listarTodas();
        return ResponseEntity.ok(todasCompraventas);
    }

    @GetMapping("/compraventas/{id}")
    public ResponseEntity<CompraventaOutDto> listarPorId(@PathVariable long id) throws CompraventaNoEncontradaException {
        CompraventaOutDto dto = compraventaService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/compraventas")
    public ResponseEntity<CompraventaOutDto> agregarCompraventa(@Valid @RequestBody CompraventaInDto compraventaInDto)
        throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

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



