package com.natalia.relab.repository;

import com.natalia.relab.model.Categoria;
import com.natalia.relab.model.Producto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository  extends CrudRepository<Categoria,Long> {
    List<Categoria> findAll();
    boolean existsByNombre(String nombre);
    boolean existsById(@NonNull Long categoriaId); // Para ver si existe ese ID de categoria antes de listar productos de esa categoría
}
