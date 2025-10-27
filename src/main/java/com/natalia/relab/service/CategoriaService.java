package com.natalia.relab.service;

import com.natalia.relab.dto.CategoriaInDto;
import com.natalia.relab.dto.CategoriaOutDto;
import com.natalia.relab.dto.CategoriaSimpleDto;
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
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;


    // --- POST
    public CategoriaOutDto agregar(CategoriaInDto categoriaInDto) {

        // Creo categoria
        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaInDto.getNombre());
        categoria.setDescripcion(categoriaInDto.getDescripcion());
        categoria.setFechaCreacion(categoriaInDto.getFechaCreacion());
        categoria.setActivo(categoriaInDto.isActivo());
        categoria.setTasaComision(categoriaInDto.getTasaComision());

        Categoria guardada  = categoriaRepository.save(categoria);

        return mapToOutDto(guardada);
    }

    // --- GET todos
    public List<CategoriaOutDto> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // --- GET por id
    public CategoriaOutDto buscarPorId(long id) throws CategoriaNoEncontradaException {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(CategoriaNoEncontradaException::new);
        return mapToOutDto(categoria);
    }

    // --- PUT / modificar
    public CategoriaOutDto modificar(long id, CategoriaInDto categoriaInDto) throws CategoriaNoEncontradaException {
        Categoria categoriaAnterior = categoriaRepository.findById(id)
                .orElseThrow(CategoriaNoEncontradaException::new);

        categoriaAnterior.setNombre(categoriaInDto.getNombre());
        categoriaAnterior.setDescripcion(categoriaInDto.getDescripcion());
        categoriaAnterior.setFechaCreacion(categoriaInDto.getFechaCreacion());
        categoriaAnterior.setActivo(categoriaInDto.isActivo());
        categoriaAnterior.setTasaComision(categoriaInDto.getTasaComision());

        Categoria actualizada = categoriaRepository.save(categoriaAnterior);
        return mapToOutDto(actualizada);
    }

    // --- DELETE
    public void eliminar(long id) throws CategoriaNoEncontradaException {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(CategoriaNoEncontradaException::new);
        categoriaRepository.delete(categoria);
    }


    // --- Metodo auxiliar privado para mapear y no repetir código
    private CategoriaOutDto mapToOutDto(Categoria categoria) {

        return new CategoriaOutDto(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getFechaCreacion(),
                categoria.isActivo(),
                categoria.getTasaComision()
        );

    }
}
