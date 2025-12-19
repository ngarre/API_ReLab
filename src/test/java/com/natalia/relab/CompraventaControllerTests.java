package com.natalia.relab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natalia.relab.controller.CompraventaController;
import com.natalia.relab.dto.CompraventaInDto;
import com.natalia.relab.dto.CompraventaOutDto;
import com.natalia.relab.dto.CompraventaUpdateDto;
import com.natalia.relab.service.CompraventaService;
import exception.CompraventaNoEncontradaException;
import exception.ProductoNoEncontradoException;
import exception.ProductoYaVendidoException;
import exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompraventaController.class)
public class CompraventaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompraventaService compraventaService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------------------------------------------------
    //                  TEST POST - 201
    // ---------------------------------------------------------
    @Test
    void testAgregarCompraventa_201() throws Exception {

        CompraventaInDto inDto = new CompraventaInDto();
        inDto.setProductoId(1L);
        inDto.setCompradorId(2L);
        inDto.setVendedorId(3L);
        inDto.setPrecioFinal(300.0f);

        CompraventaOutDto outDto = new CompraventaOutDto();
        outDto.setId(1L);
        outDto.setPrecioFinal(300.0f);

        when(compraventaService.agregar(any(CompraventaInDto.class)))
                .thenReturn(outDto);

        mockMvc.perform(post("/compraventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.precioFinal").value(300.0));
    }

    // ----------------------------------------------------------------
    //      TEST POST /compraventas - 400 (Bad Request por Validación)
    // ----------------------------------------------------------------
    @Test
    void testAgregarCompraventa_400_PorValidacion() throws Exception {

        CompraventaInDto inDto = new CompraventaInDto();
        inDto.setProductoId(null); // obligatorio
        inDto.setCompradorId(1L);
        inDto.setVendedorId(2L);

        mockMvc.perform(post("/compraventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.productoId").exists());
    }

    // ---------------------------------------------------------
    //           TEST POST - 404 Not Found del Producto
    // ---------------------------------------------------------
    @Test
    void testAgregarCompraventa_404() throws Exception {

        CompraventaInDto inDto = new CompraventaInDto();
        inDto.setProductoId(99L);
        inDto.setCompradorId(1L);
        inDto.setVendedorId(2L);
        inDto.setPrecioFinal(100.0f);

        when(compraventaService.agregar(any(CompraventaInDto.class)))
                .thenThrow(new ProductoNoEncontradoException());

        mockMvc.perform(post("/compraventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    //           TEST POST - 409 CONFLICT - Producto ya vendido
    // ---------------------------------------------------------
    @Test
    void testAgregarCompraventa_409_ProductoYaVendido() throws Exception {

        // DTO de entrada válido
        CompraventaInDto inDto = new CompraventaInDto();
        inDto.setProductoId(1L);
        inDto.setCompradorId(2L);
        inDto.setVendedorId(3L);
        inDto.setPrecioFinal(250.0f);

        // El service lanza la excepción de conflicto
        when(compraventaService.agregar(any(CompraventaInDto.class)))
                .thenThrow(new ProductoYaVendidoException());

        mockMvc.perform(post("/compraventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                // Código HTTP correcto
                .andExpect(status().isConflict())
                // Estructura del error
                .andExpect(jsonPath("$.code").value("409"))
                .andExpect(jsonPath("$.message").value("El producto ya ha sido vendido"));
    }


    // ---------------------------------------------------------
    //           TEST GET Todas - 200 (SIN FILTROS)
    // ---------------------------------------------------------
    @Test
    void testVerTodasCompraventas_200() throws Exception {

        CompraventaOutDto dto = new CompraventaOutDto();
        dto.setId(1L);
        dto.setPrecioFinal(150.0f);

        when(compraventaService.listarConFiltros(null, null, null))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/compraventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].precioFinal").value(150.0));
    }


    // ---------------------------------------------------------
    //  TEST GET Todas - 404 Filtro por comprador que no existe
    // ---------------------------------------------------------
    @Test
    void testListarCompraventas_404_CompradorNoExiste() throws Exception {

        long compradorId = 99L;

        when(compraventaService.listarConFiltros(compradorId, null, null))
                .thenThrow(new UsuarioNoEncontradoException());

        mockMvc.perform(get("/compraventas")
                        .param("compradorId", String.valueOf(compradorId)))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    //           TEST GET por ID - 200
    // ---------------------------------------------------------
    @Test
    void testListarCompraventaPorId_200() throws Exception {

        long id = 1L;

        CompraventaOutDto dto = new CompraventaOutDto();
        dto.setId(id);
        dto.setPrecioFinal(200.0f);

        when(compraventaService.buscarPorId(id)).thenReturn(dto);

        mockMvc.perform(get("/compraventas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.precioFinal").value(200.0));
    }

    // ---------------------------------------------------------
    //           TEST GET por ID - 404
    // ---------------------------------------------------------
    @Test
    void testListarCompraventaPorId_404() throws Exception {

        when(compraventaService.buscarPorId(99L))
                .thenThrow(new CompraventaNoEncontradaException());

        mockMvc.perform(get("/compraventas/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    //           TEST PUT - 200
    // ---------------------------------------------------------
    @Test
    void testActualizarCompraventa_200() throws Exception {

        long id = 1L;

        CompraventaUpdateDto updateDto = new CompraventaUpdateDto();
        updateDto.setComentario("Actualizada");

        CompraventaOutDto outDto = new CompraventaOutDto();
        outDto.setId(id);
        outDto.setComentario("Actualizada");

        when(compraventaService.modificar(eq(id), any(CompraventaUpdateDto.class)))
                .thenReturn(outDto);

        mockMvc.perform(put("/compraventas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comentario").value("Actualizada"));
    }

    // ---------------------------------------------------------
    //        TEST PUT - 400 Bad Request por precio negativo
    // ---------------------------------------------------------
    @Test
    void testActualizarCompraventa_400() throws Exception {

        CompraventaUpdateDto updateDto = new CompraventaUpdateDto();
        updateDto.setPrecioFinal(-5F); // inválido

        mockMvc.perform(put("/compraventas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------
    //           TEST DELETE  - 204
    // ---------------------------------------------------------
    @Test
    void testEliminarCompraventa_204() throws Exception {

        doNothing().when(compraventaService).eliminar(1L);

        mockMvc.perform(delete("/compraventas/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    // ---------------------------------------------------------
    //           TEST DELETE  - 404
    // ---------------------------------------------------------
    @Test
    void testEliminarCompraventa_404() throws Exception {

        doThrow(new CompraventaNoEncontradaException())
                .when(compraventaService).eliminar(99L);

        mockMvc.perform(delete("/compraventas/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}
