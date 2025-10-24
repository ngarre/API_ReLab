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
@Entity(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String nickname;
    @Column
    private String password;
    @Column
    private String nombre;
    @Column
    private String apellido;
    @Column
    private String email;
    @Column(name = "fecha_nacimiento")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;
    @Column(name = "cuenta_activa")
    private boolean cuentaActiva;
    @Column(name = "fecha_alta")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaAlta;
    @Column(name = "tipo_usuario") //Particular, empresa o centro de investigación
    private String tipoUsuario;
}
