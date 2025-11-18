package com.natalia.relab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioUpdateDto {
    // No es obligatorio actualizar el nickname, pero si se envía, que no esté vacío
    @Size(min = 1, message = "El nickname no puede estar vacío")
    private String nickname;
    // Es opcional modificar la contraseña, pero si se cambia, que al menos tenga 4 caracteres
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;
    private String nombre;
    private String apellido;
    // Si se actualiza el email que tenga un formato válido
    @Email(message = "El formato del email no es válido")
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    private Boolean cuentaActiva;
    private String tipoUsuario;
    private Double latitud;
    private Double longitud;
}


// No se usa @NotNull, porque en una actualización parcial el usuario podría no enviar todos los campos.