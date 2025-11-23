package com.natalia.relab;

import com.natalia.relab.dto.CategoriaSimpleDto;
import com.natalia.relab.dto.ProductoInDto;
import com.natalia.relab.dto.ProductoOutDto;
import com.natalia.relab.dto.UsuarioSimpleDto;
import com.natalia.relab.model.Categoria;
import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.CategoriaRepository;
import com.natalia.relab.repository.ProductoRepository;
import com.natalia.relab.repository.UsuarioRepository;
import com.natalia.relab.service.ProductoService;
import exception.CategoriaNoEncontradaException;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
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
    public void testAgregarConImagen_UsuarioNoExiste() { // No hay stubbing de ModelMapper porque la excepción ocurre antes de mapear el DTO a entidad
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

    // ---------------------------------------------------------
    //           TEST GET by ID - buscarPorId()
    // ---------------------------------------------------------
    @Test
    public void testBuscarPorId_Exito() throws ProductoNoEncontradoException {
        // ID del producto a buscar
        long idBuscado = 100L;

        // Producto que simula estar en la BBDD
        Producto productoEnBBDD = new Producto();
        productoEnBBDD.setId(idBuscado);
        productoEnBBDD.setNombre("Producto1");
        productoEnBBDD.setDescripcion("Descripción del producto 1");
        productoEnBBDD.setPrecio(100.0f);
        productoEnBBDD.setActivo(true);
        productoEnBBDD.setModo(false);

        // ProductoOutDto esperado
        ProductoOutDto outDto = new ProductoOutDto();
        outDto.setId(idBuscado);
        outDto.setNombre("Producto1");

        // Defino el comportamiento de los mocks
        when(productoRepository.findById(idBuscado)).thenReturn(Optional.of(productoEnBBDD));

        // No necesito stub de modelMapper porque mapToOutDto es metodo privado manual
        // El mapeo se realiza dentro del service

        // Llamo al metodo a testear
        ProductoOutDto resultado = productoService.buscarPorId(idBuscado);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(idBuscado, resultado.getId());
        assertEquals("Producto1", resultado.getNombre());
    }

    @Test
    public void testBuscarPorId_FallaSiNoExiste() {
        long idInexistente = 999L;

        // Mockeo el repositorio para que devuelva vacío
        when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Verifico que se lanza la excepción
        assertThrows(ProductoNoEncontradoException.class,
                () -> productoService.buscarPorId(idInexistente));
    }

    // ---------------------------------------------------------
    //   TEST GET Todos o Aplicar Filtros - listarConFiltros()
    // ---------------------------------------------------------
    @Test // Solo caso de exito porque si no se encuentra se devuelve lista vacía
    public void testListarConFiltradoPorNombre() throws UsuarioNoEncontradoException, CategoriaNoEncontradaException {
        String nombre = "Prod";

        // Productos simulados en la BBDD
        Producto p1 = new Producto();
        p1.setId(1L);
        p1.setNombre("Producto1");

        Producto p2 = new Producto();
        p2.setId(2L);
        p2.setNombre("Producto2");

        when(productoRepository.findByNombreContainingIgnoreCase(nombre))
                .thenReturn(List.of(p1, p2));

        List<ProductoOutDto> resultado = productoService.listarConFiltrado(nombre, null, null, null);

        assertEquals(2, resultado.size()); // Verifico que se devuelven 2 productos
        assertEquals(1L, resultado.get(0).getId()); // Verifico que el primer producto es el esperado
        assertEquals(2L, resultado.get(1).getId()); // Verifico que el segundo producto es el esperado
    }


    //@Test // FIXME
//    public void testListarConFiltradoPorActivo() throws UsuarioNoEncontradoException, CategoriaNoEncontradaException {
//        // Creamos un Producto de ejemplo
//        Producto p1 = new Producto();
//        p1.setId(1L);
//        p1.setNombre("Producto1");
//        p1.setDescripcion("Descripción 1");
//        p1.setPrecio(100.0f); // IMPORTANTE: evitar NullPointerException
//        p1.setActivo(true);
//        p1.setModo(false);
//
//        // Categoria y Usuario asociados
//        Categoria c1 = new Categoria();
//        c1.setId(1L);
//        c1.setNombre("Categoria1");
//        p1.setCategoria(c1);
//
//        Usuario u1 = new Usuario();
//        u1.setId(10L);
//        u1.setNickname("usuario10");
//        p1.setUsuario(u1);
//
//        // Mockeamos el repositorio para devolver solo productos activos
//        when(productoRepository.findByActivo(true)).thenReturn(List.of(p1));
//
//        // Creamos ProductoOutDto esperado
//        ProductoOutDto dto = new ProductoOutDto();
//        dto.setId(p1.getId());
//        dto.setNombre(p1.getNombre());
//        // Si mapToOutDto también llena CategoriaSimpleDto y UsuarioSimpleDto:
//        dto.setCategoria(new CategoriaSimpleDto(c1.getId(), c1.getNombre()));
//        dto.setUsuario(new UsuarioSimpleDto(u1.getId(), u1.getNickname()));
//
////        // Mockeamos el mapper
////        when(modelMapper.map(p1, ProductoOutDto.class)).thenReturn(dto);
//
//        // Ejecutamos el método
//        List<ProductoOutDto> resultado = productoService.listarConFiltrado(null, true, null, null);
//
//        // Verificaciones
//        assertNotNull(resultado);
//        assertEquals(1, resultado.size());
//        assertEquals("Producto1", resultado.getFirst().getNombre());
//        assertEquals(100.0f, resultado.getFirst().getPrecio()); // si mapToOutDto copia el precio
//        assertEquals("Categoria1", resultado.getFirst().getCategoria().getNombre());
//        assertEquals("usuario10", resultado.getFirst().getUsuario().getNickname());
//    }

}
