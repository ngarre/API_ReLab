package com.natalia.relab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natalia.relab.controller.ServiciosController;
import com.natalia.relab.dto.ServiciosInDto;
import com.natalia.relab.dto.ServiciosOutDto;
import com.natalia.relab.security.JwtUtil;
import com.natalia.relab.service.ServiciosService;
import exception.ServicioNoEncontradoException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiciosController.class)
@AutoConfigureMockMvc(addFilters = false)   // ← DESACTIVA SPRING SECURITY EN TESTS
public class ServiciosControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiciosService serviciosService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------------------------------------------------
    // POST /servicios - 201
    // ---------------------------------------------------------
    @Test
    void testAgregarServicio_201() throws Exception {

        ServiciosInDto inDto = new ServiciosInDto();
        inDto.setIdUsuario(1L);
        inDto.setNickname("TestUser");
        inDto.setTipoServicio(1);
        inDto.setDescripcion("Calibración de equipo");
        inDto.setComentario("Servicio rápido");
        inDto.setEmail("test@example.com");
        inDto.setTelefono("123456789");
        inDto.setPrecio(50.0);

        ServiciosOutDto outDto = new ServiciosOutDto();
        outDto.setId(1L);
        outDto.setFecha(LocalDate.now());
        outDto.setIdUsuario(1L);
        outDto.setNickname("TestUser");
        outDto.setTipoServicio(1);
        outDto.setDescripcion("Calibración de equipo");
        outDto.setComentario("Servicio rápido");
        outDto.setEmail("test@example.com");
        outDto.setTelefono("123456789");
        outDto.setPrecio(50.0);

        when(serviciosService.agregar(any(ServiciosInDto.class)))
                .thenReturn(outDto);

        mockMvc.perform(post("/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nickname").value("TestUser"))
                .andExpect(jsonPath("$.tipoServicio").value(1))
                .andExpect(jsonPath("$.descripcion").value("Calibración de equipo"));
    }

    // ---------------------------------------------------------
    // GET /servicios - 200
    // ---------------------------------------------------------
    @Test
    void testListarServicios_200() throws Exception {

        ServiciosOutDto s1 = new ServiciosOutDto();
        s1.setId(1L);
        s1.setIdUsuario(1L);
        s1.setTipoServicio(1);

        ServiciosOutDto s2 = new ServiciosOutDto();
        s2.setId(2L);
        s2.setIdUsuario(2L);
        s2.setTipoServicio(2);

        when(serviciosService.listar()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/servicios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tipoServicio").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].tipoServicio").value(2));
    }

    // ---------------------------------------------------------
    // GET /servicios/usuario/{idUsuario} - 200
    // ---------------------------------------------------------
    @Test
    void testListarServiciosPorUsuario_200() throws Exception {

        long idUsuario = 1L;

        ServiciosOutDto servicio = new ServiciosOutDto();
        servicio.setId(1L);
        servicio.setIdUsuario(idUsuario);
        servicio.setTipoServicio(1);

        when(serviciosService.buscarPorIdUsuario(idUsuario))
                .thenReturn(List.of(servicio));

        mockMvc.perform(get("/servicios/usuario/{idUsuario}", idUsuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].idUsuario").value(idUsuario));
    }

    // ---------------------------------------------------------
    // DELETE /servicios/usuario/{idUsuario} - 204
    // ---------------------------------------------------------
    @Test
    void testEliminarServiciosPorUsuario_204() throws Exception {

        long idUsuario = 1L;

        doNothing().when(serviciosService).eliminarPorIdUsuario(idUsuario);

        mockMvc.perform(delete("/servicios/usuario/{idUsuario}", idUsuario))
                .andExpect(status().isNoContent());
    }

    // ---------------------------------------------------------
    // DELETE /servicios/usuario/{idUsuario} - 404
    // ---------------------------------------------------------
    @Test
    void testEliminarServiciosPorUsuario_404() throws Exception {

        long idUsuario = 999L;

        doThrow(new ServicioNoEncontradoException())
                .when(serviciosService)
                .eliminarPorIdUsuario(idUsuario);

        mockMvc.perform(delete("/servicios/usuario/{idUsuario}", idUsuario))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // DELETE /servicios/{id} - 204
    // ---------------------------------------------------------
    @Test
    void testEliminarServicio_204() throws Exception {

        long id = 1L;

        doNothing().when(serviciosService).eliminar(id);

        mockMvc.perform(delete("/servicios/{id}", id))
                .andExpect(status().isNoContent());
    }

    // ---------------------------------------------------------
    // DELETE /servicios/{id} - 404
    // ---------------------------------------------------------
    @Test
    void testEliminarServicio_404() throws Exception {

        long id = 999L;

        doThrow(new ServicioNoEncontradoException())
                .when(serviciosService)
                .eliminar(id);

        mockMvc.perform(delete("/servicios/{id}", id))
                .andExpect(status().isNotFound());
    }
}