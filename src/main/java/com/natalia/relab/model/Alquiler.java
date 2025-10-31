package com.natalia.relab.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "alquileres")
public class Alquiler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaInicio;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaFin; // Me ayudará para saber si un producto está disponible o no de forma más sencilla.
    private int meses;
    private float precio;
    private String comentario;
    private boolean cancelado;

    // RELACIÓN CON LA TABLA PRODUCTO
    @ManyToOne //  Muchos registros de alquiler apuntan a un mismo producto.
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // RELACIÓN CON LA TABLA USUARIOS - ARRENDADOR
    @ManyToOne // Un usuario puede realizar varios alquileres, pero un alquiler solo es hecho por un usuario a la vez.  Sería ManyToMany si en cada registro de alquiler aparecieran varios productos alquilados.
    @JoinColumn(name = "arrendador_id")
    private Usuario arrendador;

    // RELACIÓN CON LA TABLA USUARIOS - ARRENDATARIO
    @ManyToOne
    @JoinColumn(name = "arrendatario_id")
    private Usuario arrendatario;

}
