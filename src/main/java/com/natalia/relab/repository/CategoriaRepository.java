package com.natalia.relab.repository;

import com.natalia.relab.model.Categoria;
import com.natalia.relab.model.Producto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository  extends CrudRepository<Categoria,Long> {
    List<Categoria> findAll();
    Optional<Categoria> findByNombreContainingIgnoreCase(String nombre); // Para filtrar por coincidencia parcial del nombre sin tener en cuenta mayúsculas o minúsculas
    List<Categoria> findByActiva(boolean activa);
    List<Categoria> findByFechaCreacion(LocalDate fechaCreacion); // Permite filtrar por una fecha exacta
    List<Categoria> findByFechaCreacionBetween(LocalDate desde, LocalDate hasta); // Permite filtrar por rango de fecha
}
