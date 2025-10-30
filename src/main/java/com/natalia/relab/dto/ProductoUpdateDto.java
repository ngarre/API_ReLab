package com.natalia.relab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoUpdateDto {
    private String nombre;
    private String descripcion;
    private float precio;
    private Date fechaActualizacion;
    private boolean activo;
    private long categoriaId;
}

// Sirve para poder hacer la operación PUT de producto evitando
// que se pueda cambiar el usuario al cual pertenece el producto