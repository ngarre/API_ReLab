package com.natalia.relab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioUpdateDto {
    private String nickname;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    private boolean cuentaActiva;
    private String tipoUsuario;
//    private boolean admin;
//    private Float saldo;
    private Double latitud;
    private Double longitud;
}
