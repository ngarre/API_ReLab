package com.natalia.relab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natalia.relab.controller.AlquilerController;
import com.natalia.relab.dto.*;
import com.natalia.relab.service.AlquilerService;
import exception.AlquilerNoEncontradoException;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlquilerController.class)
public class AlquilerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlquilerService alquilerService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------------------------------------------------
    //           TEST POST - 201 CREATED
    // ---------------------------------------------------------
    @Test
    void testAgregarAlquiler_201() throws Exception {

        // --------- DTO de entrada (request) ---------
        AlquilerInDto inDto = new AlquilerInDto();
        inDto.setFechaInicio(LocalDate.of(2025, 1, 1));
        inDto.setFechaFin(LocalDate.of(2025, 6, 1));
        inDto.setMeses(5);
        inDto.setPrecio(150.0f);
        inDto.setComentario("Alquiler de prueba");
        inDto.setCancelado(false);
        inDto.setProductoId(10L);
        inDto.setArrendadorId(1L);
        inDto.setArrendatarioId(2L);

        // --------- DTO de salida (response) ---------
        AlquilerOutDto outDto = new AlquilerOutDto();
        outDto.setId(100L);
        outDto.setFechaInicio(inDto.getFechaInicio());
        outDto.setFechaFin(inDto.getFechaFin());
        outDto.setMeses(5);
        outDto.setPrecio(150.0f);
        outDto.setComentario("Alquiler de prueba");
        outDto.setCancelado(false);
        outDto.setProducto(new ProductoSimpleDto(10L, "Microscopio"));
        outDto.setArrendador(new UsuarioSimpleDto(1L, "natalia"));
        outDto.setArrendatario(new UsuarioSimpleDto(2L, "juan"));

        // --------- Mock del servicio ---------
        when(alquilerService.agregar(any(AlquilerInDto.class)))
                .thenReturn(outDto);

        // --------- Ejecución + verificaciones ---------
        mockMvc.perform(post("/alquileres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.precio").value(150.0))
                .andExpect(jsonPath("$.producto.id").value(10))
                .andExpect(jsonPath("$.arrendador.nickname").value("natalia"))
                .andExpect(jsonPath("$.arrendatario.nickname").value("juan"));
    }

    // ---------------------------------------------------------
    //           TEST POST - 400 BAD REQUEST (Validación)
    // ---------------------------------------------------------
    @Test
    void testAgregarAlquiler_400_PorValidacion() throws Exception {

        // DTO inválido: faltan campos @NotNull
        AlquilerInDto inDto = new AlquilerInDto();
        inDto.setPrecio(-10); // @Min(0) incumplido
        // productoId, arrendadorId y arrendatarioId → NULL

        mockMvc.perform(post("/alquileres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.productoId").exists())
                .andExpect(jsonPath("$.errors.arrendadorId").exists())
                .andExpect(jsonPath("$.errors.arrendatarioId").exists())
                .andExpect(jsonPath("$.errors.precio").exists());
    }

    // ---------------------------------------------------------
    //           TEST POST - 404 NOT FOUND (Producto)
    // ---------------------------------------------------------
    @Test
    void testAgregarAlquiler_404_ProductoNoEncontrado() throws Exception {

        // --------- DTO válido ---------
        AlquilerInDto inDto = new AlquilerInDto();
        inDto.setFechaInicio(LocalDate.of(2025, 1, 1));
        inDto.setFechaFin(LocalDate.of(2025, 6, 1));
        inDto.setMeses(5);
        inDto.setPrecio(120.0f);
        inDto.setComentario("Alquiler prueba");
        inDto.setCancelado(false);
        inDto.setProductoId(99L);       // Producto inexistente
        inDto.setArrendadorId(1L);
        inDto.setArrendatarioId(2L);

        // --------- Mock del service → lanza excepción ---------
        when(alquilerService.agregar(any(AlquilerInDto.class)))
                .thenThrow(new ProductoNoEncontradoException());

        // --------- Ejecución + verificación ---------
        mockMvc.perform(post("/alquileres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    //           TEST GET por ID - 200
    // ---------------------------------------------------------
    @Test
    void testListarAlquilerPorId_200() throws Exception {

        long alquilerId = 1L;

        // --------- DTO que devuelve el service ---------
        AlquilerOutDto outDto = new AlquilerOutDto();
        outDto.setId(alquilerId);
        outDto.setMeses(6);
        outDto.setPrecio(300.0f);
        outDto.setCancelado(false);

        // Producto y usuarios simplificados
        outDto.setProducto(new ProductoSimpleDto(10L, "Microscopio"));
        outDto.setArrendador(new UsuarioSimpleDto(1L, "juan"));
        outDto.setArrendatario(new UsuarioSimpleDto(2L, "maria"));

        // --------- Mock del service ---------
        when(alquilerService.buscarPorId(alquilerId))
                .thenReturn(outDto);

        // --------- Ejecución + verificaciones ---------
        mockMvc.perform(get("/alquileres/{id}", alquilerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(alquilerId))
                .andExpect(jsonPath("$.precio").value(300.0))
                .andExpect(jsonPath("$.producto.nombre").value("Microscopio"))
                .andExpect(jsonPath("$.arrendador.nickname").value("juan"))
                .andExpect(jsonPath("$.arrendatario.nickname").value("maria"));
    }


    // ---------------------------------------------------------
    //           TEST GET por ID - 404
    // ---------------------------------------------------------
    @Test
    void testListarAlquilerPorId_404() throws Exception {

        long alquilerId = 99L;

        // --------- Mock del service → lanza excepción ---------
        when(alquilerService.buscarPorId(alquilerId))
                .thenThrow(new AlquilerNoEncontradoException());

        // --------- Ejecución + verificación ---------
        mockMvc.perform(get("/alquileres/{id}", alquilerId))
                .andExpect(status().isNotFound());
    }


    // ---------------------------------------------------------
    //           TEST GET Todos - 200 (sin filtros)
    // ---------------------------------------------------------
    @Test
    void testListarAlquileres_200_SinFiltros() throws Exception {

        // --------- DTOs simulados ---------
        AlquilerOutDto a1 = new AlquilerOutDto();
        a1.setId(1L);
        a1.setPrecio(200.0f);

        AlquilerOutDto a2 = new AlquilerOutDto();
        a2.setId(2L);
        a2.setPrecio(350.0f);

        // --------- Mock del service ---------
        when(alquilerService.listarConFiltros(null, null, null))
                .thenReturn(List.of(a1, a2));

        // --------- Ejecución + verificaciones ---------
        mockMvc.perform(get("/alquileres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    // ---------------------------------------------------------
    //         TEST GET todos - 200 ( FILTRO por arrendadorId)
    // ---------------------------------------------------------
    @Test
    void testListarAlquileres_200_PorArrendador() throws Exception {

        long arrendadorId = 5L;

        AlquilerOutDto alquiler = new AlquilerOutDto();
        alquiler.setId(10L);
        alquiler.setPrecio(500.0f);

        when(alquilerService.listarConFiltros(arrendadorId, null, null))
                .thenReturn(List.of(alquiler));

        mockMvc.perform(get("/alquileres")
                        .param("arrendadorId", String.valueOf(arrendadorId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10));
    }

    // ---------------------------------------------------------
    //  TEST GET todos - 404 Filtro por arrendador que no existe
    // ---------------------------------------------------------
    @Test
    void testListarAlquileres_404_ArrendadorNoExiste() throws Exception {

        long arrendadorId = 99L;

        when(alquilerService.listarConFiltros(arrendadorId, null, null))
                .thenThrow(new UsuarioNoEncontradoException());

        mockMvc.perform(get("/alquileres")
                        .param("arrendadorId", String.valueOf(arrendadorId)))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    //           TEST PUT - 200
    // ---------------------------------------------------------
    @Test
    void testActualizarAlquiler_200() throws Exception {

        long alquilerId = 1L;

        // --------- DTO de entrada ---------
        AlquilerUpdateDto updateDto = new AlquilerUpdateDto();
        updateDto.setPrecio(300.0f);
        updateDto.setComentario("Actualizado");
        updateDto.setCancelado(false);

        // --------- DTO de salida ---------
        AlquilerOutDto outDto = new AlquilerOutDto();
        outDto.setId(alquilerId);
        outDto.setPrecio(300.0f);
        outDto.setComentario("Actualizado");
        outDto.setCancelado(false);

        // --------- Mock del service ---------
        when(alquilerService.modificar(eq(alquilerId), any(AlquilerUpdateDto.class)))
                .thenReturn(outDto);

        // --------- Ejecución + verificaciones ---------
        mockMvc.perform(put("/alquileres/{id}", alquilerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(alquilerId))
                .andExpect(jsonPath("$.precio").value(300.0))
                .andExpect(jsonPath("$.comentario").value("Actualizado"));
    }

    // ---------------------------------------------------------
    //           TEST PUT - 400
    // ---------------------------------------------------------
    @Test
    void testActualizarAlquiler_400_PorValidacion() throws Exception {

        long alquilerId = 1L;

        AlquilerUpdateDto updateDto = new AlquilerUpdateDto();
        updateDto.setPrecio(-10.0f); // inválido

        mockMvc.perform(put("/alquileres/{id}", alquilerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.precio").exists());
    }

    // ---------------------------------------------------------
    //           TEST PUT - 404
    // ---------------------------------------------------------
    @Test
    void testActualizarAlquiler_404_NoExiste() throws Exception {

        long alquilerId = 99L;

        AlquilerUpdateDto updateDto = new AlquilerUpdateDto();
        updateDto.setPrecio(200.0f);

        when(alquilerService.modificar(eq(alquilerId), any(AlquilerUpdateDto.class)))
                .thenThrow(new AlquilerNoEncontradoException());

        mockMvc.perform(put("/alquileres/{id}", alquilerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    //           TEST DELETE - 204
    // ---------------------------------------------------------
    @Test
    void testEliminarAlquiler_204() throws Exception {

        long alquilerId = 1L;

        // delete devuelve void → doNothing()
        doNothing().when(alquilerService).eliminar(alquilerId);

        mockMvc.perform(delete("/alquileres/{id}", alquilerId))
                .andExpect(status().isNoContent());
    }

    // ---------------------------------------------------------
    //           TEST DELETE - 404
    // ---------------------------------------------------------
    @Test
    void testEliminarAlquiler_404_NoExiste() throws Exception {

        long alquilerId = 99L;

        doThrow(new AlquilerNoEncontradoException())
                .when(alquilerService).eliminar(alquilerId);

        mockMvc.perform(delete("/alquileres/{id}", alquilerId))
                .andExpect(status().isNotFound());
    }
}
