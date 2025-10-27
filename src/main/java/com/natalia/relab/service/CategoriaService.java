package com.natalia.relab.service;

import com.natalia.relab.model.Categoria;
import com.natalia.relab.model.Producto;
import com.natalia.relab.repository.CategoriaRepository;
import com.natalia.relab.repository.ProductoRepository;
import exception.CategoriaNoEncontradaException;
import exception.ProductoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;

    public Categoria agregar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listarTodas() {
        List<Categoria> todasCategorias = categoriaRepository.findAll();
        return todasCategorias;
    }

    public Categoria buscarPorId(long id) throws CategoriaNoEncontradaException {
        return categoriaRepository.findById(id)
                .orElseThrow(CategoriaNoEncontradaException::new);
    }

    public Categoria modificar(long id, Categoria categoria) throws CategoriaNoEncontradaException {
        Categoria categoriaAnterior = categoriaRepository.findById(id)
                .orElseThrow(CategoriaNoEncontradaException::new);

        categoriaAnterior.setNombre(categoria.getNombre());
        categoriaAnterior.setDescripcion(categoria.getDescripcion());
        categoriaAnterior.setFechaCreacion(categoria.getFechaCreacion());
        categoriaAnterior.setActivo(categoria.isActivo());
        categoriaAnterior.setTasaComision(categoria.getTasaComision());

        return categoriaRepository.save(categoriaAnterior);
    }

    public void eliminar(long id) throws CategoriaNoEncontradaException {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(CategoriaNoEncontradaException::new);
        categoriaRepository.delete(categoria);
    }
}
