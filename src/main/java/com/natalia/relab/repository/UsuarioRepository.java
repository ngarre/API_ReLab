package com.natalia.relab.repository;

import com.natalia.relab.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario,Long> {

    List<Usuario> findAll();
    Optional<Usuario> findByNickname(String nickname);
    Optional<Usuario> findByNicknameAndPassword(String nickname, String password);
}
