package com.natalia.relab.service;

import com.natalia.relab.dto.*;
import com.natalia.relab.model.Categoria;
import com.natalia.relab.repository.CategoriaRepository;
import exception.CategoriaNoEncontradaException;
import exception.NombreYaExisteException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoriaRepository categoriaRepository;


    // --- POST
    public CategoriaOutDto agregar(CategoriaInDto categoriaInDto) {

        // Validación de que el nombre no esté en uso
        if (categoriaRepository.existsByNombre(categoriaInDto.getNombre())) {
            throw new NombreYaExisteException();
        }

        // Creo categoria
        Categoria categoria = modelMapper.map(categoriaInDto, Categoria.class);
        // Fecha automática del sistema
        categoria.setFechaCreacion(LocalDate.now());

        // Guardar y devolver DTO
        Categoria guardada  = categoriaRepository.save(categoria);
        return mapToOutDto(guardada);
    }

    // --- GET por id
    public CategoriaOutDto buscarPorId(long id) throws CategoriaNoEncontradaException {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(CategoriaNoEncontradaException::new);
        return mapToOutDto(categoria);
    }

    // --- GET con FILTRADO dinámico
    public List<CategoriaOutDto> listarConFiltros(
            String nombre,
            Boolean activa,
            LocalDate fechaCreacion,
            LocalDate desde,
            LocalDate hasta) {

        // Filtrado por nombre --> Coincidencias parciales y sin distinguir mayúsculas y minúsculas
        if (nombre != null && !nombre.isEmpty()) {
            return categoriaRepository.findByNombreContainingIgnoreCase(nombre)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        // Filtrado por activa o no
        if (activa != null) {
            return categoriaRepository.findByActiva(activa)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        // Filtrado por fecha exacta o rango ("Desde-hasta" o "desde-hasta fecha actual")
        // 1. Filtrado por fecha EXACTA
        if (fechaCreacion != null) {
            return categoriaRepository.findByFechaCreacion(fechaCreacion)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }
        // 2. Filtrado por RANGO completo: desde + hasta
        if (desde != null && hasta != null) {
            return categoriaRepository.findByFechaCreacionBetween(desde, hasta)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }
        // 3. Filtrado por RANGO parcialmente abierto (solo desde)
        if (desde != null) {
            return categoriaRepository.findByFechaCreacionBetween(desde, LocalDate.now())
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        // Si no hay ningún filtro, listar todas
        return categoriaRepository.findAll()
                .stream()
                .map(this::mapToOutDto)
                .toList();
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

        // Mapeo automático sobre el objeto existente
        modelMapper.map(categoriaUpdateDto, categoriaAnterior);

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
        return modelMapper.map(categoria, CategoriaOutDto.class);
    }
}
