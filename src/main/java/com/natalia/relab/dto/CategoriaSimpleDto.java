package com.natalia.relab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaSimpleDto {
    private Long id;
    private String nombre;
}


//VERSIÓN REDUCIDA DE LA CATEGORÍA: solo con los campos básicos que te interesa mostrar junto al PRODUCTO al devolver productos.