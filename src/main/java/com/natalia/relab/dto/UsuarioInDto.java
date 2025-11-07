package com.natalia.relab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioInDto {
    @NotBlank(message="El nickname es un campo obligatorio") // NotNull deja pasar cadena vacía "".  Por eso utilizo NotBlank que comprueba: que no sea null, que no esté vacío y que no contenga solo espacios.
    private String nickname;
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 carácteres")
    private String password;
    private String nombre;
    private String apellido;
    @Email(message = "El formato de email no es válido") // Permite dejar el email a null, esto solo comprueba el formato en caso de que se introduzca
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    private boolean cuentaActiva;
//    @JsonFormat(pattern = "yyyy-MM-dd") --> Al cogerse del sistema ya no la pido en el POST
//    private LocalDate fechaAlta;
    private String tipoUsuario;
    private boolean admin;
    @Min(value=0, message = "El saldo tiene que ser mayor que cero" )
    private Float saldo;
    private Double latitud;
    private Double longitud;
}
