package com.natalia.relab.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoOutDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private float precio;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaActualizacion;
    private boolean activo;
    private boolean modo;
    private CategoriaSimpleDto categoria;
    private UsuarioSimpleDto usuario;

    // Atributo para cargar la imagen. La app Android cargará la imagen incovando http así: http://localhost:8080/productos/1/imagen
    private String imagenUrl; // ProductoOutDto relamente NO tiene la imagen.  Solo tiene una URL.

}
