package com.natalia.relab.repository;

import com.natalia.relab.model.Alquiler;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlquilerRepository extends CrudRepository<Alquiler, Long> {
    List<Alquiler> findAll();
}
