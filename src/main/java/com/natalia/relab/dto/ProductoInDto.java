package com.natalia.relab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoInDto { // Es un objeto que utilizaré para mandar información. Será objeto JAVA que tiene justo lo que tengo para el producto.
    private String nombre;
    private String descripcion;
    private float precio;
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate fechaActualizacion;
    private boolean activo;
    private long categoriaId;
    private long usuarioId;
}
