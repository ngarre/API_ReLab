package com.natalia.relab.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "productos")

public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String nombre;
    @Column
    private String descripcion;
    @Column
    private float precio;
    @Column(name = "fecha_actualizacion")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaActualizacion;
    @Column
    private boolean activo;

    // RELACIÓN CON TABLA CATEGORIA
    @ManyToOne // Cada producto tiene una sola categoría asociada, mientras que cada categoría puede aplicarse a varios productos.  Muchos productos se relacionan con una categoría.
    @JoinColumn(name = "categoria_id") // FK
    private Categoria categoria;

    // RELACIÓN CON TABLA USUARIOS
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) // Cada producto tiene un usuario asociado
    private Usuario usuario;

//    // RELACIÓN CON LA TABLA COMPRAVENTA -> No hace falta porque no necesito conocer las compraventas de un producto
//    @OneToOne(mappedBy = "producto")
//    private Compraventa compraventa;
}
