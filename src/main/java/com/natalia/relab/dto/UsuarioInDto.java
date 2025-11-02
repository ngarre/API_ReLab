package com.natalia.relab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioInDto {
    private String nickname;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    private boolean cuentaActiva;
//    @JsonFormat(pattern = "yyyy-MM-dd") --> Al cogerse del sistema ya no la pido en el POST
//    private LocalDate fechaAlta;
    private String tipoUsuario;
    private boolean admin;
    private Float saldo;
    private Double latitud;
    private Double longitud;
}
