package com.natalia.relab.service;


import com.natalia.relab.dto.UsuarioInDto;
import com.natalia.relab.dto.UsuarioOutDto;
import com.natalia.relab.dto.UsuarioUpdateDto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import exception.NicknameYaExisteException;
import exception.UsuarioNoEncontradoException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired                                     // Así hacemos que la capa Service pueda comunicarse con la Repository.  Crea una instancia de la clase en repository cada vez que llame a metodos de la capa service
    private UsuarioRepository usuarioRepository;

    // --- POST
    public UsuarioOutDto agregar(UsuarioInDto usuarioInDto) {

        log.info("Agregando usuario con nickname {}", usuarioInDto.getNickname());

        // Valido que el nickname no esté en uso
        if (usuarioRepository.existsByNickname(usuarioInDto.getNickname())) {
            log.error("Error: nickname {} ya existe", usuarioInDto.getNickname());
            throw new NicknameYaExisteException();
        }

        // Creo usuario
        Usuario usuario = modelMapper.map(usuarioInDto, Usuario.class);
        // Fecha automática del sistema
        usuario.setFechaAlta(LocalDate.now());


        // Guardar y devolver DTO
        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado con id {}", guardado.getId());
        return mapToOutDto(guardado);
    }

    // --- GET por id
    public UsuarioOutDto buscarPorId(long id) throws UsuarioNoEncontradoException {
        log.info("Buscando usuario por id {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Usuario con id {} no encontrado", id);
                    return new UsuarioNoEncontradoException();
                });

       return mapToOutDto(usuario);
    }

    // --- GET con FILTRADO dinámico
    public List<UsuarioOutDto> listarConFiltros(
            String nickname,
            String password,
            String tipoUsuario,
            Boolean cuentaActiva) throws UsuarioNoEncontradoException {

        log.info("Listando usuarios con filtros nickname={}, tipoUsuario={}, cuentaActiva={}",
                nickname, tipoUsuario, cuentaActiva);

        // Login (nickname + password): Caso especial para autenticación en la aplicación Android
        if (nickname != null && !nickname.isEmpty() && password != null && !password.isEmpty()) {
            Usuario usuario = usuarioRepository.findByNicknameAndPassword(nickname, password)
                    .orElseThrow(UsuarioNoEncontradoException::new);
            return List.of(mapToOutDto(usuario));
        }

        // Parto de todos los usuarios
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Filtro por nickname
        if (nickname != null && !nickname.isEmpty()) {
            usuarios = usuarios.stream()
                    .filter(usuario -> usuario.getNickname().equals(nickname))
                    .toList();
        }

        // Filtrado por tipoUsuario
        if (tipoUsuario != null && !tipoUsuario.isEmpty()) {
            usuarios = usuarios.stream()
                    .filter(usuario -> usuario.getTipoUsuario().equalsIgnoreCase(tipoUsuario))
                    .toList();
        }

        // Filtrado por cuentaActiva
        if (cuentaActiva != null) {
            usuarios = usuarios.stream()
                    .filter(usuario -> usuario.isCuentaActiva() == cuentaActiva)
                    .toList();
        }

        return usuarios.stream()
                .map(this::mapToOutDto)
                .toList();
    }


    // --- PUT / modificar
    public UsuarioOutDto modificar(long id, UsuarioUpdateDto usuarioUpdateDto) throws UsuarioNoEncontradoException {
        log.info("Modificando usuario con id {}", id);

        Usuario usuarioAnterior = usuarioRepository.findById(id) // Tal y como estaba en la BBDD
                .orElseThrow(() -> {
                    log.error("Usuario {} no encontrado para modificar", id);
                    return new UsuarioNoEncontradoException();
                });

        // Verifico si el nickname del nuevo está en uso por OTRO usuario
        if (usuarioRepository.existsByNickname(usuarioUpdateDto.getNickname()) // Revisamos si el nickname ya está en la BBDD
                && !usuarioAnterior.getNickname().equals(usuarioUpdateDto.getNickname())) { // Nos aseguramos de que quien tiene ese nickname no sea el mismo usuario que estamos modificando

            log.error("Nickname {} ya está en uso", usuarioUpdateDto.getNickname());
            throw new NicknameYaExisteException();
        }

        // Mapeo automático sobre el objeto existente
        modelMapper.map(usuarioUpdateDto, usuarioAnterior);

        Usuario actualizado = usuarioRepository.save(usuarioAnterior);

        log.info("Usuario {} modificado correctamente", id);
        return mapToOutDto(actualizado);
    }

    // --- DELETE
    public void eliminar(long id) throws UsuarioNoEncontradoException {
        log.warn("Eliminando usuario con id {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No se puede eliminar, el usuario {} no existe", id);
                    return new UsuarioNoEncontradoException();
                });

        usuarioRepository.delete(usuario);
        log.info("Usuario {} eliminado", id);
    }

    // --- Metodo auxiliar privado para mapear y no repetir código: para volcar datos de usuario a usuarioOutDto
    private UsuarioOutDto mapToOutDto(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioOutDto.class);
    }
}
