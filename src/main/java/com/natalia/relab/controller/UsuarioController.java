package com.natalia.relab.controller;

import com.natalia.relab.model.Usuario;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UsuarioController {

    @GetMapping("/usuarios")
    public List<Usuario> listarTodos() {
        return null;
    }

    @PostMapping("/usuarios")
    public void agregarUsuarios() {
    }
}
