package com.natalia.relab.service;

import com.natalia.relab.dto.*;
import com.natalia.relab.model.Categoria;
import com.natalia.relab.repository.CategoriaRepository;
import exception.CategoriaNoEncontradaException;
import exception.NombreYaExisteException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoriaRepository categoriaRepository;


    // --- POST
    public CategoriaOutDto agregar(CategoriaInDto categoriaInDto) {
        log.info("Intentando crear nueva categoría");
        log.debug("Datos recibidos: {}", categoriaInDto);

        // Validación de que el nombre no esté en uso
        if (categoriaRepository.existsByNombre(categoriaInDto.getNombre())) {
            log.warn("El nombre '{}' ya existe. Lanzando excepción.", categoriaInDto.getNombre());
            throw new NombreYaExisteException();
        }

        // Creo categoria
        Categoria categoria = modelMapper.map(categoriaInDto, Categoria.class);
        // Fecha automática del sistema
        categoria.setFechaCreacion(LocalDate.now());

        // Guardar y devolver DTO
        Categoria guardada  = categoriaRepository.save(categoria);

        log.info("Categoría creada con ID {}", guardada.getId());
        return mapToOutDto(guardada);
    }

    // --- GET por id
    public CategoriaOutDto buscarPorId(long id) throws CategoriaNoEncontradaException {
        log.info("Buscando categoría por ID {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Categoría {} no encontrada", id);
                    return new CategoriaNoEncontradaException();
                });

        return mapToOutDto(categoria);
    }

    // --- GET con FILTRADO dinámico
    public List<CategoriaOutDto> listarConFiltros(
            String nombre,
            Boolean activa,
            LocalDate fechaCreacion,
            LocalDate desde,
            LocalDate hasta) {

        log.info("Listando categorías con filtros - nombre: {}, activa: {}, fechaCreacion: {}, desde: {}, hasta: {}",
                nombre, activa, fechaCreacion, desde, hasta);

        // Parto de todas las categorías
        List<Categoria> categorias = categoriaRepository.findAll();

        // Filtrado por nombre (parcial e ignore case)
        if (nombre != null && !nombre.trim().isEmpty()) {
            String nombreFiltrado = nombre.trim().toLowerCase();
            categorias = categorias.stream()
                    .filter(categoria -> categoria.getNombre() != null &&
                            categoria.getNombre().toLowerCase().contains(nombreFiltrado))
                    .toList();
        }

        // Filtrado por activa (true/false)
        if (activa != null) {
            categorias = categorias.stream()
                    .filter(categoria -> categoria.isActiva() == activa)
                    .toList();
        }

        // Filtrado por fecha exacta o rango ("Desde-hasta" o "desde-hasta fecha actual")
        // 1. Filtrado por fecha EXACTA
        if (fechaCreacion != null) {
            categorias = categorias.stream()
                    .filter(categoria -> categoria.getFechaCreacion() != null &&
                            categoria.getFechaCreacion().isEqual(fechaCreacion))
                    .toList();
        }

        // 2. Filtrado por RANGO completo (desde + hasta)
        if (desde != null && hasta != null) {
            categorias = categorias.stream()
                    .filter(categoria -> categoria.getFechaCreacion() != null &&
                            !categoria.getFechaCreacion().isBefore(desde) &&
                            !categoria.getFechaCreacion().isAfter(hasta))
                    .toList();
        } else if (desde != null) { // rango abierto (desde hasta hoy)
            LocalDate hoy = LocalDate.now();
            categorias = categorias.stream()
                    .filter(categoria -> categoria.getFechaCreacion() != null &&
                            !categoria.getFechaCreacion().isBefore(desde) &&
                            !categoria.getFechaCreacion().isAfter(hoy))
                    .toList();
        }

        // Filtrado por rango parcialmente abierto (desde inicio hasta esa fecha)
        else if (hasta != null) {
            categorias = categorias.stream()
                    .filter(c -> c.getFechaCreacion() != null &&
                            !c.getFechaCreacion().isAfter(hasta))
                    .toList();
        }

        // Mapear a DTOs y devolver
        return categorias.stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // --- PUT / modificar
    public CategoriaOutDto modificar(long id, CategoriaUpdateDto categoriaUpdateDto) throws CategoriaNoEncontradaException {

        log.info("Intentando modificar categoría con ID {}", id);

        Categoria categoriaAnterior = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Categoría {} no encontrada", id);
                    return new CategoriaNoEncontradaException();
                });


        // Verifico si el nombre de la categoría no esté en uso por OTRA categoría
        if (categoriaRepository.existsByNombre(categoriaUpdateDto.getNombre())
                && !categoriaAnterior.getNombre().equals(categoriaUpdateDto.getNombre())) {
            log.warn("El nombre '{}' ya existe. No se puede actualizar", categoriaUpdateDto.getNombre());
            throw new NombreYaExisteException();
        }

        // Mapeo automático sobre el objeto existente
        modelMapper.map(categoriaUpdateDto, categoriaAnterior);

        Categoria actualizada = categoriaRepository.save(categoriaAnterior);
        log.info("Categoría {} actualizada correctamente", id);
        return mapToOutDto(actualizada);
    }

    // --- DELETE
    public void eliminar(long id) throws CategoriaNoEncontradaException {
        log.warn("Intentando eliminar categoría {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Categoría {} no encontrada", id);
                    return new CategoriaNoEncontradaException();
                });
        categoriaRepository.delete(categoria);
        log.info("Categoría {} eliminada correctamente", id);
    }

    // --- Metodo auxiliar privado para mapear y no repetir código
    private CategoriaOutDto mapToOutDto(Categoria categoria) {
        return modelMapper.map(categoria, CategoriaOutDto.class);
    }
}
