package com.natalia.relab.service;

import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired                                     // Así hacemos que la capa Service pueda comunicarse con la Repository.  Crea una instancia de la clase en repository cada vez que llame a metodos de la capa service
    private UsuarioRepository usuarioRepository;

    public void agregar(Usuario usuario) {}

    public void eliminar(Usuario usuario) {}

    public List<Usuario> listarTodos() {
        List<Usuario> todosUsuarios = usuarioRepository.findAll();
        return todosUsuarios;
    }

    public Usuario buscarPorId(long id) {
        return null;
    }

    public void modificar(Usuario usuario) {}

}
