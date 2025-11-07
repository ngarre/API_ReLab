package com.natalia.relab.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true) // No pueden existir dos categorías con el mismo nombre
    private String nombre;
    @Column(nullable = false)
    private String descripcion;
    @Column(name = "fecha_creacion")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCreacion;
    @Column
    private boolean activa;
    @Column(name = "tasa_comision")
    private float tasaComision; // Habrá que validar que esté entre 0 y 1

    // RELACIÓN CON LA TABLA PRODUCTO
    @OneToMany(mappedBy = "categoria") // Un registro de esta tabla se relaciona con muchos de la tabla productos.  mappedBy indica que la relación se gestiona desde la entidad Producto
    private List<Producto> productos;
}
