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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlquilerService {

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

        // Busco el producto en la base de datos
        Producto producto = productoRepository.findById(alquilerInDto.getProductoId())
                .orElseThrow(ProductoNoEncontradoException::new);

        // Busco usuario arrendador en la base de datos
        Usuario arrendador = usuarioRepository.findById(alquilerInDto.getArrendadorId())
                .orElseThrow(UsuarioNoEncontradoException::new);

        // Busco usuario arrendatario en la base de datos
        Usuario arrendatario = usuarioRepository.findById(alquilerInDto.getArrendatarioId())
                .orElseThrow(UsuarioNoEncontradoException::new);

        // Creo registro de alquiler
        // 1. Mapeo datos simples con ModelMapper
        Alquiler alquiler  = modelMapper.map(alquilerInDto, Alquiler.class);

        // 2. Campos adicionales que no vienen del DTO como objetos:
        alquiler.setProducto(producto);
        alquiler.setArrendador(arrendador);
        alquiler.setArrendatario(arrendatario);

        Alquiler guardado = alquilerRepository.save(alquiler);
        return mapToOutDto(guardado);
    }

    // --- GET por id
    public AlquilerOutDto buscarPorId(long id) throws AlquilerNoEncontradoException {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(AlquilerNoEncontradoException::new);
        return mapToOutDto(alquiler);
    }


    // --- GET con FILTRADO dinámico
    public List<AlquilerOutDto> listarConFiltros(
            Long arrendadorId,
            Long arrendatarioId,
            Long productoId) throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        // Filtrado por arrendadorId
        if (arrendadorId != null) {
            // Se verifica que el usuario arrendador exista
            boolean existe = usuarioRepository.existsById(arrendadorId);
            if (!existe) {
                throw new UsuarioNoEncontradoException();
            }

            return alquilerRepository.findByArrendadorId(arrendadorId)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        // Filtrado por arrendatarioId
        if (arrendatarioId != null) {
            // Se verifica que el usuario arrendatario exista
            boolean existe = usuarioRepository.existsById(arrendatarioId);
            if (!existe) {
                throw new UsuarioNoEncontradoException();
            }

            return alquilerRepository.findByArrendatarioId(arrendatarioId)
                .stream()
                .map(this::mapToOutDto)
                .toList();
        }

        // Filtrado por productoid
        if (productoId != null) {
            // Se verifica que el producto exista
            boolean existe = productoRepository.existsById(productoId);
            if (!existe) {
                throw new ProductoNoEncontradoException();
            }

            return alquilerRepository.findByProductoId(productoId)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        // Sin filtros → todos los alquileres
        return alquilerRepository.findAll()
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

//    public List<AlquilerOutDto> listarTodos() {
//        return alquilerRepository.findAll()
//                .stream()
//                .map(this::mapToOutDto)
//                .toList();
//    }
//
//
//    // --- GET con FILTRADO por ArrendadorId
//    public List<AlquilerOutDto> buscarPorArrendadorId(Long arrendadorId) {
//        return alquilerRepository.findByArrendadorId(arrendadorId)
//                .stream()
//                .map(this::mapToOutDto)
//                .toList();
//    }
//
//    // --- GET con FILTRADO por ArrendatarioId
//    public List<AlquilerOutDto> buscarPorArrendatarioId(Long arrendatarioId) {
//        return alquilerRepository.findByArrendatarioId(arrendatarioId)
//                .stream()
//                .map(this::mapToOutDto)
//                .toList();
//    }
//
//    // --- GET con FILTRADO por ProductoId
//    public List<AlquilerOutDto> buscarPorProductoId(Long productoId) {
//        return alquilerRepository.findByProductoId(productoId)
//                .stream()
//                .map(this::mapToOutDto)
//                .toList();
//    }


    // --- PUT / modificar
    public AlquilerOutDto modificar(long id, AlquilerUpdateDto alquilerUpdateDto) throws AlquilerNoEncontradoException {
        Alquiler alquilerAnterior = alquilerRepository.findById(id)
                .orElseThrow(AlquilerNoEncontradoException::new);


        // Mapeo desde ProductoUpdateDto a mi Entidad Producto
        modelMapper.map(alquilerUpdateDto, alquilerAnterior);

        Alquiler actualizado = alquilerRepository.save(alquilerAnterior);
        return mapToOutDto(actualizado);
    }

    // --- DELETE
    public void eliminar(long id) throws AlquilerNoEncontradoException {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(AlquilerNoEncontradoException::new);
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


