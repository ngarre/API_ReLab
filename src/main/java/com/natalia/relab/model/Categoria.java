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
@Entity(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String nombre;
    @Column
    private String descripcion;
    @Column(name = "fecha_creacion")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaCreacion;
    @Column
    private boolean activo;
    @Column(name = "tasa_comision")
    private float tasaComision; // Habrá que validar que esté entre 0 y 1
}
