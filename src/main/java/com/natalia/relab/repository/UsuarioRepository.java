package com.natalia.relab.repository;

import com.natalia.relab.model.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsuarioRepository extends CrudRepository<Usuario,Long> {

    List<Usuario> findAll();
}
