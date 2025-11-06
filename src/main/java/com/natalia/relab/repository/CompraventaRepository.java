package com.natalia.relab.repository;

import com.natalia.relab.model.Compraventa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompraventaRepository extends CrudRepository<Compraventa, Long> {
    List<Compraventa> findAll();
    List<Compraventa> findByCompradorId(Long id);
    List<Compraventa> findByVendedorId(Long id);
    Optional<Compraventa> findByProductoId(Long id); // Un mismo producto solo puede estar implicado en una transacción de compraventa
}
