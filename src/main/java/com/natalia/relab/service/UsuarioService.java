package com.natalia.relab.service;


import com.natalia.relab.dto.UsuarioInDto;
import com.natalia.relab.dto.UsuarioOutDto;
import com.natalia.relab.dto.UsuarioUpdateDto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import exception.NicknameYaExisteException;
import exception.UsuarioNoEncontradoException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class UsuarioService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired                                     // Así hacemos que la capa Service pueda comunicarse con la Repository.  Crea una instancia de la clase en repository cada vez que llame a metodos de la capa service
    private UsuarioRepository usuarioRepository;

    // --- POST
    public UsuarioOutDto agregar(UsuarioInDto usuarioInDto) {

        // Valido que el nickname no esté en uso
        if (usuarioRepository.existsByNickname(usuarioInDto.getNickname())) {
            throw new NicknameYaExisteException();
        }

        // Creo usuario
        Usuario usuario = modelMapper.map(usuarioInDto, Usuario.class);
        // Fecha automática del sistema
        usuario.setFechaAlta(LocalDate.now());


        // Guardar y devolver DTO
        Usuario guardado = usuarioRepository.save(usuario);
        return mapToOutDto(guardado);
    }


    // --- GET por id
    public UsuarioOutDto buscarPorId(long id) throws UsuarioNoEncontradoException {
       Usuario usuario = usuarioRepository.findById(id)
               .orElseThrow(UsuarioNoEncontradoException::new);
       return mapToOutDto(usuario);
    }

    // --- GET con FILTRADO dinámico
    public List<UsuarioOutDto> listarConFiltros(
            String nickname,
            String password,
            String tipoUsuario,
            Boolean cuentaActiva) throws UsuarioNoEncontradoException {

        // Login (nickname + password)
        if (nickname != null && !nickname.isEmpty() && password != null && !password.isEmpty()) {
            Usuario usuario = usuarioRepository.findByNicknameAndPassword(nickname, password)
                    .orElseThrow(UsuarioNoEncontradoException::new);
            return List.of(mapToOutDto(usuario));
        }

        // Filtrado por nickname
        if (nickname != null && !nickname.isEmpty()) {
            Usuario usuario = usuarioRepository.findByNickname(nickname)
                    .orElseThrow(UsuarioNoEncontradoException::new);
            return List.of(mapToOutDto(usuario));
        }

        // Filtrado por tipoUsuario
        if (tipoUsuario != null && !tipoUsuario.isEmpty()) {
            return usuarioRepository.findByTipoUsuario(tipoUsuario)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        // Filtrado por cuentaActiva
        if (cuentaActiva != null) {
            return usuarioRepository.findByCuentaActiva(cuentaActiva)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        // Sin filtros → todos los usuarios
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }


    // --- PUT / modificar
    public UsuarioOutDto modificar(long id, UsuarioUpdateDto usuarioUpdateDto) throws UsuarioNoEncontradoException {
        Usuario usuarioAnterior = usuarioRepository.findById(id) //Tal y como estaba en la BBDD
                .orElseThrow(UsuarioNoEncontradoException::new);

        // Verifico si el nickname del nuevo está en uso por OTRO usuario
        if (usuarioRepository.existsByNickname(usuarioUpdateDto.getNickname()) // Revisamos si el nickname ya está en la BBDD
                && !usuarioAnterior.getNickname().equals(usuarioUpdateDto.getNickname())) { // Nos aseguramos de que quien tiene ese nickname no sea el mismo usuario que estamos modificando
            throw new NicknameYaExisteException();
        }

        // Mapeo automático sobre el objeto existente
        modelMapper.map(usuarioUpdateDto, usuarioAnterior);

        Usuario actualizado = usuarioRepository.save(usuarioAnterior);
        return mapToOutDto(actualizado);
    }

    // --- DELETE
    public void eliminar(long id) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNoEncontradoException::new);
        usuarioRepository.delete(usuario);
    }

    // --- Metodo auxiliar privado para mapear y no repetir código: para volcar datos de usuario a usuarioOutDto
    private UsuarioOutDto mapToOutDto(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioOutDto.class);
    }
}
