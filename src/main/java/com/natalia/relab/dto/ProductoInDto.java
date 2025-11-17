package com.natalia.relab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoInDto { // Es un objeto que utilizaré para mandar información. Será objeto JAVA que tiene justo lo que tengo para el producto.
    @NotBlank(message="El nombre del producto es un campo obligatorio")
    private String nombre;
    private String descripcion;
    @Min(value=0, message = "El precio tiene que ser mayor que cero" )
    private Float precio;
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private LocalDate fechaActualizacion;
    private boolean activo;
    private boolean modo;
    private long categoriaId;
    @NotNull(message = "Debe especificarse el usuario que publica el producto")
    private Long usuarioId;

    // Pongo "Long" y no "long", porque si lo pongo en minúscula en caso de que un producto
    // llegase sin el ID de usuario, Spring lo pondría automáticamente a 0,
    // ese no es un ID válido, pero no es Null y no saltarían mis validaciones.
    // Long es un objeto envoltorio del tipo long, puede contener un valor o ser null.
    // Esto permite que si en el JSON no viene el usuarioId, Spring deje el valor en Null y mis validaciones
    // @NotNull salten.
}
