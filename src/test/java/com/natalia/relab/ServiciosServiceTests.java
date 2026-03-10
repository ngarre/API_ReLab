package com.natalia.relab;

import com.natalia.relab.dto.ServiciosInDto;
import com.natalia.relab.dto.ServiciosOutDto;
import com.natalia.relab.model.Servicios;
import com.natalia.relab.repository.ServiciosRepository;
import com.natalia.relab.service.ServiciosService;
import exception.ServicioNoEncontradoException;
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
public class ServiciosServiceTests {

    @InjectMocks
    private ServiciosService serviciosService;

    @Mock
    private ServiciosRepository serviciosRepository;

    @Mock
    private ModelMapper modelMapper;

    // ---------------------------------------------------------
    //           TEST POST - agregar()
    // ---------------------------------------------------------

    @Test
    public void testAgregarServicio_Exito() {
        ServiciosInDto serviciosInDto = new ServiciosInDto(
                10L,                                    // idUsuario
                "usuario10",                            // nickname
                1,                                      // tipoServicio (1=Calibración)
                "Calibración de microscopio",           // descripcion
                "Servicio completado correctamente",    // comentario
                "usuario@example.com",                  // email
                "666123456",                            // telefono
                75.50                                   // precio
        );

        // Mock del servicio mapeado por ModelMapper de inDto a servicio
        Servicios servicioMapeado = new Servicios();

        // Mock del servicio guardado
        Servicios servicioGuardado = new Servicios();
        servicioGuardado.setId(1L);
        servicioGuardado.setFecha(LocalDate.now());
        servicioGuardado.setIdUsuario(10L);
        servicioGuardado.setNickname("usuario10");
        servicioGuardado.setTipoServicio(1);
        servicioGuardado.setDescripcion("Calibración de microscopio");
        servicioGuardado.setComentario("Servicio completado correctamente");
        servicioGuardado.setEmail("usuario@example.com");
        servicioGuardado.setTelefono("666123456");
        servicioGuardado.setPrecio(75.50);

        // Mock del DTO de salida
        ServiciosOutDto serviciosOutDto = new ServiciosOutDto(
                1L,
                LocalDate.now(),
                10L,
                "usuario10",
                1,
                "Calibración de microscopio",
                "Servicio completado correctamente",
                "usuario@example.com",
                "666123456",
                75.50
        );

        // Stubs
        when(modelMapper.map(serviciosInDto, Servicios.class)).thenReturn(servicioMapeado);
        when(serviciosRepository.save(any(Servicios.class))).thenReturn(servicioGuardado);
        when(modelMapper.map(servicioGuardado, ServiciosOutDto.class)).thenReturn(serviciosOutDto);

        // Metodo a testear
        ServiciosOutDto resultado = serviciosService.agregar(serviciosInDto);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getIdUsuario());
        assertEquals("usuario10", resultado.getNickname());
        assertEquals(1, resultado.getTipoServicio());
        assertEquals("Calibración de microscopio", resultado.getDescripcion());
        assertEquals("Servicio completado correctamente", resultado.getComentario());
        assertEquals("usuario@example.com", resultado.getEmail());
        assertEquals("666123456", resultado.getTelefono());
        assertEquals(75.50, resultado.getPrecio());
        assertNotNull(resultado.getFecha());
    }

    @Test
    public void testAgregarServicio_TipoMantenimiento() {
        ServiciosInDto serviciosInDto = new ServiciosInDto(
                20L,
                "usuario20",
                2,  // tipoServicio (2=Mantenimiento)
                "Mantenimiento de centrífuga",
                "Mantenimiento preventivo",
                "usuario20@example.com",
                "666234567",
                50.00
        );

        Servicios servicioMapeado = new Servicios();

        Servicios servicioGuardado = new Servicios();
        servicioGuardado.setId(2L);
        servicioGuardado.setFecha(LocalDate.now());
        servicioGuardado.setIdUsuario(20L);
        servicioGuardado.setNickname("usuario20");
        servicioGuardado.setTipoServicio(2);
        servicioGuardado.setDescripcion("Mantenimiento de centrífuga");
        servicioGuardado.setComentario("Mantenimiento preventivo");
        servicioGuardado.setEmail("usuario20@example.com");
        servicioGuardado.setTelefono("666234567");
        servicioGuardado.setPrecio(50.00);

        ServiciosOutDto serviciosOutDto = new ServiciosOutDto(
                2L,
                LocalDate.now(),
                20L,
                "usuario20",
                2,
                "Mantenimiento de centrífuga",
                "Mantenimiento preventivo",
                "usuario20@example.com",
                "666234567",
                50.00
        );

        when(modelMapper.map(serviciosInDto, Servicios.class)).thenReturn(servicioMapeado);
        when(serviciosRepository.save(any(Servicios.class))).thenReturn(servicioGuardado);
        when(modelMapper.map(servicioGuardado, ServiciosOutDto.class)).thenReturn(serviciosOutDto);

        ServiciosOutDto resultado = serviciosService.agregar(serviciosInDto);

        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
        assertEquals(2, resultado.getTipoServicio());
        assertEquals(50.00, resultado.getPrecio());
    }

    // ---------------------------------------------------------
    //           TEST GET Todos - listar()
    // ---------------------------------------------------------

    @Test
    public void testListarServicios_Exito() {
        // Servicio 1
        Servicios servicio1 = new Servicios();
        servicio1.setId(1L);
        servicio1.setFecha(LocalDate.now());
        servicio1.setIdUsuario(10L);
        servicio1.setNickname("usuario10");
        servicio1.setTipoServicio(1);
        servicio1.setDescripcion("Calibración");
        servicio1.setPrecio(75.50);

        // Servicio 2
        Servicios servicio2 = new Servicios();
        servicio2.setId(2L);
        servicio2.setFecha(LocalDate.now());
        servicio2.setIdUsuario(20L);
        servicio2.setNickname("usuario20");
        servicio2.setTipoServicio(2);
        servicio2.setDescripcion("Mantenimiento");
        servicio2.setPrecio(50.00);

        // DTOs de salida
        ServiciosOutDto outDto1 = new ServiciosOutDto(
                1L, LocalDate.now(), 10L, "usuario10", 1, "Calibración", "", "", "", 75.50
        );
        ServiciosOutDto outDto2 = new ServiciosOutDto(
                2L, LocalDate.now(), 20L, "usuario20", 2, "Mantenimiento", "", "", "", 50.00
        );

        // Mock: repositorio devuelve ambos servicios
        when(serviciosRepository.findAll()).thenReturn(List.of(servicio1, servicio2));
        when(modelMapper.map(servicio1, ServiciosOutDto.class)).thenReturn(outDto1);
        when(modelMapper.map(servicio2, ServiciosOutDto.class)).thenReturn(outDto2);

        // Metodo a testear
        List<ServiciosOutDto> resultado = serviciosService.listar();

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        assertEquals("usuario10", resultado.get(0).getNickname());
        assertEquals("usuario20", resultado.get(1).getNickname());
    }

    @Test
    public void testListarServicios_ListaVacia() {
        // Mock: repositorio devuelve lista vacía
        when(serviciosRepository.findAll()).thenReturn(List.of());

        // Metodo a testear
        List<ServiciosOutDto> resultado = serviciosService.listar();

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    // ---------------------------------------------------------
    //     TEST GET por idUsuario - buscarPorIdUsuario()
    // ---------------------------------------------------------

    @Test
    public void testBuscarPorIdUsuario_Exito() {
        long idUsuarioBuscado = 10L;

        // Servicio 1 del usuario
        Servicios servicio1 = new Servicios();
        servicio1.setId(1L);
        servicio1.setIdUsuario(idUsuarioBuscado);
        servicio1.setNickname("usuario10");
        servicio1.setTipoServicio(1);
        servicio1.setDescripcion("Calibración");

        // Servicio 2 del usuario
        Servicios servicio2 = new Servicios();
        servicio2.setId(2L);
        servicio2.setIdUsuario(idUsuarioBuscado);
        servicio2.setNickname("usuario10");
        servicio2.setTipoServicio(2);
        servicio2.setDescripcion("Mantenimiento");

        // DTOs de salida
        ServiciosOutDto outDto1 = new ServiciosOutDto(
                1L, LocalDate.now(), idUsuarioBuscado, "usuario10", 1, "Calibración", "", "", "", 0.0
        );
        ServiciosOutDto outDto2 = new ServiciosOutDto(
                2L, LocalDate.now(), idUsuarioBuscado, "usuario10", 2, "Mantenimiento", "", "", "", 0.0
        );

        // Stub
        when(serviciosRepository.findByIdUsuario(idUsuarioBuscado))
                .thenReturn(List.of(servicio1, servicio2));
        when(modelMapper.map(servicio1, ServiciosOutDto.class)).thenReturn(outDto1);
        when(modelMapper.map(servicio2, ServiciosOutDto.class)).thenReturn(outDto2);

        // Metodo a testear
        List<ServiciosOutDto> resultado = serviciosService.buscarPorIdUsuario(idUsuarioBuscado);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(idUsuarioBuscado, resultado.get(0).getIdUsuario());
        assertEquals(idUsuarioBuscado, resultado.get(1).getIdUsuario());
        assertEquals("usuario10", resultado.get(0).getNickname());
        assertEquals("usuario10", resultado.get(1).getNickname());
    }

    @Test
    public void testBuscarPorIdUsuario_SinServicios() {
        long idUsuarioBuscado = 999L;

        // Stub: el usuario no tiene servicios
        when(serviciosRepository.findByIdUsuario(idUsuarioBuscado))
                .thenReturn(List.of());

        // Metodo a testear
        List<ServiciosOutDto> resultado = serviciosService.buscarPorIdUsuario(idUsuarioBuscado);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    @Test
    public void testBuscarPorIdUsuario_MultiplesServicios() {
        long idUsuarioBuscado = 15L;

        // Crear 3 servicios del usuario
        Servicios servicio1 = new Servicios();
        servicio1.setId(10L);
        servicio1.setIdUsuario(idUsuarioBuscado);
        servicio1.setTipoServicio(1);

        Servicios servicio2 = new Servicios();
        servicio2.setId(11L);
        servicio2.setIdUsuario(idUsuarioBuscado);
        servicio2.setTipoServicio(2);

        Servicios servicio3 = new Servicios();
        servicio3.setId(12L);
        servicio3.setIdUsuario(idUsuarioBuscado);
        servicio3.setTipoServicio(3);

        // DTOs de salida
        ServiciosOutDto outDto1 = new ServiciosOutDto(10L, LocalDate.now(), idUsuarioBuscado, "", 1, "", "", "", "", 0.0);
        ServiciosOutDto outDto2 = new ServiciosOutDto(11L, LocalDate.now(), idUsuarioBuscado, "", 2, "", "", "", "", 0.0);
        ServiciosOutDto outDto3 = new ServiciosOutDto(12L, LocalDate.now(), idUsuarioBuscado, "", 3, "", "", "", "", 0.0);

        when(serviciosRepository.findByIdUsuario(idUsuarioBuscado))
                .thenReturn(List.of(servicio1, servicio2, servicio3));
        when(modelMapper.map(servicio1, ServiciosOutDto.class)).thenReturn(outDto1);
        when(modelMapper.map(servicio2, ServiciosOutDto.class)).thenReturn(outDto2);
        when(modelMapper.map(servicio3, ServiciosOutDto.class)).thenReturn(outDto3);

        List<ServiciosOutDto> resultado = serviciosService.buscarPorIdUsuario(idUsuarioBuscado);

        assertEquals(3, resultado.size());
        assertEquals(10L, resultado.get(0).getId());
        assertEquals(11L, resultado.get(1).getId());
        assertEquals(12L, resultado.get(2).getId());
    }

    // ---------------------------------------------------------
    //  TEST DELETE por ID - eliminar()
    // ---------------------------------------------------------

    @Test
    public void testEliminarServicio_Exito() throws ServicioNoEncontradoException {
        long servicioId = 1L;

        Servicios servicio = new Servicios();
        servicio.setId(servicioId);
        servicio.setNickname("usuario10");

        when(serviciosRepository.findById(servicioId)).thenReturn(Optional.of(servicio));

        // Metodo a testear
        serviciosService.eliminar(servicioId);

        // Verificar que se llamó a delete
        verify(serviciosRepository, times(1)).delete(servicio);
    }

    @Test
    public void testEliminarServicio_Falla_ServicioNoEncontrado() {
        long servicioId = 999L;

        // Stub: servicio no existe
        when(serviciosRepository.findById(servicioId)).thenReturn(Optional.empty());

        // Verificaciones
        assertThrows(
                ServicioNoEncontradoException.class,
                () -> serviciosService.eliminar(servicioId)
        );
    }


    // ---------------------------------------------------------
    //  TEST DELETE por idUsuario - eliminarPorIdUsuario()
    // ---------------------------------------------------------

    @Test
    public void testEliminarPorIdUsuario_Exito() throws ServicioNoEncontradoException {
        long idUsuario = 10L;

        // Servicios del usuario
        Servicios servicio1 = new Servicios();
        servicio1.setId(1L);
        servicio1.setIdUsuario(idUsuario);

        Servicios servicio2 = new Servicios();
        servicio2.setId(2L);
        servicio2.setIdUsuario(idUsuario);

        // Stub: repositorio devuelve los servicios del usuario
        when(serviciosRepository.findByIdUsuario(idUsuario))
                .thenReturn(List.of(servicio1, servicio2));

        // Metodo a testear
        serviciosService.eliminarPorIdUsuario(idUsuario);

        // Verificaciones
        verify(serviciosRepository, times(1)).deleteAll(List.of(servicio1, servicio2));
    }

    @Test
    public void testEliminarPorIdUsuario_Falla_SinServicios() {
        long idUsuario = 999L;

        // Stub: no existen servicios para este usuario
        when(serviciosRepository.findByIdUsuario(idUsuario))
                .thenReturn(List.of());

        // Verificaciones
        assertThrows(
                ServicioNoEncontradoException.class,
                () -> serviciosService.eliminarPorIdUsuario(idUsuario)
        );
    }
}









