package com.natalia.relab.repository;

import com.natalia.relab.model.Compraventa;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompraventaRepository extends CrudRepository<Compraventa, Long> {
    List<Compraventa> findAll();

    // Metodo necesario para comprobar si el producto ya está vendido:
    boolean existsByProductoId(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM compraventas c WHERE c.comprador.id = :usuarioId")
    void deleteByCompradorId(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Transactional
    @Query("DELETE FROM compraventas c WHERE c.producto.id = :productoId")
    void deleteByProductoId(@Param("productoId") Long productoId);
}
