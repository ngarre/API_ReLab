package com.natalia.relab.repository;

import com.natalia.relab.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario,Long> {

    List<Usuario> findAll();
    Optional<Usuario> findByNickname(String nickname); // Se pone Optional porque puede existir un usuario con ese nickname o ninguno
    Optional<Usuario> findByNicknameAndPassword(String nickname, String password);
    List<Usuario> findByTipoUsuario(String tipoUsuario);
    List<Usuario> findByCuentaActiva(boolean cuentaActiva);
    boolean existsByNickname(String nickname); // Lanza consulta a la BBDD para comprabar si ya existe usuario con ese nickname
}
