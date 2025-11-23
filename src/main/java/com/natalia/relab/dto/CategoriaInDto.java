package com.natalia.relab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaInDto {
    @NotBlank(message="El nombre es un campo obligatorio")
    private String nombre;
    @NotBlank(message = "La descripción es un campo obligatorio")
    @Size(min = 10, message = "La descripción debe tener al menos 10 caracteres")
    private String descripcion;
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate fechaCreacion;
    private Boolean activa;
    @DecimalMin(value = "0.0", inclusive = true, message = "El valor mínimo permitido es 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "El valor máximo permitido es 1")
    private Float tasaComision;
}
