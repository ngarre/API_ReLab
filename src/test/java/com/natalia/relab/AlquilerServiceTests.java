package com.natalia.relab;

import com.natalia.relab.dto.AlquilerInDto;
import com.natalia.relab.dto.AlquilerOutDto;
import com.natalia.relab.dto.AlquilerUpdateDto;
import com.natalia.relab.dto.CompraventaOutDto;
import com.natalia.relab.model.Alquiler;
import com.natalia.relab.model.Compraventa;
import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.AlquilerRepository;
import com.natalia.relab.repository.ProductoRepository;
import com.natalia.relab.repository.UsuarioRepository;
import com.natalia.relab.service.AlquilerService;
import exception.AlquilerNoEncontradoException;
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
class AlquilerServiceTests {

    @InjectMocks
    private AlquilerService alquilerService;

    @Mock
    private AlquilerRepository alquilerRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ModelMapper modelMapper;

    // ---------------------------------------------------------
    //           TEST POST - agregar()
    // ---------------------------------------------------------

    @Test
    public void testAgregarAlquiler_Exito() throws UsuarioNoEncontradoException, ProductoNoEncontradoException {
        AlquilerInDto alquilerInDto = new AlquilerInDto(
        LocalDate.of(2024, 6, 24),
        LocalDate.of(2025, 6, 24),
        12,
        1500.0f,
        "Alquiler de prueba",
        false,
        1L,
        2L,
        3L);

        // Mock Producto
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Centrífuga");

        // Mock Usuario1
        Usuario arrendador = new Usuario();
        arrendador.setId(2L);
        arrendador.setNombre("Juan");

        // Mock Usuario2
        Usuario arrendatario = new Usuario();
        arrendatario.setId(3L);
        arrendatario.setNombre("María");

        // Mockeo alquiler mapeado por ModelMapper de inDto a alquiler
        Alquiler alquilerMapeado = new Alquiler();

        // Mockeo alquiler guardado
        Alquiler alquilerGuardado = new Alquiler();
        alquilerGuardado.setId(1L);
        alquilerGuardado.setFechaInicio(LocalDate.of(2024, 6, 24));
        alquilerGuardado.setFechaFin(LocalDate.of(2025, 6, 24));
        alquilerGuardado.setMeses(12);
        alquilerGuardado.setPrecio(1500.0f);
        alquilerGuardado.setComentario("Alquiler de prueba");
        alquilerGuardado.setCancelado(false);
        alquilerGuardado.setProducto(producto);
        alquilerGuardado.setArrendador(arrendador);
        alquilerGuardado.setArrendatario(arrendatario);

        // Stubs
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(arrendador));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(arrendatario));
        when(modelMapper.map(alquilerInDto, Alquiler.class)).thenReturn(alquilerMapeado);
        when(alquilerRepository.save(any(Alquiler.class))).thenReturn(alquilerGuardado);

        // Metodo a testear
        AlquilerOutDto resultado = alquilerService.agregar(alquilerInDto);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(1L, resultado.getProducto().getId());
        assertEquals(2L, resultado.getArrendador().getId());
        assertEquals(3L, resultado.getArrendatario().getId());
        assertEquals(LocalDate.of(2024, 6, 24), resultado.getFechaInicio());
        assertEquals(LocalDate.of(2025, 6, 24), resultado.getFechaFin());
        assertEquals(12, resultado.getMeses());
        assertEquals(1500.0f, resultado.getPrecio());
        assertEquals("Alquiler de prueba", resultado.getComentario());
        assertFalse(resultado.isCancelado());
    }

    @Test
    public void TestAgregarAlquiler_FallaProductoNoEncontrado() {
        AlquilerInDto alquilerInDto = new AlquilerInDto(
        LocalDate.of(2024, 6, 24),
        LocalDate.of(2025, 6, 24),
        12,
        1500.0f,
        "Alquiler de prueba",
        false,
        1L,
        2L,
        3L);

        // Stub
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        // Verificaciones
         assertThrows(ProductoNoEncontradoException.class,
                 () -> {alquilerService.agregar(alquilerInDto);
        });
    }

    // ---------------------------------------------------------
    //           TEST GET by ID - buscarPorId()
    // ---------------------------------------------------------

    @Test
    public void testBuscarPorId_Exito() throws AlquilerNoEncontradoException {
        // ID del alquiler a buscar
        long alquilerId = 1L;

        // Alquiler que simula estar en la BBDD
        Alquiler alquilerEnBBDD = new Alquiler();
        alquilerEnBBDD.setId(alquilerId);
        alquilerEnBBDD.setComentario("Alquiler de prueba");

        // AlquilerOutDto que se espera como resultado
        AlquilerOutDto alquilerOutDto = new AlquilerOutDto();
        alquilerOutDto.setId(alquilerId);
        alquilerOutDto.setComentario("Alquiler de prueba");

        // Stubs
        when(alquilerRepository.findById(alquilerId)).thenReturn(Optional.of(alquilerEnBBDD));
        // No necesito stub de modelMapper porque mapToOutDto es metodo privado manual
        // El mapeo se realiza dentro del service

        // Metodo a testear
        AlquilerOutDto resultado = alquilerService.buscarPorId(alquilerId);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(alquilerId, resultado.getId());
        assertEquals("Alquiler de prueba", resultado.getComentario());
    }

    @Test
    public void testBuscarPorId_FallaAlquilerNoEncontrado() {
        // ID del alquiler a buscar
        long alquilerId = 999L;

        // Stub: alquiler no existe en la BBDD
        when(alquilerRepository.findById(alquilerId)).thenReturn(Optional.empty());

        // Verificaciones
        assertThrows(AlquilerNoEncontradoException.class,
                () -> {alquilerService.buscarPorId(alquilerId);
        });
    }

    // ---------------------------------------------------------
    //   TEST GET Todos o Aplicar Filtros - listarConFiltros()
    // ---------------------------------------------------------

    //-- FILTRADO por ARRENDADOR --//
    @Test
    public void testListarConFiltros_FiltradoPorArrendador_Exito() throws UsuarioNoEncontradoException, ProductoNoEncontradoException {
        // Id del arrendador a filtrar
        Long arrendadorId = 2L;

        // Mock: existe el arrendador
        when(usuarioRepository.existsById(arrendadorId)).thenReturn(true);

        // Alquiler simulado
        Alquiler alquiler = new Alquiler();
        alquiler.setId(1L);

        // Asocio arrendador al alquiler
        Usuario arrendador = new Usuario();
        arrendador.setId(arrendadorId);
        arrendador.setNickname("juan123");
        alquiler.setArrendador(arrendador);

        // Asocio producto
        Producto producto = new Producto();
        producto.setId(5L);
        producto.setNombre("Microscopio");
        alquiler.setProducto(producto);

        // Stub: devuelvo lista con un alquiler
        when(alquilerRepository.findByArrendadorId(arrendadorId)).thenReturn(java.util.List.of(alquiler));

        // Metodo a testear
        List<AlquilerOutDto> resultado =
                alquilerService.listarConFiltros(arrendadorId, null, null);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.getFirst().getId());
        assertEquals(2L, resultado.getFirst().getArrendador().getId());
        assertEquals(5L, resultado.getFirst().getProducto().getId());
    }

    @Test
    public void testListarConFiltros_FiltradoPorArrendador_FallaUsuarioNoEncontrado() {
        // Id del arrendador a filtrar
        Long arrendadorId = 999L;

        // Mock: no existe el arrendador
        when(usuarioRepository.existsById(arrendadorId)).thenReturn(false);

        // Verificaciones
        assertThrows(UsuarioNoEncontradoException.class,
                () -> {alquilerService.listarConFiltros(arrendadorId, null, null);
        });
    }

    //-- FILTRADO por ARRENDATARIO --//
    @Test
    public void testListarConFiltros_FiltradoPorArrendatario_Exito() throws UsuarioNoEncontradoException, ProductoNoEncontradoException {
        // Id del arrendatario a filtrar
        Long arrendatarioId = 3L;

        // Mock: existe el arrendatario
        when(usuarioRepository.existsById(arrendatarioId)).thenReturn(true);

        // Alquiler simulado
        Alquiler alquiler = new Alquiler();
        alquiler.setId(1L);

        // Asocio arrendatario al alquiler
        Usuario arrendatario = new Usuario();
        arrendatario.setId(arrendatarioId);
        arrendatario.setNickname("maria456");
        alquiler.setArrendatario(arrendatario);

        // Asocio producto
        Producto producto = new Producto();
        producto.setId(5L);
        producto.setNombre("Microscopio");
        alquiler.setProducto(producto);

        // Stub: devuelvo lista con un alquiler
        when(alquilerRepository.findByArrendatarioId(arrendatarioId)).thenReturn(List.of(alquiler));

        // Metodo a testear
        List<AlquilerOutDto> resultado =
                alquilerService.listarConFiltros(null, arrendatarioId, null);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.getFirst().getId());
        assertEquals(3L, resultado.getFirst().getArrendatario().getId());
        assertEquals(5L, resultado.getFirst().getProducto().getId());
    }

    @Test
    public void testListarConFiltros_FiltradoPorArrendatario_FallaUsuarioNoEncontrado() {
        // Id del arrendatario a filtrar
        Long arrendatarioId = 999L;

        // Mock: no existe el arrendatario
        when(usuarioRepository.existsById(arrendatarioId)).thenReturn(false);

        // Verificaciones
        assertThrows(UsuarioNoEncontradoException.class,
                () -> {
                    alquilerService.listarConFiltros(null, arrendatarioId, null);
                });
    }

    //-- FILTRADO por PRODUCTO --//
    @Test
    public void testListarConFiltros_PorProducto_Exito() throws UsuarioNoEncontradoException, ProductoNoEncontradoException {
        // Id del producto para filtrar
        Long productoId = 5L;

        // El producto existe
        when(productoRepository.existsById(productoId)).thenReturn(true);

        // Entidad Alquiler simulada
        Alquiler alquiler = new Alquiler();
        alquiler.setId(3L);

        // Asocio producto
        Producto producto = new Producto();
        producto.setId(5L);
        producto.setNombre("Microscopio");
        alquiler.setProducto(producto);

        // Asocio arrendador
        Usuario arrendador = new Usuario();
        arrendador.setId(10L);
        arrendador.setNickname("juan26");
        alquiler.setArrendador(arrendador);

        // Mock: el alquiler devuelto por el repositorio
        when(alquilerRepository.findByProductoId(productoId)).thenReturn(List.of(alquiler));

        List<AlquilerOutDto> resultado = alquilerService.listarConFiltros(null, null, productoId);

        assertNotNull(resultado); // La lista no es nula
        assertEquals(1, resultado.size()); // La lista tiene un elemento
        assertEquals(3L, resultado.getFirst().getId()); // El ID de la compraventa es correcto
        assertEquals(5L, resultado.getFirst().getProducto().getId()); // El ID del producto es correcto
    }

    @Test
    public void testListarConFiltros_PorProducto_FallaProductoNoEncontrado() {
        // Id del producto para filtrar
        Long productoId = 999L;

        // El producto no existe
        when(productoRepository.existsById(productoId)).thenReturn(false);

        // Verificaciones
        assertThrows(ProductoNoEncontradoException.class,
                () -> {
                    alquilerService.listarConFiltros(null, null, productoId);
                });
    }

    // -- SIN FILTROS --> Devuelve todos los alquileres -- //

    @Test
    public void testListarConFiltros_SinFiltros_Exito() throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        // Alquiler 1
        Alquiler a1 = new Alquiler();
        a1.setId(1L);

        // producto asociado a alquiler 1
        Producto p1 = new Producto();
        p1.setId(10L);
        p1.setNombre("Microscopio");
        a1.setProducto(p1);

        // Alquiler 2
        Alquiler a2 = new Alquiler();
        a2.setId(2L);

        // producto asociado a alquiler 2
        Producto p2 = new Producto();
        p2.setId(20L);
        p2.setNombre("Centrífuga");
        a2.setProducto(p2);

        // Mock: el repositorio devuelve ambos alquileres
        when(alquilerRepository.findAll()).thenReturn(List.of(a1, a2));

        // Metodo a testear
        List<AlquilerOutDto> resultado = alquilerService.listarConFiltros(null, null, null);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.getFirst().getId());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        assertEquals(10L, resultado.get(0).getProducto().getId());
        assertEquals(20L, resultado.get(1).getProducto().getId());
    }

    // ---------------------------------------------------------
    //            TEST PUT / modificar()
    // ---------------------------------------------------------

    @Test
    public void testModificarAlquiler_Exito() throws AlquilerNoEncontradoException {
        // --- ID ---
        long alquilerId = 1L;

        // --- DTO con datos a modificar ---
        AlquilerUpdateDto updateDto = new AlquilerUpdateDto();
        updateDto.setPrecio(2000.0f);
        updateDto.setComentario("Comentario modificado");
        updateDto.setCancelado(true);

        // --- Entidad original en BBDD ---
        Alquiler alquilerEnBBDD = new Alquiler();
        alquilerEnBBDD.setId(alquilerId);
        alquilerEnBBDD.setPrecio(1500.0f);
        alquilerEnBBDD.setComentario("Comentario viejo");
        alquilerEnBBDD.setCancelado(false);

        // Producto asociado
        Producto producto = new Producto();
        producto.setId(5L);
        producto.setNombre("Microscopio");
        alquilerEnBBDD.setProducto(producto);

        // Arrendador asociado
        Usuario arrendador = new Usuario();
        arrendador.setId(10L);
        arrendador.setNickname("juan26");
        alquilerEnBBDD.setArrendador(arrendador);

        // Arrendatario asociado
        Usuario arrendatario = new Usuario();
        arrendatario.setId(20L);
        arrendatario.setNickname("maria30");
        alquilerEnBBDD.setArrendatario(arrendatario);

        // --- Entidad resultante después de guardar ---
        Alquiler alquilerModificado = new Alquiler();
        alquilerModificado.setId(alquilerId);
        alquilerModificado.setPrecio(updateDto.getPrecio());
        alquilerModificado.setComentario(updateDto.getComentario());
        alquilerModificado.setCancelado(updateDto.getCancelado());
        alquilerModificado.setProducto(producto);
        alquilerModificado.setArrendador(arrendador);
        alquilerModificado.setArrendatario(arrendatario);

        // Mock findById → devuelve el alquiler existente
        when(alquilerRepository.findById(alquilerId)).thenReturn(Optional.of(alquilerEnBBDD));

        // Mock save → devuelve la versión modificada
        when(alquilerRepository.save(any(Alquiler.class))).thenReturn(alquilerModificado);

        // --- Ejecutar ---
        AlquilerOutDto resultado = alquilerService.modificar(alquilerId, updateDto);

        // --- Verificaciones ---
        assertNotNull(resultado);
        assertEquals(alquilerId, resultado.getId());
        assertEquals(2000.0f, resultado.getPrecio());
        assertEquals("Comentario modificado", resultado.getComentario());
        assertTrue(resultado.isCancelado());
        assertNotNull(resultado.getProducto());
        assertEquals(5L, resultado.getProducto().getId());
        assertEquals("Microscopio", resultado.getProducto().getNombre());
        assertEquals("juan26", resultado.getArrendador().getNickname());
        assertEquals("maria30", resultado.getArrendatario().getNickname());
    }

    @Test
    public void testModificarAlquiler_Falla_AlquilerNoEncontrado() {

        long alquilerId = 999L;

        // Mock: no existe el alquiler
        when(alquilerRepository.findById(alquilerId)).thenReturn(Optional.empty());

        // Verificar excepción
        assertThrows(
                AlquilerNoEncontradoException.class,
                () -> alquilerService.modificar(alquilerId, new AlquilerUpdateDto())
        );
    }

    // ---------------------------------------------------------
    //                TEST DELETE / eliminar()
    // ---------------------------------------------------------
    @Test
    public void testEliminarAlquiler_Exito() throws AlquilerNoEncontradoException {

        long alquilerId = 1L;

        Alquiler alquiler = new Alquiler();
        alquiler.setId(alquilerId);

        when(alquilerRepository.findById(alquilerId)).thenReturn(Optional.of(alquiler));

        // Ejecutar
        alquilerService.eliminar(alquilerId);

        // Verificar que se llamó a delete
        verify(alquilerRepository, times(1)).delete(alquiler);
    }

    @Test
    public void testEliminarAlquiler_Falla_AlquilerNoEncontrado() {
        // ID de alquiler que no existe
        long alquilerId = 999L;

        // Mock: no existe el alquiler
        when(alquilerRepository.findById(alquilerId)).thenReturn(Optional.empty());

        // Verificar excepción
        assertThrows(
                AlquilerNoEncontradoException.class,
                () -> alquilerService.eliminar(alquilerId)
        );
    }
}
