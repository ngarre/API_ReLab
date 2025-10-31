package com.natalia.relab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlquilerInDto {

    private Date fechaInicio;
    private Date fechaFin;
    private int meses;
    private float precio;
    private String comentario;
    private boolean cancelado;
    private long productoId;
    private long arrendadorId;
    private long arrendatarioId;

}
