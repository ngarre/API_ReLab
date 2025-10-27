package com.natalia.relab.service;

import com.natalia.relab.dto.CategoriaSimpleDto;
import com.natalia.relab.dto.ProductoInDto;
import com.natalia.relab.dto.ProductoOutDto;
import com.natalia.relab.model.Categoria;
import com.natalia.relab.model.Producto;
import com.natalia.relab.repository.CategoriaRepository;
import com.natalia.relab.repository.ProductoRepository;
import exception.CategoriaNoEncontradaException;
import exception.ProductoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // --- POST
    public ProductoOutDto agregar(ProductoInDto productoInDto) throws CategoriaNoEncontradaException {
        // Busco categoria en la BBDD
        Categoria categoria = categoriaRepository.findById(productoInDto.getCategoriaId())
                .orElseThrow(CategoriaNoEncontradaException::new);

        // Creo producto
        Producto producto = new Producto();
        producto.setNombre(productoInDto.getNombre());
        producto.setDescripcion(productoInDto.getDescripcion());
        producto.setPrecio(productoInDto.getPrecio());
        producto.setFechaActualizacion(productoInDto.getFechaActualizacion());
        producto.setActivo(productoInDto.isActivo());
        producto.setCategoria(categoria);

        Producto guardado = productoRepository.save(producto);

        // Mapear a ProductoOutDto
        CategoriaSimpleDto categoriaSimple = new CategoriaSimpleDto(
                categoria.getId(), categoria.getNombre()
        );

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
    public ProductoOutDto modificar(long id, ProductoInDto productoInDto) throws ProductoNoEncontradoException, CategoriaNoEncontradaException {
        Producto productoAnterior = productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);

        Categoria categoria = categoriaRepository.findById(productoInDto.getCategoriaId())
                .orElseThrow(CategoriaNoEncontradaException::new);

        productoAnterior.setNombre(productoInDto.getNombre());
        productoAnterior.setDescripcion(productoInDto.getDescripcion());
        productoAnterior.setPrecio(productoInDto.getPrecio());
        productoAnterior.setFechaActualizacion(productoInDto.getFechaActualizacion());
        productoAnterior.setActivo(productoInDto.isActivo());

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

        return new ProductoOutDto(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getFechaActualizacion(),
                producto.isActivo(),
                categoriaSimple
        );

    }
}

