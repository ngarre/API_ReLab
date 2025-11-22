package com.natalia.relab;

import com.natalia.relab.dto.ProductoInDto;
import com.natalia.relab.dto.ProductoOutDto;
import com.natalia.relab.model.Categoria;
import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.CategoriaRepository;
import com.natalia.relab.repository.ProductoRepository;
import com.natalia.relab.repository.UsuarioRepository;
import com.natalia.relab.service.ProductoService;
import exception.CategoriaNoEncontradaException;
import exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTests {

    @InjectMocks
    private ProductoService productoService;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    ModelMapper modelMapper;

    // ---------------------------------------------------------
    //           TEST POST - agregarConImagen()
    // ---------------------------------------------------------

    @Test
    public void testAgregarConImagen() throws CategoriaNoEncontradaException, UsuarioNoEncontradoException {
        ProductoInDto inDto = new ProductoInDto(
                "Producto1",
                "Descripción del producto 1",
                100.0f,
                true,
                false,
                1L,
                10L,
                new byte[]{1, 2, 3}
        );

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Categoria1");

        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setNickname("usuario10");

        // Mockeo producto mapeado por ModelMapper
        Producto productoMapeado = new Producto();

        Producto productoGuardado = new Producto();
        productoGuardado.setId(100L);
        productoGuardado.setNombre(inDto.getNombre());
        productoGuardado.setPrecio(inDto.getPrecio());
        productoGuardado.setCategoria(categoria);
        productoGuardado.setUsuario(usuario);

        // Stubs
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(modelMapper.map(inDto, Producto.class)).thenReturn(productoMapeado);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        // Ejecución
        ProductoOutDto resultado = productoService.agregarConImagen(inDto);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("Producto1", resultado.getNombre());
        assertEquals(100.0f, resultado.getPrecio());
        assertEquals("usuario10", resultado.getUsuario().getNickname());
        assertEquals("Categoria1", resultado.getCategoria().getNombre());
    }

    @Test
    public void testAgregarConImagen_UsuarioNoExiste() {
        // DTO de entrada con un usuario inexistente
        ProductoInDto inDto = new ProductoInDto(
                "Producto1",
                "Descripción del producto 1",
                100.0f,
                true,
                false,
                10L,    // categoría existente
                99L,    // usuario inexistente
                new byte[]{1, 2, 3}
        );

        // Mockeo la categoría para que exista
        Categoria categoria = new Categoria();
        categoria.setId(10L);
        categoria.setNombre("Categoria1");
        when(categoriaRepository.findById(10L)).thenReturn(Optional.of(categoria));

        // Mockeo el usuario como NO existente
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Ejecución y verificación: espero que se lance UsuarioNoEncontradoException
        assertThrows(UsuarioNoEncontradoException.class,
                () -> productoService.agregarConImagen(inDto));
    }



}
