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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- POST
    public ProductoOutDto agregar(ProductoInDto productoInDto) throws CategoriaNoEncontradaException, UsuarioNoEncontradoException {

        // Busco categoria en la BBDD
            Categoria categoria = categoriaRepository.findById(productoInDto.getCategoriaId())
                    .orElseThrow(CategoriaNoEncontradaException::new);
            // Busco el usuario en la BBDD
            Usuario usuario = usuarioRepository.findById(productoInDto.getUsuarioId())
                    .orElseThrow(UsuarioNoEncontradoException::new);

            // Creo producto
            Producto producto = new Producto();
            producto.setNombre(productoInDto.getNombre());
            producto.setDescripcion(productoInDto.getDescripcion());
            producto.setPrecio(productoInDto.getPrecio());

            producto.setFechaActualizacion(LocalDate.now());

            producto.setActivo(productoInDto.isActivo());
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


    // --- PUT / modificar
    public ProductoOutDto modificar(long id, ProductoUpdateDto productoUpdateDto) throws ProductoNoEncontradoException, CategoriaNoEncontradaException {
        Producto productoAnterior = productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);

        Categoria categoria = categoriaRepository.findById(productoUpdateDto.getCategoriaId())
                .orElseThrow(CategoriaNoEncontradaException::new);


        productoAnterior.setNombre(productoUpdateDto.getNombre());
        productoAnterior.setDescripcion(productoUpdateDto.getDescripcion());
        productoAnterior.setPrecio(productoUpdateDto.getPrecio());
//        productoAnterior.setFechaActualizacion(productoUpdateDto.getFechaActualizacion());
        productoAnterior.setActivo(productoUpdateDto.isActivo());
        productoAnterior.setCategoria(categoria);
        // El campo UsuarioId no quiero que se pueda modificar

        Producto actualizado = productoRepository.save(productoAnterior);
        return mapToOutDto(actualizado);
    }

    // --- DELETE
    public void eliminar(long id) throws ProductoNoEncontradoException {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);
        productoRepository.delete(producto);
    }


    // --- Metodo auxiliar privado para mapear y no repetir código
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
                categoriaSimple,
                usuarioSimple
        );

    }
}

