package com.natalia.relab;

import com.natalia.relab.dto.*;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    //-- FILTRADO por NOMBRE --//
    @Test
    public void testListarConFiltradoPorNombre() throws UsuarioNoEncontradoException, CategoriaNoEncontradaException {
        String nombre = "Prod";

        // Productos simulados en la BBDD
        Producto p1 = new Producto();
        p1.setId(1L);
        p1.setNombre("Producto1");
        p1.setPrecio(100.0f);


        Producto p2 = new Producto();
        p2.setId(2L);
        p2.setNombre("Producto2");
        p2.setPrecio(100.0f);

        when(productoRepository.findByNombreContainingIgnoreCase(nombre))
                .thenReturn(List.of(p1, p2));

        List<ProductoOutDto> resultado = productoService.listarConFiltrado(nombre, null, null, null);

        assertEquals(2, resultado.size()); // Verifico que se devuelven 2 productos
        assertEquals(1L, resultado.get(0).getId()); // Verifico que el primer producto es el esperado
        assertEquals(2L, resultado.get(1).getId()); // Verifico que el segundo producto es el esperado
    }

    //-- FILTRADO por ACTIVO --//
    @Test
    public void testListarConFiltradoPorActivo() throws UsuarioNoEncontradoException, CategoriaNoEncontradaException {
        // Creamos un Producto de ejemplo
        Producto p1 = new Producto();
        p1.setId(1L);
        p1.setNombre("Producto1");
        p1.setDescripcion("Descripción 1");
        p1.setPrecio(100.0f); // IMPORTANTE: evitar NullPointerException
        p1.setActivo(true);
        p1.setModo(false);

        // Categoria y Usuario asociados
        Categoria c1 = new Categoria();
        c1.setId(1L);
        c1.setNombre("Categoria1");
        p1.setCategoria(c1);

        Usuario u1 = new Usuario();
        u1.setId(10L);
        u1.setNickname("usuario10");
        p1.setUsuario(u1);

        // Mockeamos el repositorio para devolver solo productos activos
        when(productoRepository.findByActivo(true)).thenReturn(List.of(p1));

        // Creamos ProductoOutDto esperado
        ProductoOutDto dto = new ProductoOutDto();
        dto.setId(p1.getId());
        dto.setNombre(p1.getNombre());
        // Si mapToOutDto también llena CategoriaSimpleDto y UsuarioSimpleDto:
        dto.setCategoria(new CategoriaSimpleDto(c1.getId(), c1.getNombre()));
        dto.setUsuario(new UsuarioSimpleDto(u1.getId(), u1.getNickname()));

//        // Mockeamos el mapper
//        when(modelMapper.map(p1, ProductoOutDto.class)).thenReturn(dto);

        // Ejecutamos el metodo
        List<ProductoOutDto> resultado = productoService.listarConFiltrado(null, true, null, null);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Producto1", resultado.getFirst().getNombre());
        assertEquals(100.0f, resultado.getFirst().getPrecio()); // si mapToOutDto copia el precio
        assertEquals("Categoria1", resultado.getFirst().getCategoria().getNombre());
        assertEquals("usuario10", resultado.getFirst().getUsuario().getNickname());
    }

    //-- FILTRADO por CategoriaId --//
    @Test
    public void testListarConFiltradoPorCategoria_Exito() throws CategoriaNoEncontradaException, UsuarioNoEncontradoException {
        // Datos de prueba
        Long categoriaId = 1L;

        // Categoria simulada en la BBDD
        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);
        categoria.setNombre("Categoria1");

        // Producto simulados en la BBDD
        Producto p1 = new Producto();
        p1.setId(1L);
        p1.setNombre("Producto1");
        p1.setPrecio(100.0f);
        p1.setCategoria(categoria);

        when(categoriaRepository.existsById(categoriaId)).thenReturn(true); // Comprobar existencia de categoria
        when(productoRepository.findByCategoriaId(categoriaId)).thenReturn(List.of(p1)); // Devolver productos por categoria

        // Ejecución
        List<ProductoOutDto> resultado = productoService.listarConFiltrado(null, null, categoriaId, null);

        // Verificaciones
        assertNotNull(resultado); // Verifico que el resultado no es nulo
        assertEquals(1, resultado.size()); // Verifico que se devuelve 1 producto
        assertEquals("Producto1", resultado.getFirst().getNombre()); // Verifico que el producto es el esperado
    }

    @Test
    public void testListarConFiltradoPorCategoria_FallaSiNoExiste() {
        Long categoriaIdInexistente = 999L;

        // Mockeo el repositorio para que la categoría no exista
        when(categoriaRepository.existsById(categoriaIdInexistente)).thenReturn(false);

        // Verifico que se lanza la excepción
        assertThrows(CategoriaNoEncontradaException.class,
                () -> productoService.listarConFiltrado(null, null, categoriaIdInexistente, null));
    }

    //-- FILTRADO por usuarioId --//
    @Test
    public void testListarConFiltradoPorUsuario_Exito() throws UsuarioNoEncontradoException, CategoriaNoEncontradaException {
        // Datos de prueba
        Long usuarioId = 10L;

        // Usuario simulado en la BBDD
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNickname("usuario10");

        // Producto simulado en la BBDD
        Producto p1 = new Producto();
        p1.setId(1L);
        p1.setNombre("Producto1");
        p1.setPrecio(100.0f);
        p1.setUsuario(usuario);

        when(usuarioRepository.existsById(usuarioId)).thenReturn(true); // Comprobar existencia de usuario
        when(productoRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(p1)); // Devolver productos por usuario

        // Ejecución
        List<ProductoOutDto> resultado = productoService.listarConFiltrado(null, null, null, usuarioId);

        // Verificaciones
        assertNotNull(resultado); // Verifico que el resultado no es nulo
        assertEquals(1, resultado.size()); // Verifico que se devuelve 1 producto
        assertEquals("Producto1", resultado.getFirst().getNombre()); // Verifico que el producto es el esperado
    }

    @Test
    public void testListarConFiltradoPorUsuario_FallaSiNoExiste() {
        Long usuarioIdInexistente = 999L;

        // Mockeo el repositorio para que el usuario no exista
        when(usuarioRepository.existsById(usuarioIdInexistente)).thenReturn(false);

        // Verifico que se lanza la excepción
        assertThrows(UsuarioNoEncontradoException.class,
                () -> productoService.listarConFiltrado(null, null, null, usuarioIdInexistente));
    }

    // -- SIN FILTROS --> Devuelve todos los productos -- //
    @Test
    public void testListarConFiltros_SinFiltrosDevuelveTodos() throws UsuarioNoEncontradoException, CategoriaNoEncontradaException {
        // Productos simulados en la BBDD
        Producto p1 = new Producto();
        p1.setId(1L);
        p1.setNombre("Producto1");
        p1.setPrecio(100.0f);

        Producto p2 = new Producto();
        p2.setId(2L);
        p2.setNombre("Producto2");
        p2.setPrecio(150.0f);

        when(productoRepository.findAll())
                .thenReturn(List.of(p1, p2));

        List<ProductoOutDto> resultado = productoService.listarConFiltrado(null, null, null, null);

        assertEquals(2, resultado.size()); // Verifico que se devuelven 2 productos
        assertEquals(1L, resultado.get(0).getId()); // Verifico que el primer producto es el esperado
        assertEquals(2L, resultado.get(1).getId()); // Verifico que el segundo producto es el esperado
    }

    // ---------------------------------------------------------
    //            TEST PUT / actualizarConImagen()
    // ---------------------------------------------------------

    @Test
    public void testActualizarConImagen_Exito() throws ProductoNoEncontradoException, CategoriaNoEncontradaException {
        long idAModificar = 1;

        // UpdateDto con los datos a modificar
        ProductoUpdateDto updateDto = new ProductoUpdateDto();
        updateDto.setNombre("NuevoProducto");
        updateDto.setDescripcion("Nueva descripción");
        updateDto.setPrecio(150.0f);
        updateDto.setActivo(true);
        updateDto.setModo(false);
        updateDto.setCategoriaId(2L);
        updateDto.setImagen(new byte[]{1, 2, 3}); // Simula nueva imagen

        // Producto existente en la BBDD antes de la modificación
        Producto productoEnBBDD = new Producto();
        productoEnBBDD.setId(1L);
        productoEnBBDD.setNombre("ProductoViejo");
        productoEnBBDD.setDescripcion("Descripción vieja");
        productoEnBBDD.setPrecio(100.0f);
        productoEnBBDD.setActivo(false);
        productoEnBBDD.setModo(true);
        productoEnBBDD.setCategoria(null);
        productoEnBBDD.setImagen(null);

        // Categoria que existe en la BBDD
        Categoria categoria = new Categoria();
        categoria.setId(2L);
        categoria.setNombre("CategoriaNueva");

        // Producto después de la modificación
        Producto productoModificado = new Producto();
        productoModificado.setId(1L);
        productoModificado.setNombre("NuevoProducto");
        productoModificado.setDescripcion("Nueva descripción");
        productoModificado.setPrecio(150.0f);
        productoModificado.setActivo(true);
        productoModificado.setModo(false);
        productoModificado.setCategoria(categoria);
        productoModificado.setImagen(new byte[]{1, 2, 3});
        productoModificado.setFechaActualizacion(LocalDate.now());

        // ProductoOutDto esperado como resultado
        CategoriaSimpleDto categoriaDto = new CategoriaSimpleDto(2L, "CategoriaNueva");
        ProductoOutDto outDtoEsperado = new ProductoOutDto();
        outDtoEsperado.setId(1L);
        outDtoEsperado.setNombre("NuevoProducto");
        outDtoEsperado.setDescripcion("Nueva descripción");
        outDtoEsperado.setPrecio(150.0f);
        outDtoEsperado.setActivo(true);
        outDtoEsperado.setModo(false);
        outDtoEsperado.setCategoria(categoriaDto);
        outDtoEsperado.setImagenUrl("/productos/1/imagen");

        // Defino el comportamiento de los mocks
        when(productoRepository.findById(idAModificar)).thenReturn(java.util.Optional.of(productoEnBBDD));
        when(categoriaRepository.findById(2L)).thenReturn(java.util.Optional.of(categoria));
        lenient().doNothing().when(modelMapper).map(updateDto, productoEnBBDD);when(productoRepository.save(any(Producto.class))).thenReturn(productoModificado);
        // No necesito stub de modelMapper porque mapToOutDto es metodo privado manual

        // Llamo al metodo a testear
        ProductoOutDto resultado = productoService.actualizarConImagen(idAModificar, updateDto);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(idAModificar, resultado.getId());
        assertEquals("NuevoProducto", resultado.getNombre());
        assertEquals("Nueva descripción", resultado.getDescripcion());
        assertEquals(150.0f, resultado.getPrecio());
        assertTrue(resultado.isActivo());
        assertFalse(resultado.isModo());
        assertNotNull(resultado.getCategoria());
        assertEquals(2L, resultado.getCategoria().getId());
        assertEquals("CategoriaNueva", resultado.getCategoria().getNombre());
        assertEquals("/productos/1/imagen", resultado.getImagenUrl());
    }

    @Test
    public void testActualizarConImagen_FallaSiNoExisteProducto() {
        long idInexistente = 999;

        // UpdateDto con los datos a modificar
        ProductoUpdateDto updateDto = new ProductoUpdateDto();
        updateDto.setNombre("NuevoProducto");

        // Defino el comportamiento del mock
        when(productoRepository.findById(idInexistente)).thenReturn(java.util.Optional.empty());

        // Verifico que se lanza la excepción al intentar modificar un producto inexistente
        assertThrows(ProductoNoEncontradoException.class,
                () -> productoService.actualizarConImagen(idInexistente, updateDto));
    }

    // ---------------------------------------------------------
    //                TEST DELETE / eliminar()
    // ---------------------------------------------------------

    @Test
    public void testEliminarProducto_Exito() throws ProductoNoEncontradoException {
        long idAEliminar = 5;

        // Producto que simula estar en la BBDD antes de la eliminación
        Producto productoEnBBDD = new Producto();
        productoEnBBDD.setId(idAEliminar);

        // Defino el comportamiento del mock
        when(productoRepository.findById(idAEliminar)).thenReturn(java.util.Optional.of(productoEnBBDD));

        // Llamo al metodo a testear
        productoService.eliminar(idAEliminar);

        // Verificaciones
        verify(productoRepository, times(1)).delete(productoEnBBDD);
    }

    @Test
    public void testEliminarProducto_FallaSiNoExiste() {
        long idInexistente = 999;

        when(productoRepository.findById(idInexistente)).thenReturn(java.util.Optional.empty());

        // Verifico que se lanza la excepción al intentar eliminar un producto inexistente
        assertThrows(ProductoNoEncontradoException.class,
                () -> productoService.eliminar(idInexistente));

        // Verifico que no se llamó a delete
        verify(productoRepository, never()).delete(any(Producto.class));
    }


    // ---------------------------------------------------------
    //               TEST ENTIDAD / buscarPorEntidad()
    // ---------------------------------------------------------

    @Test
    public void testBuscarPorIdEntidad_Exito() throws ProductoNoEncontradoException {
        long id = 1L;

        // Producto que simula estar en la BBDD
        Producto producto = new Producto();
        producto.setId(id);

        // Defino el comportamiento del mock
        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        // Llamo al metodo a testear
        Producto resultado = productoService.buscarPorIdEntidad(id);

        assertNotNull(resultado); // Verifico que el resultado no es nulo
        assertEquals(id, resultado.getId()); // Verifico que el ID del producto es el esperado
    }

    @Test
    public void testBuscarPorIdEntidad_FallaSiNoExiste() {
        long idInexistente = 999L;

        when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(ProductoNoEncontradoException.class,
                () -> productoService.buscarPorIdEntidad(idInexistente));
    }

}
