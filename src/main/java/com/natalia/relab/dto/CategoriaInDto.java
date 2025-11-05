package com.natalia.relab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaInDto {
    private String nombre;
    private String descripcion;
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate fechaCreacion;
    private boolean activa;
    private float tasaComision;
}
