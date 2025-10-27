package com.natalia.relab.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoOutDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private float precio;
    private Date fechaActualizacion;
    private boolean activo;
    private CategoriaSimpleDto categoria;
}
