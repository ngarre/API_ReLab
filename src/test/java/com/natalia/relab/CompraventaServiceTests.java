package com.natalia.relab;

import com.natalia.relab.dto.CompraventaInDto;
import com.natalia.relab.dto.CompraventaOutDto;
import com.natalia.relab.model.Compraventa;
import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.CompraventaRepository;
import com.natalia.relab.repository.ProductoRepository;
import com.natalia.relab.repository.UsuarioRepository;
import com.natalia.relab.service.CompraventaService;
import exception.ProductoNoEncontradoException;
import exception.ProductoYaVendidoException;
import exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

        // Mockeo categoria mapeada por ModelMapper de inDto a compraventaGuardada
        Compraventa compraventaMapeada = new Compraventa();

        // Mockeo categoria guardada en la base de datos
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
    public void testAgregarCompraventa_FallaProductoYaVendido() {
        // TODO Implementar test para el caso en que el producto ya ha sido vendido
    }
}
