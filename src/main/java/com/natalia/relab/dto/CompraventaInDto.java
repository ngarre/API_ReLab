package com.natalia.relab.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompraventaInDto {
    @NotNull(message = "Debe especificarse el producto comprado/vendido")
    private Long productoId;
    @NotNull(message = "Debe especificarse el comprador")
    private Long compradorId;
    @NotNull(message = "Debe especificarse el vendedor")
    private Long vendedorId;
    private boolean devuelto;
    private String comentario;
    @Min(value=0, message = "El precio debe ser mayor que cero" )
    private float precioFinal;
}
