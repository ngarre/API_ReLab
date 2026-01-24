package com.natalia.relab.repository;

import com.natalia.relab.model.Compraventa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompraventaRepository extends CrudRepository<Compraventa, Long> {
    List<Compraventa> findAll();

    // Metodo necesario para comprobar si el producto ya está vendido:
    boolean existsByProductoId(Long id);
}
