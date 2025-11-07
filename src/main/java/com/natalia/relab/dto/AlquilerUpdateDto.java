package com.natalia.relab.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlquilerUpdateDto {

    @Min(value=0, message = "El precio debe ser mayor que cero" )
    private float precio;
    private String comentario;
    private boolean cancelado;

}
