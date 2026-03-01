package com.natalia.relab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioMobileOutDto {

    private long id;
    private String nombre;
    private String apellido;
    private String password;
    private String nickname;
    private String tipoUsuario;
    private String email;
    private double latitud;
    private double longitud;
    private String direccion;
}
