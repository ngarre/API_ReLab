package com.natalia.relab.repository;

import com.natalia.relab.model.Reviews;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReviewsRepository extends CrudRepository<Reviews, Long> {

    // Recuperar todas las reviews de un usuario
    List<Reviews> findByIdUsuario(Long idUsuario);

    @Modifying
    @Transactional
    @Query("DELETE FROM reviews r WHERE r.idUsuario = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);


}