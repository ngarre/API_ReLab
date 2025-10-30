package com.natalia.relab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompraventaInDto {

    private long productoId;
    private long compradorId;
    private long vendedorId;
    private boolean devuelto;
    private String comentario;
    private float precioFinal;
}
