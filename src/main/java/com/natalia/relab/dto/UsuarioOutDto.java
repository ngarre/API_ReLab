package com.natalia.relab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioOutDto {
    private long id;
    private String nickname;
    private String nombre;
    private String apellido;
    private String email;
    private Date fechaNacimiento;
    private boolean cuentaActiva;
    private Date fechaAlta;
    private String tipoUsuario;
    private Float saldo;
    private Double latitud;
    private Double longitud;
}
