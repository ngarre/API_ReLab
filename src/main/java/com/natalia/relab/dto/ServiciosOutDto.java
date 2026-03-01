package com.natalia.relab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiciosOutDto {
    private long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;      // Cojo la fecha del sistema
    private long idUsuario;       // id Usuario que proporciona el servicio
    private String nickname;
    private int tipoServicio;     // 1=Calibración, 2=Mantenimiento, 3=Reparación
    private String descripcion;
    private String comentario;
    private String email;
    private String telefono;
    private double precio;
}
