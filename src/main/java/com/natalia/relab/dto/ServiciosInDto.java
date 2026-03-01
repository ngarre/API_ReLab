package com.natalia.relab.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiciosInDto {
    private long idUsuario;       // id Usuario que proporciona el servicio
    private String nickname;
    private int tipoServicio;     // 1=Calibración, 2=Mantenimiento, 3=Reparación
    private String descripcion;
    private String comentario;
    private String email;
    private String telefono;
    private double precio;
}
