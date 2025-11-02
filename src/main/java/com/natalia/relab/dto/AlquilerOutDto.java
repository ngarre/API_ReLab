package com.natalia.relab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlquilerOutDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
    private int meses;
    private float precio;
    private String comentario;
    private boolean cancelado;

    private ProductoSimpleDto producto;
    private UsuarioSimpleDto arrendador;
    private UsuarioSimpleDto arrendatario;
}
