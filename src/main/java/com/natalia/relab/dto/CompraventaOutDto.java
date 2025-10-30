package com.natalia.relab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompraventaOutDto {
    private Long id;
    private LocalDate fecha;
    private float precioFinal;
    private String comentario;
    private boolean devuelto;

    private ProductoSimpleDto producto;
    private UsuarioSimpleDto comprador;
    private UsuarioSimpleDto vendedor;
}
