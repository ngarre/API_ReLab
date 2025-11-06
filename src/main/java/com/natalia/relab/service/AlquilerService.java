package com.natalia.relab.service;

import com.natalia.relab.dto.*;
import com.natalia.relab.model.Alquiler;
import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.AlquilerRepository;
import com.natalia.relab.repository.ProductoRepository;
import com.natalia.relab.repository.UsuarioRepository;
import exception.AlquilerNoEncontradoException;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlquilerService {

    @Autowired
    private AlquilerRepository alquilerRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // -- POST
    public AlquilerOutDto agregar(AlquilerInDto alquilerInDto) throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        // Busco el producto en la base de datos
        Producto producto = productoRepository.findById(alquilerInDto.getProductoId())
                .orElseThrow(ProductoNoEncontradoException::new);

        // Busco usuario arrendador en la base de datos
        Usuario arrendador = usuarioRepository.findById(alquilerInDto.getArrendadorId())
                .orElseThrow(UsuarioNoEncontradoException::new);

        // Busco usuario arrendatario en la base de datos
        Usuario arrendatario = usuarioRepository.findById(alquilerInDto.getArrendatarioId())
                .orElseThrow(UsuarioNoEncontradoException::new);

        // Creo registro de alquiler
        Alquiler alquiler = new Alquiler();
        alquiler.setProducto(producto);
        alquiler.setArrendador(arrendador);
        alquiler.setArrendatario(arrendatario);
        alquiler.setFechaInicio(alquilerInDto.getFechaInicio());
        alquiler.setFechaFin(alquilerInDto.getFechaFin());
        alquiler.setMeses(alquilerInDto.getMeses());
        alquiler.setPrecio(alquilerInDto.getPrecio());
        alquiler.setComentario(alquilerInDto.getComentario());
        alquiler.setCancelado(alquilerInDto.isCancelado());

        Alquiler guardado = alquilerRepository.save(alquiler);
        return mapToOutDto(guardado);
    }

    // -- GET todos
    public List<AlquilerOutDto> listarTodos() {
        return alquilerRepository.findAll()
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // --- GET por id
    public AlquilerOutDto buscarPorId(long id) throws AlquilerNoEncontradoException {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(AlquilerNoEncontradoException::new);
        return mapToOutDto(alquiler);
    }

    // --- GET con FILTRADO por ArrendadorId
    public List<AlquilerOutDto> buscarPorArrendadorId(Long arrendadorId) {
        return alquilerRepository.findByArrendadorId(arrendadorId)
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // --- GET con FILTRADO por ArrendatarioId
    public List<AlquilerOutDto> buscarPorArrendatarioId(Long arrendatarioId) {
        return alquilerRepository.findByArrendatarioId(arrendatarioId)
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // --- GET con FILTRADO por ProductoId
    public List<AlquilerOutDto> buscarPorProductoId(Long productoId) {
        return alquilerRepository.findByProductoId(productoId)
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }


    // --- PUT / modificar
    public AlquilerOutDto modificar(long id, AlquilerUpdateDto alquilerUpdateDto) throws AlquilerNoEncontradoException {
        Alquiler alquilerAnterior = alquilerRepository.findById(id)
                .orElseThrow(AlquilerNoEncontradoException::new);


        // Solo mapeo los campos que se pueden modificar
        alquilerAnterior.setPrecio(alquilerUpdateDto.getPrecio());
        alquilerAnterior.setComentario(alquilerUpdateDto.getComentario());
        alquilerAnterior.setCancelado(alquilerUpdateDto.isCancelado());

        Alquiler actualizado = alquilerRepository.save(alquilerAnterior);
        return mapToOutDto(actualizado);
    }

    // --- DELETE
    public void eliminar(long id) throws AlquilerNoEncontradoException {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(AlquilerNoEncontradoException::new);
        alquilerRepository.delete(alquiler);
    }


    // -- Metodo auxiliar privado para mapear y no repetir código
    private AlquilerOutDto mapToOutDto (Alquiler alquiler) {

        ProductoSimpleDto productoSimple = null;
        if (alquiler.getProducto() != null) {
            productoSimple = new ProductoSimpleDto(
                    alquiler.getProducto().getId(),
                    alquiler.getProducto().getNombre()
            );
        }

        UsuarioSimpleDto arrendadorSimple = null;
        if (alquiler.getArrendador() != null) {
            arrendadorSimple = new UsuarioSimpleDto(
                    alquiler.getArrendador().getId(),
                    alquiler.getArrendador().getNickname()
            );
        }

        UsuarioSimpleDto arrendatarioSimple = null;
        if (alquiler.getArrendatario() != null) {
            arrendatarioSimple = new UsuarioSimpleDto(
                   alquiler.getArrendatario().getId(),
                   alquiler.getArrendatario().getNickname()
            );
        }

        return new AlquilerOutDto(
                alquiler.getId(),
                alquiler.getFechaInicio(),
                alquiler.getFechaFin(),
                alquiler.getMeses(),
                alquiler.getPrecio(),
                alquiler.getComentario(),
                alquiler.isCancelado(),
                productoSimple,
                arrendadorSimple,
                arrendatarioSimple
        );
    }
}


