package com.natalia.relab.service;

import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import exception.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UsuarioService {

    @Autowired                                     // Así hacemos que la capa Service pueda comunicarse con la Repository.  Crea una instancia de la clase en repository cada vez que llame a metodos de la capa service
    private UsuarioRepository usuarioRepository;

    public void agregar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }


    public List<Usuario> listarTodos() {
        List<Usuario> todosUsuarios = usuarioRepository.findAll();
        return todosUsuarios;
    }

    public Usuario buscarPorId(long id) throws UsuarioNoEncontradoException {
        return usuarioRepository.findById(id)
                .orElseThrow(UsuarioNoEncontradoException::new);
    }


    public Usuario modificar(long id, Usuario usuario) throws UsuarioNoEncontradoException {
        Usuario usuarioAnterior = usuarioRepository.findById(id) //Tal y como estaba en la BBDD
                .orElseThrow(UsuarioNoEncontradoException::new);

        // TODO usar ModelMapper para mapear atributos entre objetos
        usuarioAnterior.setNickname(usuario.getNickname());
        usuarioAnterior.setPassword(usuario.getPassword());
        usuarioAnterior.setNombre(usuario.getNombre());
        usuarioAnterior.setApellido(usuario.getApellido());
        usuarioAnterior.setEmail(usuario.getEmail());
        usuarioAnterior.setFechaNacimiento(usuario.getFechaNacimiento());
        usuarioAnterior.setCuentaActiva(usuario.isCuentaActiva());
        usuarioAnterior.setFechaAlta(usuario.getFechaAlta());
        usuarioAnterior.setTipoUsuario(usuario.getTipoUsuario());
        usuarioAnterior.setAdmin(usuario.isAdmin());

        return usuarioRepository.save(usuarioAnterior);

    }

    public void eliminar(long id) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNoEncontradoException::new);
        usuarioRepository.delete(usuario);
    }

}
