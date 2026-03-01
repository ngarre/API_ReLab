package com.natalia.relab.repository;

import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends CrudRepository<Producto,Long> {
    List<Producto> findAll(); // Es de los metodos de la interfaz CrudRepository
    boolean existsById(@NonNull Long productoId); // Para ver si existe ese ID de ese producto antes de listar alquileres de ese producto
    List<Producto> findByUsuarioId(Long usuarioId);

    @Modifying
    @Transactional
    @Query("DELETE FROM productos p WHERE p.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);

}
