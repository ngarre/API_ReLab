package com.natalia.relab.service;


import com.natalia.relab.dto.UsuarioInDto;
import com.natalia.relab.dto.UsuarioOutDto;
import com.natalia.relab.dto.UsuarioUpdateDto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import exception.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class UsuarioService {

    @Autowired                                     // Así hacemos que la capa Service pueda comunicarse con la Repository.  Crea una instancia de la clase en repository cada vez que llame a metodos de la capa service
    private UsuarioRepository usuarioRepository;

    // --- POST
    public UsuarioOutDto agregar(UsuarioInDto usuarioInDto) {

        // Creo usuario
        Usuario usuario = new Usuario();
        usuario.setNickname(usuarioInDto.getNickname());
        usuario.setPassword(usuarioInDto.getPassword());
        usuario.setNombre(usuarioInDto.getNombre());
        usuario.setApellido(usuarioInDto.getApellido());
        usuario.setEmail(usuarioInDto.getEmail());
        usuario.setFechaNacimiento(usuarioInDto.getFechaNacimiento());
        usuario.setCuentaActiva(usuarioInDto.isCuentaActiva());

        // Fecha automática del sistema
        usuario.setFechaAlta(LocalDate.now());

        usuario.setTipoUsuario(usuarioInDto.getTipoUsuario());
        usuario.setAdmin(usuarioInDto.isAdmin());
        usuario.setSaldo(usuarioInDto.getSaldo());
        usuario.setLatitud(usuarioInDto.getLatitud());
        usuario.setLongitud(usuarioInDto.getLongitud());



        Usuario guardado = usuarioRepository.save(usuario);
        return mapToOutDto(guardado);
    }


    // --- GET todos
    public List<UsuarioOutDto> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // --- GET por id
    public UsuarioOutDto buscarPorId(long id) throws UsuarioNoEncontradoException {
       Usuario usuario = usuarioRepository.findById(id)
               .orElseThrow(UsuarioNoEncontradoException::new);
       return mapToOutDto(usuario);
    }

    // --- PUT / modificar
    public UsuarioOutDto modificar(long id, UsuarioUpdateDto usuarioUpdateDto) throws UsuarioNoEncontradoException {
        Usuario usuarioAnterior = usuarioRepository.findById(id) //Tal y como estaba en la BBDD
                .orElseThrow(UsuarioNoEncontradoException::new);

        // TODO usar ModelMapper para mapear atributos entre objetos
        usuarioAnterior.setNickname(usuarioUpdateDto.getNickname());
        usuarioAnterior.setPassword(usuarioUpdateDto.getPassword());
        usuarioAnterior.setNombre(usuarioUpdateDto.getNombre());
        usuarioAnterior.setApellido(usuarioUpdateDto.getApellido());
        usuarioAnterior.setEmail(usuarioUpdateDto.getEmail());
        usuarioAnterior.setFechaNacimiento(usuarioUpdateDto.getFechaNacimiento());
        usuarioAnterior.setCuentaActiva(usuarioUpdateDto.isCuentaActiva());
//        usuarioAnterior.setFechaAlta(usuarioUpdateDto.getFechaAlta());
        usuarioAnterior.setTipoUsuario(usuarioUpdateDto.getTipoUsuario());
//        usuarioAnterior.setAdmin(usuarioUpdateDto.isAdmin());
//        usuarioAnterior.setSaldo(usuarioUpdateDto.getSaldo());
        usuarioAnterior.setLatitud(usuarioUpdateDto.getLatitud());
        usuarioAnterior.setLongitud(usuarioUpdateDto.getLongitud());

        Usuario actualizado = usuarioRepository.save(usuarioAnterior);
        return mapToOutDto(actualizado);
    }

    // --- DELETE
    public void eliminar(long id) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNoEncontradoException::new);
        usuarioRepository.delete(usuario);
    }

    // --- Metodo auxiliar privado para mapear y no repetir código
    private UsuarioOutDto mapToOutDto(Usuario usuario) {

        return new UsuarioOutDto(
                usuario.getId(),
                usuario.getNickname(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getFechaNacimiento(),
                usuario.isCuentaActiva(),
                usuario.getFechaAlta(),
                usuario.getTipoUsuario(),
                usuario.isAdmin(),
                usuario.getSaldo(),
                usuario.getLatitud(),
                usuario.getLongitud()
        );

    }
}
