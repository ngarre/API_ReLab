package com.natalia.relab.service;

import com.natalia.relab.dto.*;
import com.natalia.relab.model.Compraventa;
import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.CategoriaRepository;
import com.natalia.relab.repository.CompraventaRepository;
import com.natalia.relab.repository.ProductoRepository;
import com.natalia.relab.repository.UsuarioRepository;
import exception.CompraventaNoEncontradaException;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompraventaService {

    @Autowired
    private CompraventaRepository compraventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // -- POST
    public CompraventaOutDto agregar(CompraventaInDto compraventaInDto) throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        // Busco el producto en la base de datos
        Producto producto = productoRepository.findById(compraventaInDto.getProductoId())
                .orElseThrow(ProductoNoEncontradoException::new);

        // Busco usuario comprador en la base de datos
        Usuario comprador = usuarioRepository.findById(compraventaInDto.getCompradorId())
                .orElseThrow(UsuarioNoEncontradoException::new);

        // Busco usuario vendedor en la base de datos
        Usuario vendedor = usuarioRepository.findById(compraventaInDto.getVendedorId())
                .orElseThrow(UsuarioNoEncontradoException::new);

        // Creo registro de compraventa
        Compraventa compraventa = new Compraventa();
        compraventa.setProducto(producto);
        compraventa.setComprador(comprador);
        compraventa.setVendedor(vendedor);
        compraventa.setDevuelto(compraventaInDto.isDevuelto());
        compraventa.setComentario(compraventaInDto.getComentario());
        compraventa.setPrecioFinal(compraventaInDto.getPrecioFinal());
        compraventa.setFecha(java.time.LocalDate.now()); // Fecha sistema

        Compraventa guardada = compraventaRepository.save(compraventa);
        return mapToOutDto(guardada);
    }

    // --- GET todas
    public List<CompraventaOutDto> listarTodas() {
        return compraventaRepository.findAll()
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }


    // --- GET por id
    public CompraventaOutDto buscarPorId(long id) throws CompraventaNoEncontradaException {
        Compraventa compraventa = compraventaRepository.findById(id)
                .orElseThrow(CompraventaNoEncontradaException::new);
        return mapToOutDto(compraventa);
    }


    // --- PUT / modificar
    public CompraventaOutDto modificar(long id, CompraventaUpdateDto compraventaUpdateDto) throws CompraventaNoEncontradaException {
        Compraventa compraventaAnterior = compraventaRepository.findById(id)
                .orElseThrow(CompraventaNoEncontradaException::new);


        // Solo mapeo los campos que se pueden modificar
        compraventaAnterior.setDevuelto(compraventaUpdateDto.isDevuelto());
        compraventaAnterior.setComentario(compraventaUpdateDto.getComentario());
        compraventaAnterior.setPrecioFinal(compraventaUpdateDto.getPrecioFinal());

        // Los campos comprador, vendedor y fecha no los dejo editables.  No le veo el sentido a modificarlos.

        Compraventa actualizada = compraventaRepository.save(compraventaAnterior);
        return mapToOutDto(actualizada);

    }

    // --- DELETE
    public void eliminar(long id) throws CompraventaNoEncontradaException {
        Compraventa compraventa = compraventaRepository.findById(id)
                .orElseThrow(CompraventaNoEncontradaException::new);
        compraventaRepository.delete(compraventa);
    }

    // -- Metodo auxiliar privado para mapear y no repetir código
    private CompraventaOutDto mapToOutDto (Compraventa compraventa) {

        ProductoSimpleDto productoSimple = null;
        if (compraventa.getProducto() != null) {
            productoSimple = new ProductoSimpleDto(
                    compraventa.getProducto().getId(),
                    compraventa.getProducto().getNombre()
            );
        }

        UsuarioSimpleDto compradorSimple = null;
        if (compraventa.getComprador() != null) {
            compradorSimple = new UsuarioSimpleDto(
                    compraventa.getComprador().getId(),
                    compraventa.getComprador().getNickname()
            );
        }

        UsuarioSimpleDto vendedorSimple = null;
        if (compraventa.getVendedor() != null) {
            vendedorSimple = new UsuarioSimpleDto(
                    compraventa.getVendedor().getId(),
                    compraventa.getVendedor().getNickname()
            );
        }

        return new CompraventaOutDto(
                compraventa.getId(),
                compraventa.getFecha(),
                compraventa.getPrecioFinal(),
                compraventa.getComentario(),
                compraventa.isDevuelto(),
                productoSimple,
                compradorSimple,
                vendedorSimple
        );
    }
}
