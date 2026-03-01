package com.natalia.relab.controller;

import com.natalia.relab.dto.ReviewsInDto;
import com.natalia.relab.dto.ReviewsOutDto;
import com.natalia.relab.service.ReviewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewsController {

    private static final Logger log = LoggerFactory.getLogger(ReviewsController.class);

    @Autowired
    private ReviewsService reviewsService;

    // GET --> Obtener reviews por idUsuario
    @GetMapping("/reviews/usuario/{idUsuario}")
    public List<ReviewsOutDto> obtenerPorIdUsuario(@PathVariable Long idUsuario) {
        log.info("GET /reviews/usuario/{} - solicitando reviews por ID de usuario", idUsuario);
        return reviewsService.buscarPorIdUsuario(idUsuario);
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewsOutDto> agregarReview(@RequestBody ReviewsInDto reviewsInDto) {
        log.info("POST /reviews - creando nueva review con datos: {}", reviewsInDto);
        ReviewsOutDto nuevaReview = reviewsService.agregar(reviewsInDto);
        log.info("Review creada con ID {}", nuevaReview.getId());
        return new ResponseEntity<>(nuevaReview, HttpStatus.CREATED);
    }
}
