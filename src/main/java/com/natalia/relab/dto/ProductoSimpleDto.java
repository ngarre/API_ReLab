package com.natalia.relab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoSimpleDto {
    private Long id;
    private String nombre;
}


// La finalidad de este DTO es mostrar una versión abreviada del producto
// al devolver al cliente los datos de una compraventa