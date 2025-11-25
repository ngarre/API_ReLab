package com.natalia.relab.service;

import com.natalia.relab.dto.*;
import com.natalia.relab.model.Categoria;
import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.CategoriaRepository;
import com.natalia.relab.repository.ProductoRepository;
import com.natalia.relab.repository.UsuarioRepository;
import exception.CategoriaNoEncontradaException;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- POST
    // Metodo en desuso que no permite subida con imagen:
//    public ProductoOutDto agregar(ProductoInDto productoInDto) throws CategoriaNoEncontradaException, UsuarioNoEncontradoException {
//
//            // Busco categoria en la BBDD, es opcional ponerla.  Pero en caso de no existir una categoría con ese ID salta excepción.
//            Categoria categoria = null;
//            if (productoInDto.getCategoriaId() != null) {
//                categoria = categoriaRepository.findById(productoInDto.getCategoriaId())
//                    .orElseThrow(CategoriaNoEncontradaException::new);
//            }
//            // Busco el usuario en la BBDD
//            Usuario usuario = usuarioRepository.findById(productoInDto.getUsuarioId())
//                    .orElseThrow(UsuarioNoEncontradoException::new);
//
//            // Creo producto
//            // 1. Mapeo datos simples con ModelMapper
//            Producto producto = modelMapper.map(productoInDto, Producto.class);
//
//            // 2. Campos adicionales que no vienen del DTO como objetos. ModelMapper no sabe transformar un id de categoría en un objeto Categoría.
//            producto.setFechaActualizacion(LocalDate.now());
//            producto.setCategoria(categoria);
//            producto.setUsuario(usuario);
//
//            Producto guardado = productoRepository.save(producto);
//            return mapToOutDto(guardado);
//    }

    // --- POST NUEVO que permite subida CON IMAGEN
    public ProductoOutDto agregarConImagen(ProductoInDto productoInDto)
            throws CategoriaNoEncontradaException, UsuarioNoEncontradoException {

        log.info("Servicio: creando producto '{}'", productoInDto.getNombre());

        // Busco la categoría
        Categoria categoria = null; // Inicializo a null por si no viene categoría en el DTO y evitar NullPointerException
        if (productoInDto.getCategoriaId() != null) {

            // Es DEBUG porque es un detalle técnico del flujo, no un evento significativo para INFO
            log.debug("Buscando categoría con ID {}", productoInDto.getCategoriaId());

            categoria = categoriaRepository.findById(productoInDto.getCategoriaId())
                    .orElseThrow(() -> {
                        log.warn("Categoría {} no encontrada", productoInDto.getCategoriaId());
                        return new CategoriaNoEncontradaException();
                    });
        }

        // Busco el usuario
        log.debug("Buscando usuario con ID {}", productoInDto.getUsuarioId());
        Usuario usuario = usuarioRepository.findById(productoInDto.getUsuarioId())
                .orElseThrow(() -> {
            log.warn("Usuario {} no encontrado", productoInDto.getUsuarioId());
            return new UsuarioNoEncontradoException();
        });

        // Creo el producto a partir del DTO
        Producto producto = modelMapper.map(productoInDto, Producto.class);

        log.debug("Producto mapeado. Imagen incluida: {}", productoInDto.getImagen() != null);

        // Establecemos los valores adicionales
        producto.setFechaActualizacion(LocalDate.now());
        producto.setCategoria(categoria);
        producto.setUsuario(usuario);

        // Si hay imagen en el DTO, la agrego
        if (productoInDto.getImagen() != null) {
            producto.setImagen(productoInDto.getImagen());  // Aquí guardo la imagen en el producto
        }

        // Guardo el producto
        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado con ID {}", guardado.getId());

        return mapToOutDto(guardado);
    }


        // --- GET por id
    public ProductoOutDto buscarPorId(long id) throws ProductoNoEncontradoException {

        log.debug("Servicio: buscando producto {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto {} no encontrado", id);
                    return new ProductoNoEncontradoException();
                });

        return mapToOutDto(producto);
    }


    // --- GET con FILTRADO dinámico
    public List<ProductoOutDto> listarConFiltrado(
            String nombre,
            Boolean activo,
            Long categoriaId,
            Long usuarioId) throws UsuarioNoEncontradoException, CategoriaNoEncontradaException {

        log.debug("Servicio: filtrando productos. nombre={}, activo={}, categoriaId={}, usuarioId={}",
                nombre, activo, categoriaId, usuarioId);

        // Filtrado por nombre --> Coincidencias parciales y sin distinguir mayúsculas y minúsculas
        if (nombre != null && !nombre.trim().isEmpty()) { // Evito buscar con cadena vacía de cara a los tests unitarios y que no se llegue a poder hacer filtrado por categoriaId
            return productoRepository.findByNombreContainingIgnoreCase(nombre)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        if (activo != null) {
            return productoRepository.findByActivo(activo)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        if (categoriaId != null) {
            boolean existe = categoriaRepository.existsById(categoriaId);
            if (!existe) {
                throw new CategoriaNoEncontradaException();
            }

            return productoRepository.findByCategoriaId(categoriaId)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        if (usuarioId != null) {
            boolean existe = usuarioRepository.existsById(usuarioId);
            if (!existe) {
                throw new UsuarioNoEncontradoException();
            }

            return productoRepository.findByUsuarioId(usuarioId)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();

        }

        return productoRepository.findAll()
                .stream()
                .map(this::mapToOutDto)
                .toList();

    }


    // --- PUT / modificar --> Sin imagen
//    public ProductoOutDto modificar(long id, ProductoUpdateDto productoUpdateDto) throws ProductoNoEncontradoException, CategoriaNoEncontradaException {
//        Producto productoAnterior = productoRepository.findById(id)
//                .orElseThrow(ProductoNoEncontradoException::new);
//
//        Categoria categoria = categoriaRepository.findById(productoUpdateDto.getCategoriaId())
//                .orElseThrow(CategoriaNoEncontradaException::new);
//
//
//        // Mapear cambios simples del updateDto sobre el producto existente
//        modelMapper.map(productoUpdateDto, productoAnterior);
//
//        productoAnterior.setFechaActualizacion(LocalDate.now());
//        productoAnterior.setCategoria(categoria);
//        // No permito cambiar usuario.  No toco productoAnterior.setUsuario()
//
//        Producto actualizado = productoRepository.save(productoAnterior);
//        return mapToOutDto(actualizado);
//    }


    // --- PUT NUEVO --> Permite actualizar imagen
    public ProductoOutDto actualizarConImagen(long id, ProductoUpdateDto productoUpdateDto)
            throws ProductoNoEncontradoException, CategoriaNoEncontradaException {

        log.info("Servicio: actualizando producto {}", id);

        // Buscar el producto existente
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto {} no encontrado al intentar actualizar", id);
                    return new ProductoNoEncontradoException();
                });

        log.debug("Mapeando campos del DTO al producto {}", id);
        modelMapper.map(productoUpdateDto, productoExistente); // Mapear los cambios del DTO al producto existente (excepto categoria e imagen)

        // Actualizo la fecha de la actualización
        productoExistente.setFechaActualizacion(LocalDate.now());

        // ----------- CATEGORÍA ------------
        if (productoUpdateDto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(productoUpdateDto.getCategoriaId())
                    .orElseThrow(() -> {
                        log.warn("Categoría {} no encontrada en actualización de producto {}", productoUpdateDto.getCategoriaId(), id);
                        return new CategoriaNoEncontradaException();
                    });
            productoExistente.setCategoria(categoria);
        }

        // ----------- IMAGEN ------------
        if (productoUpdateDto.getImagen() != null) {
            log.debug("Actualizando imagen del producto {}", id);
            productoExistente.setImagen(productoUpdateDto.getImagen());
        }

        // Guardo el producto actualizado
        Producto actualizado = productoRepository.save(productoExistente);

        // Devuelvo el DTO del producto actualizado
        log.info("Producto {} actualizado correctamente", id);
        return mapToOutDto(actualizado);
    }


    // --- DELETE
    public void eliminar(long id) throws ProductoNoEncontradoException {

        log.warn("Servicio: eliminando producto {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto {} no encontrado al intentar eliminar", id);
                    return new ProductoNoEncontradoException();
                });
        productoRepository.delete(producto);

        log.warn("Producto {} eliminado correctamente", id);
    }

    // --- Metodo para devolver entidad completa del producto (incluyendo la imagen)
    public Producto buscarPorIdEntidad(Long id) throws ProductoNoEncontradoException {
        return productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);
    }

    // --- Metodo auxiliar privado para mapear y no repetir código

    // Utilizo un mapeo manual aquí en lugar de ModelMapper porque ProductoOutDto
    // contiene campos anidados (CategoriaSimpleDto y UsuarioSimpleDto) y un campo calculado (imagenUrl)
    // que no existen en la entidad Producto. ModelMapper no puede inferir correctamente estos DTOs anidados
    // ni generar la URL de la imagen automáticamente, por lo que el mapeo manual es más claro y seguro.

    private ProductoOutDto mapToOutDto(Producto producto) {
        CategoriaSimpleDto categoriaSimple = null;  // Para que no salte el NullPointerException en caso de que la categoria sea NULL
        if (producto.getCategoria() != null) {
            categoriaSimple = new CategoriaSimpleDto(
                    producto.getCategoria().getId(),
                    producto.getCategoria().getNombre()
            );
        }

        UsuarioSimpleDto usuarioSimple = null;
        if (producto.getUsuario() != null) {
            usuarioSimple = new UsuarioSimpleDto(
                    producto.getUsuario().getId(),
                    producto.getUsuario().getNickname()
            );
        }

        return new ProductoOutDto(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getFechaActualizacion(),
                producto.isActivo(),
                producto.isModo(),
                categoriaSimple,
                usuarioSimple,
                "/productos/"  + producto.getId() + "/imagen"
        );

    }
}

