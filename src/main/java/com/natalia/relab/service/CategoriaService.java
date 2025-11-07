package com.natalia.relab.service;

import com.natalia.relab.dto.*;
import com.natalia.relab.model.Categoria;
import com.natalia.relab.repository.CategoriaRepository;
import exception.CategoriaNoEncontradaException;
import exception.NicknameYaExisteException;
import exception.NombreYaExisteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;


    // --- POST
    public CategoriaOutDto agregar(CategoriaInDto categoriaInDto) {

        // Valido que el nombre no esté en uso
        if (categoriaRepository.existsByNombre(categoriaInDto.getNombre())) {
            throw new NombreYaExisteException();
        }

        // Creo categoria
        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaInDto.getNombre());
        categoria.setDescripcion(categoriaInDto.getDescripcion());

        categoria.setFechaCreacion(LocalDate.now());

        categoria.setActiva(categoriaInDto.isActiva());
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

    // --- GET con FILTRADO por nombre
    public List<CategoriaOutDto> buscarPorNombreParcial(String nombre)  {
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // --- GET con FILTRADO según si la categoría está activa o no
    public List<CategoriaOutDto> buscarActivas(boolean activa) {
        return categoriaRepository.findByActiva(activa)
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // -- GET con FILTRADO por fecha de creación EXACTA o RANGO
    public List<CategoriaOutDto> buscarPorFecha(LocalDate fechaCreacion, LocalDate desde, LocalDate hasta) {
        List<Categoria> lista;
        if (fechaCreacion != null) {
            lista = categoriaRepository.findByFechaCreacion(fechaCreacion);
        } else if (desde != null && hasta != null) {
            lista = categoriaRepository.findByFechaCreacionBetween(desde, hasta);
        } else if (desde != null){
            lista = categoriaRepository.findByFechaCreacionBetween(desde, LocalDate.now());
        } else {
            return listarTodas(); // Si no me dan parámetros de fecha listo todas las categorias.
        }
        return lista.stream().map(this::mapToOutDto).toList();
    }

    // --- PUT / modificar
    public CategoriaOutDto modificar(long id, CategoriaUpdateDto categoriaUpdateDto) throws CategoriaNoEncontradaException {
        Categoria categoriaAnterior = categoriaRepository.findById(id)
                .orElseThrow(CategoriaNoEncontradaException::new);

        // Verifico si el nombre de la categoría no esté en uso por OTRA categoría
        if (categoriaRepository.existsByNombre(categoriaUpdateDto.getNombre())
                && !categoriaAnterior.getNombre().equals(categoriaUpdateDto.getNombre())) {
            throw new NombreYaExisteException();
        }

        categoriaAnterior.setNombre(categoriaUpdateDto.getNombre());
        categoriaAnterior.setDescripcion(categoriaUpdateDto.getDescripcion());
        categoriaAnterior.setActiva(categoriaUpdateDto.isActiva());
        categoriaAnterior.setTasaComision(categoriaUpdateDto.getTasaComision());

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
                categoria.isActiva(),
                categoria.getTasaComision()
        );

    }
}
