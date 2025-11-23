package com.natalia.relab.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoUpdateDto {
    // No es obligatorio actualizar el nombre, pero si se envía, que no esté vacío
    @Size(min = 1, message = "El nombre no puede estar vacío")
    private String nombre;
    private String descripcion;
    @Min(value=0, message = "El precio tiene que ser mayor que cero" )
    private Float precio;
//    private LocalDate fechaActualizacion;
    private Boolean activo;
    private Boolean modo;
    private Long categoriaId;

    // Campo para recibir la imagen (en formato byte[])
    private byte[] imagen;
}

// Sirve para poder hacer la operación PUT de producto evitando
// que se pueda cambiar el usuario al cual pertenece el producto