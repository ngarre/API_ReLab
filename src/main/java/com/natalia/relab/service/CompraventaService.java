package com.natalia.relab.service;

import com.natalia.relab.dto.*;
import com.natalia.relab.model.Compraventa;
import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.CompraventaRepository;
import com.natalia.relab.repository.ProductoRepository;
import com.natalia.relab.repository.UsuarioRepository;
import exception.CompraventaNoEncontradaException;
import exception.ProductoNoEncontradoException;
import exception.ProductoYaVendidoException;
import exception.UsuarioNoEncontradoException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompraventaService {

    private static final Logger log = LoggerFactory.getLogger(CompraventaService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CompraventaRepository compraventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // -- POST
    public CompraventaOutDto agregar(CompraventaInDto compraventaInDto) throws UsuarioNoEncontradoException, ProductoNoEncontradoException, ProductoYaVendidoException {
        log.info("Agregando compraventa de producto id {} entre comprador id {} y vendedor id {}",
                compraventaInDto.getProductoId(),
                compraventaInDto.getCompradorId(),
                compraventaInDto.getVendedorId()
        );

        // Busco el producto en la base de datos
        Producto producto = productoRepository.findById(compraventaInDto.getProductoId())
                .orElseThrow(() -> {
                    log.error("Producto no encontrado con ID {}", compraventaInDto.getProductoId());
                    return new ProductoNoEncontradoException();
                });

        // Compruebo si el producto ya ha sido vendido
        if (compraventaRepository.existsByProductoId(producto.getId())) {
            // Pongo warn y no log porque no es un error del sistema, sino situación de negocio esperada
            log.warn("Producto {} ya ha sido vendido. Operación cancelada", producto.getId());
            throw new ProductoYaVendidoException();
        }

        // Busco usuario comprador en la base de datos
        Usuario comprador = usuarioRepository.findById(compraventaInDto.getCompradorId())
                .orElseThrow(() -> {
                    log.error("Comprador no encontrado con ID {}", compraventaInDto.getCompradorId());
                    return new UsuarioNoEncontradoException();
                });

        // Busco usuario vendedor en la base de datos
        Usuario vendedor = usuarioRepository.findById(compraventaInDto.getVendedorId())
                .orElseThrow(() -> {
                    log.error("Vendedor no encontrado con ID {}", compraventaInDto.getVendedorId());
                    return new UsuarioNoEncontradoException();
                });

        log.debug("Producto, comprador y vendedor validados correctamente."); // Marco que todas las validaciones han pasado con éxito en caso de querer depurar.

        // Creo registro de compraventa
        // 1. Mapeo datos simples con ModelMapper
        Compraventa compraventa = modelMapper.map(compraventaInDto, Compraventa.class);

        // 2. Campos adicionales que no pueden llegar en forma de objeto con el DTO y fecha del sistema.
        compraventa.setProducto(producto);
        compraventa.setComprador(comprador);
        compraventa.setVendedor(vendedor);
        compraventa.setFecha(java.time.LocalDate.now()); // Fecha sistema

        Compraventa guardada = compraventaRepository.save(compraventa);

        log.info("Compraventa creada correctamente. ID={}", guardada.getId());
        return mapToOutDto(guardada);
    }

    // --- GET por id
    public CompraventaOutDto buscarPorId(long id) throws CompraventaNoEncontradaException {

        log.info("Servicio: Buscar compraventa por ID {}", id);

        Compraventa compraventa = compraventaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Compraventa no encontrada con ID {}", id);
                    return new CompraventaNoEncontradaException();
                });

        return mapToOutDto(compraventa);
    }

    // --- GET con FILTRADO dinámico
    public List <CompraventaOutDto> listarConFiltros(
            Long compradorId,
            Long vendedorId,
            Long productoId) throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        log.info("Servicio: Listar compraventas con filtros -> compradorId={}, vendedorId={}, productoId={}",
                compradorId, vendedorId, productoId);

        // Filtrado por compradorId
        if (compradorId != null) {
            // Se verifica que el usuario comprador exista
            boolean existe = usuarioRepository.existsById(compradorId);
            if (!existe) {
                throw new UsuarioNoEncontradoException();
            }

            return compraventaRepository.findByCompradorId(compradorId)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        // Filtrado por vendedorId
        if (vendedorId != null) {
            // Se verifica que el usuario vendedor exista
            boolean existe = usuarioRepository.existsById(vendedorId);
            if (!existe) {
                throw new UsuarioNoEncontradoException();
            }

            return compraventaRepository.findByVendedorId(vendedorId)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        // Filtrado por productoid
        if (productoId != null) {
            // Se verifica que el producto exista
            boolean existe = productoRepository.existsById(productoId);
            if (!existe) {
                throw new ProductoNoEncontradoException();
            }

            return compraventaRepository.findByProductoId(productoId)
                    .stream()
                    .map(this::mapToOutDto)
                    .toList();
        }

        // Sin filtros → todas las compraventas
        return compraventaRepository.findAll()
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }


    // --- PUT / modificar
    public CompraventaOutDto modificar(long id, CompraventaUpdateDto compraventaUpdateDto) throws CompraventaNoEncontradaException {
        Compraventa compraventaAnterior = compraventaRepository.findById(id)
                .orElseThrow(CompraventaNoEncontradaException::new);

        log.info("Servicio: Modificar compraventa por ID {}", id);

        // Solo mapeo los campos que se pueden modificar
        modelMapper.map(compraventaUpdateDto, compraventaAnterior);

        // Los campos comprador, vendedor y fecha no los dejo editables.  No le veo el sentido a modificarlos.

        Compraventa actualizada = compraventaRepository.save(compraventaAnterior);

        log.info("Compraventa {} actualizada correctamente", id);
        return mapToOutDto(actualizada);

    }

    public void eliminar(long id) throws CompraventaNoEncontradaException {
        log.warn("Servicio: Eliminar compraventa {}", id);
        Compraventa compraventa = compraventaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Compraventa {} no encontrada para eliminar", id);
                    return new CompraventaNoEncontradaException();
                });
        compraventaRepository.delete(compraventa);
        log.info("Compraventa {} eliminada correctamente", id);
    }

    // -- Metodo auxiliar privado para mapear y no repetir código

    // Utilizo un mapeo manual aquí en lugar de ModelMapper porque CompraventaOutDto
    // contiene campos anidados (ProductoSimpleDto y UsuarioSimpleDto)
    // que no existen en la entidad Compraventa. ModelMapper no puede inferir correctamente estos DTOs anidados,
    // por lo que el mapeo manual es más claro y seguro.

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
