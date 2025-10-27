package com.natalia.relab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoInDto { // Es un objeto que utilizaré para mandar información. Será objeto JAVA que tiene justo lo que tengo para el producto.
    private String nombre;
    private String descripcion;
    private float precio;
    private Date fechaActualizacion;
    private boolean activo;
    private long categoriaId;
}
