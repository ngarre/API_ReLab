package com.natalia.relab.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true) // No puede existir más de un usuario con un mismo nickname.  También garantizo que el nickname no pueda ser null, incluso si nos saltamos la validación del InDTO.
    private String nickname;
    @Column(nullable = false)
    private String password;
    @Column
    private String nombre;
    @Column
    private String apellido;
    @Column
    private String email;
    @Column(name = "fecha_nacimiento")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    @Column(name = "cuenta_activa")
    private boolean cuentaActiva;
    @Column(name = "fecha_alta")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaAlta;
    @Column(name = "tipo_usuario") //Particular, empresa o centro de investigación
    private String tipoUsuario;
    @Column
    private boolean admin;
    @Column
    private Float saldo; // Cambio estos atributos a objetos envoltorio (de float y double a Float y Double para evitar los errores de campo vacío en la BBDD)
    @Column
    private Double latitud;
    @Column
    private Double longitud;

    // RELACIÓN CON LA TABLA PRODUCTO
    @OneToMany(mappedBy = "usuario") // Un usuario puede tener muchos productos, pero un producto solo puede pertenecer a un usuario.
    private List<Producto> productos;

    // RELACIÓN CON LA TABLA COMPRAVENTA - COMPRAS
    @OneToMany(mappedBy = "comprador")
    private List <Compraventa> compras;

    // RELACIÓN CON LA TABLA COMPRAVENTA - VENTAS
    @OneToMany(mappedBy = "vendedor")
    private List <Compraventa> ventas;
}
