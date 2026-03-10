package com.natalia.relab;

import com.natalia.relab.dto.ReviewsInDto;
import com.natalia.relab.dto.ReviewsOutDto;
import com.natalia.relab.model.Reviews;
import com.natalia.relab.repository.ReviewsRepository;
import com.natalia.relab.service.ReviewsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewsServiceTests {

    @InjectMocks
    private ReviewsService reviewsService;

    @Mock
    private ReviewsRepository reviewsRepository;

    @Mock
    private ModelMapper modelMapper;

    // ---------------------------------------------------------
    //           TEST POST - agregar()
    // ---------------------------------------------------------

    @Test
    public void testAgregarReview_Exito() {
        ReviewsInDto reviewsInDto = new ReviewsInDto(
                15L,                           // idUsuario
                "Excelente servicio",          // comentario
                5                              // puntuación
        );

        // Mock del review mapeado por ModelMapper de inDto a review
        Reviews reviewMapeado = new Reviews();

        // Mock del review guardado
        Reviews reviewGuardado = new Reviews();
        reviewGuardado.setId(1L);
        reviewGuardado.setFecha(LocalDate.now());
        reviewGuardado.setIdUsuario(15L);
        reviewGuardado.setComentario("Excelente servicio");
        reviewGuardado.setPuntuacion(5);

        // Mock del DTO de salida
        ReviewsOutDto reviewsOutDto = new ReviewsOutDto(
                1L,
                LocalDate.now(),
                15L,
                "Excelente servicio",
                5
        );

        // Stubs
        when(modelMapper.map(reviewsInDto, Reviews.class)).thenReturn(reviewMapeado);
        when(reviewsRepository.save(any(Reviews.class))).thenReturn(reviewGuardado);
        when(modelMapper.map(reviewGuardado, ReviewsOutDto.class)).thenReturn(reviewsOutDto);

        // Metodo a testear
        ReviewsOutDto resultado = reviewsService.agregar(reviewsInDto);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(15L, resultado.getIdUsuario());
        assertEquals("Excelente servicio", resultado.getComentario());
        assertEquals(5, resultado.getPuntuacion());
        assertNotNull(resultado.getFecha());
    }

    @Test
    public void testAgregarReview_PuntuacionBaja() {
        ReviewsInDto reviewsInDto = new ReviewsInDto(
                20L,
                "Servicio deficiente",
                1
        );

        Reviews reviewMapeado = new Reviews();

        Reviews reviewGuardado = new Reviews();
        reviewGuardado.setId(2L);
        reviewGuardado.setFecha(LocalDate.now());
        reviewGuardado.setIdUsuario(20L);
        reviewGuardado.setComentario("Servicio deficiente");
        reviewGuardado.setPuntuacion(1);

        ReviewsOutDto reviewsOutDto = new ReviewsOutDto(
                2L,
                LocalDate.now(),
                20L,
                "Servicio deficiente",
                1
        );

        when(modelMapper.map(reviewsInDto, Reviews.class)).thenReturn(reviewMapeado);
        when(reviewsRepository.save(any(Reviews.class))).thenReturn(reviewGuardado);
        when(modelMapper.map(reviewGuardado, ReviewsOutDto.class)).thenReturn(reviewsOutDto);

        ReviewsOutDto resultado = reviewsService.agregar(reviewsInDto);

        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
        assertEquals(1, resultado.getPuntuacion());
        assertEquals("Servicio deficiente", resultado.getComentario());
    }

    @Test
    public void testAgregarReview_PuntuacionMedia() {
        ReviewsInDto reviewsInDto = new ReviewsInDto(
                25L,
                "Servicio aceptable",
                3
        );

        Reviews reviewMapeado = new Reviews();

        Reviews reviewGuardado = new Reviews();
        reviewGuardado.setId(3L);
        reviewGuardado.setFecha(LocalDate.now());
        reviewGuardado.setIdUsuario(25L);
        reviewGuardado.setComentario("Servicio aceptable");
        reviewGuardado.setPuntuacion(3);

        ReviewsOutDto reviewsOutDto = new ReviewsOutDto(
                3L,
                LocalDate.now(),
                25L,
                "Servicio aceptable",
                3
        );

        when(modelMapper.map(reviewsInDto, Reviews.class)).thenReturn(reviewMapeado);
        when(reviewsRepository.save(any(Reviews.class))).thenReturn(reviewGuardado);
        when(modelMapper.map(reviewGuardado, ReviewsOutDto.class)).thenReturn(reviewsOutDto);

        ReviewsOutDto resultado = reviewsService.agregar(reviewsInDto);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals(3, resultado.getPuntuacion());
        assertEquals("Servicio aceptable", resultado.getComentario());
    }

    // ---------------------------------------------------------
    //  TEST GET por idUsuario - buscarPorIdUsuario()
    // ---------------------------------------------------------

    @Test
    public void testBuscarPorIdUsuario_Exito() {
        long idUsuarioBuscado = 15L;

        // Review 1 del usuario
        Reviews review1 = new Reviews();
        review1.setId(1L);
        review1.setIdUsuario(idUsuarioBuscado);
        review1.setComentario("Excelente");
        review1.setPuntuacion(5);
        review1.setFecha(LocalDate.now());

        // Review 2 del usuario
        Reviews review2 = new Reviews();
        review2.setId(2L);
        review2.setIdUsuario(idUsuarioBuscado);
        review2.setComentario("Bueno");
        review2.setPuntuacion(4);
        review2.setFecha(LocalDate.now());

        // DTOs de salida
        ReviewsOutDto outDto1 = new ReviewsOutDto(
                1L, LocalDate.now(), idUsuarioBuscado, "Excelente", 5
        );
        ReviewsOutDto outDto2 = new ReviewsOutDto(
                2L, LocalDate.now(), idUsuarioBuscado, "Bueno", 4
        );

        // Stub
        when(reviewsRepository.findByIdUsuario(idUsuarioBuscado))
                .thenReturn(List.of(review1, review2));
        when(modelMapper.map(review1, ReviewsOutDto.class)).thenReturn(outDto1);
        when(modelMapper.map(review2, ReviewsOutDto.class)).thenReturn(outDto2);

        // Metodo a testear
        List<ReviewsOutDto> resultado = reviewsService.buscarPorIdUsuario(idUsuarioBuscado);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(idUsuarioBuscado, resultado.get(0).getIdUsuario());
        assertEquals(idUsuarioBuscado, resultado.get(1).getIdUsuario());
        assertEquals("Excelente", resultado.get(0).getComentario());
        assertEquals("Bueno", resultado.get(1).getComentario());
        assertEquals(5, resultado.get(0).getPuntuacion());
        assertEquals(4, resultado.get(1).getPuntuacion());
    }

    @Test
    public void testBuscarPorIdUsuario_SinReviews() {
        long idUsuarioBuscado = 999L;

        // Stub: el usuario no tiene reviews
        when(reviewsRepository.findByIdUsuario(idUsuarioBuscado))
                .thenReturn(List.of());

        // Metodo a testear
        List<ReviewsOutDto> resultado = reviewsService.buscarPorIdUsuario(idUsuarioBuscado);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    @Test
    public void testBuscarPorIdUsuario_MultiplesReviews() {
        long idUsuarioBuscado = 30L;

        // Crear 4 reviews del usuario
        Reviews review1 = new Reviews();
        review1.setId(10L);
        review1.setIdUsuario(idUsuarioBuscado);
        review1.setPuntuacion(5);

        Reviews review2 = new Reviews();
        review2.setId(11L);
        review2.setIdUsuario(idUsuarioBuscado);
        review2.setPuntuacion(4);

        Reviews review3 = new Reviews();
        review3.setId(12L);
        review3.setIdUsuario(idUsuarioBuscado);
        review3.setPuntuacion(3);

        Reviews review4 = new Reviews();
        review4.setId(13L);
        review4.setIdUsuario(idUsuarioBuscado);
        review4.setPuntuacion(2);

        // DTOs de salida
        ReviewsOutDto outDto1 = new ReviewsOutDto(10L, LocalDate.now(), idUsuarioBuscado, "", 5);
        ReviewsOutDto outDto2 = new ReviewsOutDto(11L, LocalDate.now(), idUsuarioBuscado, "", 4);
        ReviewsOutDto outDto3 = new ReviewsOutDto(12L, LocalDate.now(), idUsuarioBuscado, "", 3);
        ReviewsOutDto outDto4 = new ReviewsOutDto(13L, LocalDate.now(), idUsuarioBuscado, "", 2);

        when(reviewsRepository.findByIdUsuario(idUsuarioBuscado))
                .thenReturn(List.of(review1, review2, review3, review4));
        when(modelMapper.map(review1, ReviewsOutDto.class)).thenReturn(outDto1);
        when(modelMapper.map(review2, ReviewsOutDto.class)).thenReturn(outDto2);
        when(modelMapper.map(review3, ReviewsOutDto.class)).thenReturn(outDto3);
        when(modelMapper.map(review4, ReviewsOutDto.class)).thenReturn(outDto4);

        List<ReviewsOutDto> resultado = reviewsService.buscarPorIdUsuario(idUsuarioBuscado);

        assertEquals(4, resultado.size());
        assertEquals(10L, resultado.get(0).getId());
        assertEquals(11L, resultado.get(1).getId());
        assertEquals(12L, resultado.get(2).getId());
        assertEquals(13L, resultado.get(3).getId());
        assertEquals(5, resultado.get(0).getPuntuacion());
        assertEquals(4, resultado.get(1).getPuntuacion());
        assertEquals(3, resultado.get(2).getPuntuacion());
        assertEquals(2, resultado.get(3).getPuntuacion());
    }
}

