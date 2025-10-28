package com.natalia.relab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioSimpleDto {
    private Long id;
    private String nickname;
}

// VERSIÓN REDUCIDA DEL USUARIO: Creo este DTO para que pueda mostrarse
// como resumida del usuario junto a detalles del producto