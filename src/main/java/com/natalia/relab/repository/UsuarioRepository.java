package com.natalia.relab.repository;

import com.natalia.relab.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario,Long> {

    List<Usuario> findAll();
    Optional<Usuario> findByNicknameAndPassword(String nickname, String password);
    boolean existsByNickname(String nickname); // Lanza consulta a la BBDD para comprabar si ya existe usuario con ese nickname
    boolean existsById(@NonNull Long usuarioId); // Para ver si existe ese ID de ese usuario antes de listar productos que pertenecen a ese usuario
}
