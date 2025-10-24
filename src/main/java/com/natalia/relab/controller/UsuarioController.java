package com.natalia.relab.controller;

import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import com.natalia.relab.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public List<Usuario> listarTodos() {
        List<Usuario> todosUsuarios = usuarioService.listarTodos();
        return todosUsuarios;
    }

    @PostMapping("/usuarios")
    public void agregarUsuarios(@RequestBody Usuario usuario) {
        usuarioService.agregar(usuario);
    }
}
