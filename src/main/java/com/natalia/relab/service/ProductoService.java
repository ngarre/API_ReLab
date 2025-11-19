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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class ProductoService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- POST
    public ProductoOutDto agregar(ProductoInDto productoInDto) throws CategoriaNoEncontradaException, UsuarioNoEncontradoException {

            // Busco categoria en la BBDD, es opcional ponerla.  Pero en caso de no existir una categoría con ese ID salta excepción.
            Categoria categoria = null;
            if (productoInDto.getCategoriaId() != null) {
                categoria = categoriaRepository.findById(productoInDto.getCategoriaId())
                    .orElseThrow(CategoriaNoEncontradaException::new);
            }
            // Busco el usuario en la BBDD
            Usuario usuario = usuarioRepository.findById(productoInDto.getUsuarioId())
                    .orElseThrow(UsuarioNoEncontradoException::new);

            // Creo producto
            // 1. Mapeo datos simples con ModelMapper
            Producto producto = modelMapper.map(productoInDto, Producto.class);

            // 2. Campos adicionales que no vienen del DTO como objetos. ModelMapper no sabe transformar un id de categoría en un objeto Categoría.
            producto.setFechaActualizacion(LocalDate.now());
            producto.setCategoria(categoria);
            producto.setUsuario(usuario);

            Producto guardado = productoRepository.save(producto);
            return mapToOutDto(guardado);
    }

    // --- GET todos
    public List<ProductoOutDto> listarTodos() {
        return productoRepository.findAll()
                .stream()
                .map(this::mapToOutDto) // Coge producto a producto y lo convierte al formato que me interesa, utilizando el metodo que he dejado abajo
                .toList();
    }


    // --- GET por id
    public ProductoOutDto buscarPorId(long id) throws ProductoNoEncontradoException {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);
        return mapToOutDto(producto);
    }

    // --- GET con FILTRADO por nombre (coincidencias parciales y sin tener en cuenta mayúsculas y minúsculas)
    public List<ProductoOutDto> buscarPorNombreParcial(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // --- GET con FILTRADO según si el producto está activo o no
    public List<ProductoOutDto> buscarActivos (boolean activo) {
        return productoRepository.findByActivo(activo)
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // --- GET con FILTRADO según la categoría a la que pertenezca el producto (filtrado por categoriaId)
    public List<ProductoOutDto> buscarPorCategoriaId(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId)
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // --- GET con FILTRADO según el usuario al que pertenece el producto (filtrado por usuarioId)
    public List<ProductoOutDto> buscarPorUsuarioId(Long usuarioId) {
        return productoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // --- PUT / modificar
    public ProductoOutDto modificar(long id, ProductoUpdateDto productoUpdateDto) throws ProductoNoEncontradoException, CategoriaNoEncontradaException {
        Producto productoAnterior = productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);

        Categoria categoria = categoriaRepository.findById(productoUpdateDto.getCategoriaId())
                .orElseThrow(CategoriaNoEncontradaException::new);


        // Mapear cambios simples del updateDto sobre el producto existente
        modelMapper.map(productoUpdateDto, productoAnterior);

        productoAnterior.setFechaActualizacion(LocalDate.now());
        productoAnterior.setCategoria(categoria);
        // No permito cambiar usuario.  No toco productoAnterior.setUsuario()

        Producto actualizado = productoRepository.save(productoAnterior);
        return mapToOutDto(actualizado);
    }

    // --- DELETE
    public void eliminar(long id) throws ProductoNoEncontradoException {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);
        productoRepository.delete(producto);
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

