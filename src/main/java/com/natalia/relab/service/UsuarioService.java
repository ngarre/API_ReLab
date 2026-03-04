package com.natalia.relab.service;


import com.natalia.relab.dto.UsuarioInDto;
import com.natalia.relab.dto.UsuarioOutDto;
import com.natalia.relab.dto.UsuarioUpdateDto;
import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.*;
import exception.NicknameYaExisteException;
import exception.UsuarioNoEncontradoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private ServiciosRepository serviciosRepository;

    @Autowired
    private CompraventaRepository compraVentaRepository;

    @Autowired
    private AlquilerRepository alquilerRepository;

    @PersistenceContext
    private EntityManager entityManager; // Limpia memoria interna de Hibernate
    // para evitar problemas de memoria al eliminar cuentas con muchos datos relacionados

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
        // Para una autenticación no tiene sentido traer una lista, pero lo hago así para reutilizar el DTO de salida
        if (nickname != null && !nickname.isEmpty() && password != null && !password.isEmpty()) {
            Usuario usuario = usuarioRepository.findByNicknameAndPassword(nickname, password)
                    .orElseThrow(UsuarioNoEncontradoException::new);
            return List.of(mapToOutDto(usuario));
        }

        // Parto de todos los usuarios
        // El filtrado lo haré en memoria y no en la BBDD
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Filtro por nickname
        if (nickname != null && !nickname.isEmpty()) {
            usuarios = usuarios.stream()
                    .filter(usuario -> usuario.getNickname().equals(nickname))
                    .toList();
            if (usuarios.isEmpty()) {
                log.error("No se encontraron usuarios con nickname {}", nickname);
                throw new UsuarioNoEncontradoException();
            }
        }

        // Filtrado por tipoUsuario
        if (tipoUsuario != null && !tipoUsuario.isEmpty()) {
            // Convierto la lista en un flujo de datos, aplico el filtro y vuelvo a convertirlo en lista
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

    // --- DELETE de una CUENTA (o sea, eliminar usuario y datos relacionados en resto de tablas)
    @Transactional
    public void eliminarCuenta(Long usuarioId) throws UsuarioNoEncontradoException {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(UsuarioNoEncontradoException::new);


        // Reviews
        reviewsRepository.deleteByUsuarioId(usuarioId);

        // Servicios
        serviciosRepository.deleteByUsuarioId(usuarioId);

        // Compraventas donde el usuario es comprador
        compraVentaRepository.deleteByCompradorId(usuarioId);

        // Productos del usuario
        List<Producto> productos = productoRepository.findByUsuarioId(usuarioId);

        // Compraventas que referencian los productos del usuario
        for (Producto p : productos) {
            compraVentaRepository.deleteByProductoId(p.getId());
        }

        // Alquileres donde el usuario es arrendatario
        alquilerRepository.deleteByArrendatarioId(usuarioId);

        // Borrar productos del usuario
        productoRepository.deleteByUsuarioId(usuarioId);

        entityManager.flush(); // Asegura que se ejecuten las operaciones de borrado en la base de datos antes de continuar
        entityManager.clear(); // Limpia el contexto de persistencia para evitar problemas de memoria y referencias a entidades eliminadas

        // Crear un nuevo objeto Usuario para evitar referencias a la entidad eliminada en el contexto de persistencia
        Usuario usuarioFresco = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    log.error("No se puede eliminar, el usuario {} no existe", usuarioId);
                    return new UsuarioNoEncontradoException();
                });

        // Borrar usuario
        usuarioRepository.delete(usuarioFresco);
    }

    // --- Metodo auxiliar privado para mapear y no repetir código: para volcar datos de usuario a usuarioOutDto
    private UsuarioOutDto mapToOutDto(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioOutDto.class);
    }
}
