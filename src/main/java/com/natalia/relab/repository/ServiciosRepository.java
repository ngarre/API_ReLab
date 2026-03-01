package com.natalia.relab.repository;

import com.natalia.relab.model.Servicios;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ServiciosRepository extends CrudRepository<Servicios, Long> {
    List<Servicios> findAll();
    List<Servicios> findByIdUsuario(Long usuarioId);

    @Modifying
    @Transactional
    @Query("DELETE FROM servicios s WHERE s.idUsuario = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
}