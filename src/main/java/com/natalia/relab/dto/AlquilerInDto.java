package com.natalia.relab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlquilerInDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
    private int meses;
    @Min(value=0, message = "El precio debe ser mayor que cero" )
    private float precio;
    private String comentario;
    private boolean cancelado;
    @NotNull(message = "Debe especificarse el producto alquilado")
    private Long productoId;
    @NotNull(message = "Debe especificarse el arrendador")
    private Long arrendadorId;
    @NotNull(message = "Debe especificarse el arrendatario")
    private Long arrendatarioId;

}
