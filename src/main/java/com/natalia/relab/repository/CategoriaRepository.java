package com.natalia.relab.repository;

import com.natalia.relab.model.Categoria;
import com.natalia.relab.model.Producto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository  extends CrudRepository<Categoria,Long> {
    List<Categoria> findAll();
}
