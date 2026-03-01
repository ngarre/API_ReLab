package com.natalia.relab.service;

import com.natalia.relab.dto.ReviewsInDto;
import com.natalia.relab.dto.ReviewsOutDto;
import com.natalia.relab.model.Reviews;
import com.natalia.relab.repository.ReviewsRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewsService {

    private static final Logger log = LoggerFactory.getLogger(ReviewsService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ReviewsRepository reviewsRepository;

    // --- POST
    public ReviewsOutDto agregar(ReviewsInDto reviewsInDto) {
        log.info("Intentando crear nueva review");
        log.debug("Datos recibidos: {}", reviewsInDto);

        // Creo review
        Reviews review = modelMapper.map(reviewsInDto, Reviews.class);
        // Fecha automática del sistema
        review.setFecha(LocalDate.now());

        // Guardar y devolver DTO
        Reviews guardada  = reviewsRepository.save(review);

        log.info("Review creada con ID {}", guardada.getId());
        return mapToOutDto(guardada);
    }

    // --- GET por idUsuario
    public List<ReviewsOutDto> buscarPorIdUsuario(long idUsuario) {
        log.info("Buscando reviews por ID de usuario {}", idUsuario);
        return reviewsRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }


    // --- Metodo auxiliar privado para mapear y no repetir código
    private ReviewsOutDto mapToOutDto(Reviews reviews) {
        return modelMapper.map(reviews, ReviewsOutDto.class);
    }

}



