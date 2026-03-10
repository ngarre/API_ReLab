package com.natalia.relab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natalia.relab.dto.ReviewsInDto;
import com.natalia.relab.dto.ReviewsOutDto;
import com.natalia.relab.security.JwtUtil;
import com.natalia.relab.service.ReviewsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewsController.class)
@AutoConfigureMockMvc(addFilters = false) // ← DESACTIVA SPRING SECURITY EN TESTS
public class ReviewsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewsService reviewsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void testObtenerPorIdUsuario() throws Exception {
        ReviewsOutDto review = new ReviewsOutDto();
        review.setId(1L);
        review.setIdUsuario(1L);
        review.setComentario("Muy buena app");
        review.setPuntuacion(5);
        when(reviewsService.buscarPorIdUsuario(1L)).thenReturn(List.of(review));

        mockMvc.perform(get("/reviews/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comentario").value("Muy buena app"))
                .andExpect(jsonPath("$[0].puntuacion").value(5));

        verify(reviewsService, times(1)).buscarPorIdUsuario(1L);
    }

    @Test
    void testAgregarReview() throws Exception {
        ReviewsInDto input = new ReviewsInDto();
        input.setIdUsuario(1L);
        input.setComentario("Excelente servicio");
        input.setPuntuacion(5);

        ReviewsOutDto output = new ReviewsOutDto();
        output.setId(1L);
        output.setIdUsuario(1L);
        output.setComentario("Excelente servicio");
        output.setPuntuacion(5);

        // Simula el comportamiento del servicio para que devuelva el output esperado
        when(reviewsService.agregar(any(ReviewsInDto.class))).thenReturn(output);

        // Realiza la petición POST con el JSON de entrada
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idUsuario\":1,\"comentario\":\"Excelente servicio\",\"puntuacion\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comentario").value("Excelente servicio"))
                .andExpect(jsonPath("$.puntuacion").value(5));

        verify(reviewsService, times(1)).agregar(any(ReviewsInDto.class));
    }
}