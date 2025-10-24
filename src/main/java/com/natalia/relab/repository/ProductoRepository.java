package com.natalia.relab.repository;

import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends CrudRepository<Producto,Long> {
    List<Producto> findAll(); // Es de los metodos de la interfaz CrudRepository
}
