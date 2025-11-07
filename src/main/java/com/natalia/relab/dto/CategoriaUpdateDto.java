package com.natalia.relab.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaUpdateDto {
    @Size(min = 1, message = "El nombre no puede estar vacío")
    private String nombre;
    @Size(min = 10, message = "La descripción debe tener al menos 10 caracteres")
    private String descripcion;
    private boolean activa;
    @DecimalMin(value = "0.0", inclusive = true, message = "El valor mínimo permitido es 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "El valor máximo permitido es 1")
    private float tasaComision;
}
