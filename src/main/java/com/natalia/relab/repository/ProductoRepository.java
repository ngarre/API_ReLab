package com.natalia.relab.repository;

import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends CrudRepository<Producto,Long> {
    List<Producto> findAll(); // Es de los metodos de la interfaz CrudRepository
    List<Producto> findByNombreContainingIgnoreCase(String nombre); // Para filtrar por coincidencia parcial del nombre sin tener en cuenta mayúsculas o minúsculas
    List<Producto> findByActivo(boolean activo);
    List<Producto> findByCategoriaId(Long categoriaId);
    List<Producto> findByUsuarioId(Long usuarioId);
}
