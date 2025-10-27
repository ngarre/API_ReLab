package com.natalia.relab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaInDto {
    private String nombre;
    private String descripcion;
    private Date fechaCreacion;
    private boolean activo;
    private float tasaComision;
}
