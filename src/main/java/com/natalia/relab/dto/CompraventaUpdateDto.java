package com.natalia.relab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompraventaUpdateDto {
    private boolean devuelto;
    private String comentario;
    private float precioFinal;

}





// El sentido de este DTO es la operación modificar de compraventas.
// No quiero que se puedan modificar los campos: id del comprador, id del vendedor ni fecha de la transacción.
