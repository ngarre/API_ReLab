package com.natalia.relab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewsOutDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;      // Cojo la fecha del sistema
    private long idUsuario;       // el usuario reseñado
    private String comentario;
    private int puntuacion;


}