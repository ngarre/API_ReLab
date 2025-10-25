package com.natalia.relab.controller;

import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import com.natalia.relab.service.UsuarioService;
import exception.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> ListarPorId(@PathVariable long id) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/usuarios")
    public void agregarUsuarios(@RequestBody Usuario usuario) {
        usuarioService.agregar(usuario);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> editarUsuario(@PathVariable long id, @RequestBody Usuario usuario) throws UsuarioNoEncontradoException {
        Usuario nuevoUsuario = usuarioService.modificar(id, usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable long id) throws UsuarioNoEncontradoException {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
