package com.natalia.relab.service;

import com.natalia.relab.dto.*;
import com.natalia.relab.model.Alquiler;
import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.AlquilerRepository;
import com.natalia.relab.repository.ProductoRepository;
import com.natalia.relab.repository.UsuarioRepository;
import exception.AlquilerNoEncontradoException;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlquilerService {

    private static final Logger log = LoggerFactory.getLogger(AlquilerService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AlquilerRepository alquilerRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // -- POST
    public AlquilerOutDto agregar(AlquilerInDto alquilerInDto) throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        log.info("Agregando nuevo alquiler: productoId {}, arrendadorId {}, arrendatarioId {}",
                alquilerInDto.getProductoId(),
                alquilerInDto.getArrendadorId(),
                alquilerInDto.getArrendatarioId()
        );

        // Busco el producto en la base de datos
        Producto producto = productoRepository.findById(alquilerInDto.getProductoId())
                .orElseThrow(() -> {
                    log.warn("Producto con ID {} no encontrado", alquilerInDto.getProductoId());
                    return new ProductoNoEncontradoException();
                });

        // Busco usuario arrendador en la base de datos
        Usuario arrendador = usuarioRepository.findById(alquilerInDto.getArrendadorId())
                .orElseThrow(() -> {
                    log.warn("Arrendador con ID {} no encontrado", alquilerInDto.getArrendadorId());
                    return new UsuarioNoEncontradoException();
                });

        // Busco usuario arrendatario en la base de datos
        Usuario arrendatario = usuarioRepository.findById(alquilerInDto.getArrendatarioId())
                .orElseThrow(() -> {
                    log.warn("Arrendatario con ID {} no encontrado", alquilerInDto.getArrendatarioId());
                    return new UsuarioNoEncontradoException();
                });

        log.debug("Producto y usuarios validados correctamente.");

        // Creo registro de alquiler
        // 1. Mapeo datos simples con ModelMapper
        Alquiler alquiler  = modelMapper.map(alquilerInDto, Alquiler.class);

        // 2. Campos adicionales que no vienen del DTO como objetos:
        alquiler.setProducto(producto);
        alquiler.setArrendador(arrendador);
        alquiler.setArrendatario(arrendatario);

        Alquiler guardado = alquilerRepository.save(alquiler);

        log.info("Alquiler creado correctamente con ID {}", guardado.getId());
        return mapToOutDto(guardado);
    }

    // --- GET por id
    public AlquilerOutDto buscarPorId(long id) throws AlquilerNoEncontradoException {

        log.info("Servicio: buscando alquiler por ID {}", id);

        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe alquiler con ID {}", id);
                    return new AlquilerNoEncontradoException();
                });

        return mapToOutDto(alquiler);
    }


    // --- GET con FILTRADO dinámico
    public List<AlquilerOutDto> listarConFiltros(
            Long arrendadorId,
            Long arrendatarioId,
            Long productoId) throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        log.info("Servicio: Listando alquileres con filtros: arrendadorId={}, arrendatarioId={}, productoId={}",
                arrendadorId, arrendatarioId, productoId);

        // Parto de todos los alquileres
        List<Alquiler> alquileres = alquilerRepository.findAll();

        // Filtrado por arrendadorId
        if (arrendadorId != null) {
            if (!usuarioRepository.existsById(arrendadorId)) {
                throw new UsuarioNoEncontradoException();
            }
            alquileres = alquileres.stream()
                    .filter(a -> a.getArrendador() != null && a.getArrendador().getId() == arrendadorId)
                    .toList();
        }

        // Filtrado por arrendatarioId
        if (arrendatarioId != null) {
            if (!usuarioRepository.existsById(arrendatarioId)) {
                throw new UsuarioNoEncontradoException();
            }
            alquileres = alquileres.stream()
                    .filter(a -> a.getArrendatario() != null && a.getArrendatario().getId()==arrendatarioId)
                    .toList();
        }

        // Filtrado por productoid
        if (productoId != null) {
            if (!productoRepository.existsById(productoId)) {
                throw new ProductoNoEncontradoException();
            }
            alquileres = alquileres.stream()
                    .filter(a -> a.getProducto() != null && a.getProducto().getId() == productoId)
                    .toList();
        }

        //  Mapear a DTOs y devolver
        return alquileres.stream()
                .map(this::mapToOutDto)
                .toList();
    }


    // --- PUT / modificar
    public AlquilerOutDto modificar(long id, AlquilerUpdateDto alquilerUpdateDto) throws AlquilerNoEncontradoException {
        Alquiler alquilerAnterior = alquilerRepository.findById(id)
                .orElseThrow(AlquilerNoEncontradoException::new);

        log.info("Servicio: Modificando alquiler con ID {}", id);

        // Mapeo desde ProductoUpdateDto a mi Entidad Producto
        modelMapper.map(alquilerUpdateDto, alquilerAnterior);

        Alquiler actualizado = alquilerRepository.save(alquilerAnterior);
        log.info("Servicio: Alquiler actualizado correctamente con ID {}", actualizado.getId());
        return mapToOutDto(actualizado);
    }

    // --- DELETE
    public void eliminar(long id) throws AlquilerNoEncontradoException {
        log.warn("Servicio: Intentando eliminar alquiler con ID {}", id);
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(AlquilerNoEncontradoException::new);
        log.info("Alquiler con ID {} eliminado correctamente", id);
        alquilerRepository.delete(alquiler);
    }


    // -- Metodo auxiliar privado para mapear y no repetir código

    // Utilizo un mapeo manual aquí en lugar de ModelMapper porque AlquilerOutDto
    // contiene campos anidados (ProductoSimpleDto y UsuarioSimpleDto)
    // que no existen en la entidad Alquiler. ModelMapper no puede inferir correctamente estos DTOs anidados,
    // por lo que el mapeo manual es más claro y seguro.

    private AlquilerOutDto mapToOutDto (Alquiler alquiler) {

        ProductoSimpleDto productoSimple = null;
        if (alquiler.getProducto() != null) {
            productoSimple = new ProductoSimpleDto(
                    alquiler.getProducto().getId(),
                    alquiler.getProducto().getNombre()
            );
        }

        UsuarioSimpleDto arrendadorSimple = null;
        if (alquiler.getArrendador() != null) {
            arrendadorSimple = new UsuarioSimpleDto(
                    alquiler.getArrendador().getId(),
                    alquiler.getArrendador().getNickname()
            );
        }

        UsuarioSimpleDto arrendatarioSimple = null;
        if (alquiler.getArrendatario() != null) {
            arrendatarioSimple = new UsuarioSimpleDto(
                   alquiler.getArrendatario().getId(),
                   alquiler.getArrendatario().getNickname()
            );
        }

        return new AlquilerOutDto(
                alquiler.getId(),
                alquiler.getFechaInicio(),
                alquiler.getFechaFin(),
                alquiler.getMeses(),
                alquiler.getPrecio(),
                alquiler.getComentario(),
                alquiler.isCancelado(),
                productoSimple,
                arrendadorSimple,
                arrendatarioSimple
        );
    }
}


