package com.natalia.relab.repository;

import com.natalia.relab.model.Alquiler;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AlquilerRepository extends CrudRepository<Alquiler, Long> {
    List<Alquiler> findAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM alquileres a WHERE a.arrendatario.id = :usuarioId")
    void deleteByArrendatarioId(@Param("usuarioId") Long usuarioId);
}
