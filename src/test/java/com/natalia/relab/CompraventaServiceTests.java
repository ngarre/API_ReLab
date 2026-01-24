package com.natalia.relab;

import com.natalia.relab.dto.CompraventaInDto;
import com.natalia.relab.dto.CompraventaOutDto;
import com.natalia.relab.dto.CompraventaUpdateDto;
import com.natalia.relab.model.Compraventa;
import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.CompraventaRepository;
import com.natalia.relab.repository.ProductoRepository;
import com.natalia.relab.repository.UsuarioRepository;
import com.natalia.relab.service.CompraventaService;
import exception.CompraventaNoEncontradaException;
import exception.ProductoNoEncontradoException;
import exception.ProductoYaVendidoException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompraventaServiceTests {

    @InjectMocks
    private CompraventaService compraventaService;

    @Mock
    private CompraventaRepository compraventaRepository;

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
    public void testAgregarCompraventa_Exito() throws UsuarioNoEncontradoException, ProductoNoEncontradoException, ProductoYaVendidoException {
        CompraventaInDto inDto = new CompraventaInDto(
            1L,
            2L,
            3L,
            false,
            "Comentario de prueba",
            100.0f
        );

        // Mock Producto
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Centrífuga");

        // Mock Usuario1
        Usuario comprador = new Usuario();
        comprador.setId(2L);
        comprador.setNombre("Juan");

        // Mock Usuario2
        Usuario vendedor = new Usuario();
        vendedor.setId(3L);
        vendedor.setNombre("Maria");

        // Mockeo compraventa mapeada por ModelMapper de inDto a compraventa
        Compraventa compraventaMapeada = new Compraventa();

        // Mockeo compraventa guardada en la base de datos
        Compraventa compraventaGuardada = new Compraventa();
        compraventaGuardada.setId(1L);
        compraventaGuardada.setProducto(producto);
        compraventaGuardada.setComprador(comprador);
        compraventaGuardada.setVendedor(vendedor);
        compraventaGuardada.setComentario("Comentario de prueba");
        compraventaGuardada.setPrecioFinal(100.0f);

        // Stubs
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(modelMapper.map(inDto, Compraventa.class)).thenReturn(compraventaMapeada);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(comprador));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(vendedor));
        when(compraventaRepository.save(any(Compraventa.class))).thenReturn(compraventaGuardada);

        // Metodo a testear
        CompraventaOutDto resultado = compraventaService.agregar(inDto);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(1L, resultado.getProducto().getId());
        assertEquals(2L, resultado.getComprador().getId());
        assertEquals(3L, resultado.getVendedor().getId());
        assertEquals("Comentario de prueba", resultado.getComentario());
        assertEquals(100.0f, resultado.getPrecioFinal());
    }

    @Test
    public void testAgregarCompraventa_FallaProductoYaVendido() throws UsuarioNoEncontradoException, ProductoNoEncontradoException, ProductoYaVendidoException {
        CompraventaInDto inDto = new CompraventaInDto(
                1L,
                2L,
                3L,
                false,
                "Comentario de prueba",
                100.0f
        );

        // Mock: el producto existe
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("ProductoPrueba");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Mock: el producto ya está vendido
        when(compraventaRepository.existsByProductoId(1L)).thenReturn(true);

        // No hace falta mockear compradores/vendedores porque la excepción ocurre antes

        // Verificación: esperamos ProductoYaVendidoException
        assertThrows(ProductoYaVendidoException.class,
                () -> compraventaService.agregar(inDto)
        );
    }

    // ---------------------------------------------------------
    //           TEST GET by ID - buscarPorId()
    // ---------------------------------------------------------

    @Test
    public void testBuscarPorId_Exito() throws CompraventaNoEncontradaException{
        // ID de la compraventa a buscar
        long compraventaId = 1L;

        // Compraventa que simula estar en la BBDD
        Compraventa compraventaEnBBDD = new Compraventa();
        compraventaEnBBDD.setId(compraventaId);
        compraventaEnBBDD.setComentario("Comentario de prueba");

        // CompraventaOutDto que se espera como resultado
        CompraventaOutDto outDto = new CompraventaOutDto();
        outDto.setId(compraventaId);
        outDto.setComentario("Comentario de prueba");

        // Stubs
        when(compraventaRepository.findById(compraventaId)).thenReturn(Optional.of(compraventaEnBBDD));
        // No necesito stub de modelMapper porque mapToOutDto es metodo privado manual
        // El mapeo se realiza dentro del service

        // Metodo a testear
        CompraventaOutDto resultado = compraventaService.buscarPorId(compraventaId);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(compraventaId, resultado.getId());
        assertEquals("Comentario de prueba", resultado.getComentario());
    }

    @Test
    public void testBuscarPorId_FallaCompraventaNoEncontrada() {
        // ID de la compraventa a buscar
        long compraventaId = 999L;

        // Stub: la compraventa no existe en la BBDD
        when(compraventaRepository.findById(compraventaId)).thenReturn(Optional.empty());

        // Verificación: esperamos CompraventaNoEncontradaException
        assertThrows(CompraventaNoEncontradaException.class,
                () -> compraventaService.buscarPorId(compraventaId)
        );
    }

    // ---------------------------------------------------------
    //   TEST GET Todas o Aplicar Filtros - listarConFiltros()
    // ---------------------------------------------------------

    //-- FILTRADO por COMPRADOR --//
    @Test
    public void testListarConFiltros_PorComprador_Exito()
            throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        Long compradorId = 10L;
        when(usuarioRepository.existsById(compradorId)).thenReturn(true);

        // Compraventa que SÍ coincide
        Compraventa c1 = new Compraventa();
        c1.setId(1L);
        Usuario comprador = new Usuario();
        comprador.setId(10L);
        c1.setComprador(comprador);

        // Compraventa que NO coincide
        Compraventa c2 = new Compraventa();
        c2.setId(2L);
        Usuario otro = new Usuario();
        otro.setId(99L);
        c2.setComprador(otro);

        when(compraventaRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CompraventaOutDto> resultado =
                compraventaService.listarConFiltros(compradorId, null, null);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.getFirst().getId());
    }


    @Test
    public void testListarConFiltros_PorComprador_FallaSiNoExiste() {

        Long compradorId = 999L;

        when(usuarioRepository.existsById(compradorId)).thenReturn(false);

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> compraventaService.listarConFiltros(compradorId, null, null)
        );
    }


    //-- FILTRADO por VENDEDOR --//
    @Test
    public void testListarConFiltros_PorVendedor_Exito()
            throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        Long vendedorId = 20L;
        when(usuarioRepository.existsById(vendedorId)).thenReturn(true);

        Compraventa c1 = new Compraventa();
        c1.setId(2L);
        Usuario vendedor = new Usuario();
        vendedor.setId(20L);
        c1.setVendedor(vendedor);

        Compraventa c2 = new Compraventa();
        c2.setId(3L);

        when(compraventaRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CompraventaOutDto> resultado =
                compraventaService.listarConFiltros(null, vendedorId, null);

        assertEquals(1, resultado.size());
        assertEquals(2L, resultado.getFirst().getId());
    }


    @Test
    public void testListarConFiltros_PorVendedor_FallaSiNoExiste() {
        // Id de vendedor inexistente
        Long vendedorId = 999L;

        // Mock: no existe vendedor
        when(usuarioRepository.existsById(vendedorId)).thenReturn(false);

        // Verificación: se lanza UsuarioNoEncontradoException
        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> compraventaService.listarConFiltros(null, vendedorId, null)
        );
    }


    //-- FILTRADO por PRODUCTO --//
    @Test
    public void testListarConFiltros_PorProducto_Exito()
            throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        Long productoId = 5L;
        when(productoRepository.existsById(productoId)).thenReturn(true);

        Compraventa c1 = new Compraventa();
        c1.setId(3L);
        Producto p = new Producto();
        p.setId(5L);
        c1.setProducto(p);

        Compraventa c2 = new Compraventa();
        c2.setId(4L);

        when(compraventaRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CompraventaOutDto> resultado =
                compraventaService.listarConFiltros(null, null, productoId);

        assertEquals(1, resultado.size());
        assertEquals(3L, resultado.getFirst().getId());
    }


    // Producto existe PERO no tiene compraventa asociada
    @Test
    public void testListarConFiltros_PorProducto_SinResultados()
            throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        Long productoId = 5L;
        when(productoRepository.existsById(productoId)).thenReturn(true);

        Compraventa c1 = new Compraventa();
        c1.setId(1L);

        when(compraventaRepository.findAll()).thenReturn(List.of(c1));

        List<CompraventaOutDto> resultado =
                compraventaService.listarConFiltros(null, null, productoId);

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }


    @Test
    public void testListarConFiltros_PorProducto_FallaSiNoExiste() {
        // Id de producto inexistente
        Long productoId = 99L;

        // Mock: no existe producto
        when(productoRepository.existsById(productoId)).thenReturn(false);

        assertThrows(ProductoNoEncontradoException.class,
                () -> compraventaService.listarConFiltros(null, null, productoId)
        );
    }

    // -- SIN FILTROS --> Devuelve todas las compraventas -- //
    @Test
    public void testListarConFiltros_SinFiltros_DevuelveTodas()
            throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        Compraventa c1 = new Compraventa();
        c1.setId(1L);

        Compraventa c2 = new Compraventa();
        c2.setId(2L);

        when(compraventaRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CompraventaOutDto> resultado =
                compraventaService.listarConFiltros(null, null, null);

        assertEquals(2, resultado.size());
    }

    // FILTROS COMBINADOS
    @Test
    public void testListarConFiltros_FiltrosCombinados_Exito()
            throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        Long compradorId = 1L;
        Long vendedorId = 2L;
        Long productoId = 10L;

        when(usuarioRepository.existsById(compradorId)).thenReturn(true);
        when(usuarioRepository.existsById(vendedorId)).thenReturn(true);
        when(productoRepository.existsById(productoId)).thenReturn(true);

        // Coincide
        Compraventa c1 = new Compraventa();
        c1.setId(1L);

        Usuario comprador = new Usuario();
        comprador.setId(1L);
        c1.setComprador(comprador);

        Usuario vendedor = new Usuario();
        vendedor.setId(2L);
        c1.setVendedor(vendedor);

        Producto producto = new Producto();
        producto.setId(10L);
        c1.setProducto(producto);

        // No coincide
        Compraventa c2 = new Compraventa();
        c2.setId(2L);

        when(compraventaRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CompraventaOutDto> resultado =
                compraventaService.listarConFiltros(compradorId, vendedorId, productoId);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.getFirst().getId());
    }


    // ---------------------------------------------------------
    //            TEST PUT / modificar()
    // ---------------------------------------------------------
    @Test
    public void testModificarCompraventa_Exito() throws CompraventaNoEncontradaException {
        // ID de la compraventa a modificar
        long idAModificar = 1L;

        // UpdateDto con datos a modificar
        CompraventaUpdateDto updateDto = new CompraventaUpdateDto();
        updateDto.setPrecioFinal(200.0f);
        updateDto.setComentario("Nuevo comentario");
        updateDto.setDevuelto(true);

        // Compraventa existente antes de modificar
        Compraventa compraventaEnBBDD = new Compraventa();
        compraventaEnBBDD.setId(idAModificar);
        compraventaEnBBDD.setPrecioFinal(150.0f);
        compraventaEnBBDD.setComentario("Comentario viejo");
        compraventaEnBBDD.setDevuelto(false);

        // Asocio producto
        Producto producto = new Producto();
        producto.setId(5L);
        producto.setNombre("Microscopio");
        compraventaEnBBDD.setProducto(producto);

        // Asocio comprador
        Usuario comprador = new Usuario();
        comprador.setId(10L);
        comprador.setNickname("juan");
        compraventaEnBBDD.setComprador(comprador);

        // Asocio vendedor
        Usuario vendedor = new Usuario();
        vendedor.setId(20L);
        vendedor.setNickname("maria");
        compraventaEnBBDD.setVendedor(vendedor);

        // Compraventa después de la modificación
        Compraventa compraventaModificada = new Compraventa();
        compraventaModificada.setId(idAModificar);
        compraventaModificada.setPrecioFinal(200.0f);
        compraventaModificada.setComentario("Nuevo comentario");
        compraventaModificada.setDevuelto(true);
        compraventaModificada.setProducto(producto);
        compraventaModificada.setComprador(comprador);
        compraventaModificada.setVendedor(vendedor);

        // Mocks
        when(compraventaRepository.findById(idAModificar))
                .thenReturn(Optional.of(compraventaEnBBDD));

        // modelMapper.map() solo copia valores → no debe devolver nada
        lenient().doAnswer(inv -> {
            // Simular mapeo:
            compraventaEnBBDD.setPrecioFinal(updateDto.getPrecioFinal());
            compraventaEnBBDD.setComentario(updateDto.getComentario());
            compraventaEnBBDD.setDevuelto(updateDto.getDevuelto());
            return null;
        }).when(modelMapper).map(updateDto, compraventaEnBBDD);

        // Mock guardado
        when(compraventaRepository.save(any(Compraventa.class))).thenReturn(compraventaModificada);

        // No hace falta mockear mapToOutDto porque es metodo privado manual

        // Ejecución
        CompraventaOutDto resultado = compraventaService.modificar(idAModificar, updateDto);

        // Verificaciones
        assertNotNull(resultado); // El resultado no es nulo
        assertEquals(idAModificar, resultado.getId()); // El ID es correcto
        assertEquals(200.0f, resultado.getPrecioFinal()); // El precio final modificado es correcto
        assertEquals("Nuevo comentario", resultado.getComentario()); // El comentario modificado es correcto
        assertTrue(resultado.isDevuelto()); // El estado devuelto modificado es correcto
        assertEquals(5L, resultado.getProducto().getId()); // El ID del producto es correcto
        assertEquals(10L, resultado.getComprador().getId()); // El ID del comprador es correcto
        assertEquals(20L, resultado.getVendedor().getId()); // El ID del vendedor es correcto
    }

    @Test
    public void testModificarCompraventa_FallaSiNoExiste() {

        // ID de compraventa inexistente
        long idInexistente = 999L;

        // Mock: la compraventa no existe
        when(compraventaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Verificación: lanza la excepción correspondiente
        assertThrows(CompraventaNoEncontradaException.class,
                () -> compraventaService.modificar(idInexistente, new CompraventaUpdateDto())
        );
    }

    // ---------------------------------------------------------
    //                TEST DELETE / eliminar()
    // ---------------------------------------------------------
    @Test
    public void testEliminarCompraventa_Exito() throws CompraventaNoEncontradaException {

        long idAEliminar = 1L;

        // Compraventa existente en la BBDD
        Compraventa compraventa = new Compraventa();
        compraventa.setId(idAEliminar);

        // Mocks
        when(compraventaRepository.findById(idAEliminar)).thenReturn(Optional.of(compraventa));

        // Llamada al metodo
        compraventaService.eliminar(idAEliminar);

        // Verificaciones
        verify(compraventaRepository, times(1)).findById(idAEliminar); // Verifica que se buscó la compraventa
        verify(compraventaRepository, times(1)).delete(compraventa); // Verifica que se eliminó la compraventa
    }

    @Test
    public void testEliminarCompraventa_FallaSiNoExiste() {
        // ID de compraventa inexistente
        long idInexistente = 999L;

        // Mock: la compraventa no existe
        when(compraventaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Verificación: se lanza excepción
        assertThrows(CompraventaNoEncontradaException.class,
                () -> compraventaService.eliminar(idInexistente)
        );

        // Y aseguramos que delete NO fue llamado nunca
        verify(compraventaRepository, never()).delete(any());
    }
}
