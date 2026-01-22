package com.natalia.relab;

import com.natalia.relab.dto.CategoriaInDto;
import com.natalia.relab.dto.CategoriaOutDto;
import com.natalia.relab.dto.CategoriaUpdateDto;
import com.natalia.relab.model.Categoria;
import com.natalia.relab.repository.CategoriaRepository;
import com.natalia.relab.service.CategoriaService;
import exception.CategoriaNoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTests {

    @InjectMocks
    private CategoriaService categoriaService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ModelMapper modelMapper;

    // ---------------------------------------------------------
    //           TEST POST - agregar()
    // ---------------------------------------------------------

    @Test
    public void testAgregarCategoria_Exito(){
        // Campos para CategoriaInDto
        CategoriaInDto categoriaInDto = new CategoriaInDto(
                "Centrífugas",
                "Categoría de equipos de laboratorio que se utilizan para separar componentes de una mezcla mediante la fuerza centrífuga.",
                true,
                0.15f
        );

        // Esto mockea el resultado de volcar el DTO a la entidad con ModelMapper

        Categoria categoriaMapeada = new Categoria();

        // Aquí se mockea la categoría que ya está en la BBDD tras hacer el save
        Categoria categoriaGuardada = new Categoria();
        categoriaGuardada.setId(1L);
        categoriaGuardada.setNombre(categoriaInDto.getNombre());

        // Mockeo ejemplo de CategoriaOutDto que se devolvería tras mapear la categoría guardada
        CategoriaOutDto categoriaOutDto = new CategoriaOutDto();
        categoriaOutDto.setId(categoriaGuardada.getId());
        categoriaOutDto.setNombre(categoriaGuardada.getNombre());

        // Defino el comportamiento de los mocks
        when(modelMapper.map(categoriaInDto, Categoria.class)).thenReturn(categoriaMapeada);
        when(categoriaRepository.save(categoriaMapeada)).thenReturn(categoriaGuardada);
        when(modelMapper.map(categoriaGuardada, CategoriaOutDto.class)).thenReturn(categoriaOutDto);

        // Llamo al metodo a testear
        CategoriaOutDto resultado = categoriaService.agregar(categoriaInDto);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(categoriaInDto.getNombre(), resultado.getNombre());
    }

    // ---------------------------------------------------------
    //           TEST GET by ID - buscarPorId()
    // ---------------------------------------------------------

    @Test
    public void testBuscarPorId_Exito() throws CategoriaNoEncontradaException {
        // ID a buscar
        long idBuscado = 1L;

        // Categoría que se mockea como encontrada en la BBDD
        Categoria categoriaEncontrada = new Categoria();
        categoriaEncontrada.setId(idBuscado);
        categoriaEncontrada.setNombre("Centrífugas");

        // Mock del DTO que se devolvería tras mapear la categoría encontrada
        CategoriaOutDto categoriaOutDto = new CategoriaOutDto();
        categoriaOutDto.setId(categoriaEncontrada.getId());
        categoriaOutDto.setNombre(categoriaEncontrada.getNombre());

        // Defino el comportamiento de los mocks
        when(categoriaRepository.findById(idBuscado)).thenReturn(java.util.Optional.of(categoriaEncontrada));
        when(modelMapper.map(categoriaEncontrada, CategoriaOutDto.class)).thenReturn(categoriaOutDto);

        // Llamo al metodo a testear
        CategoriaOutDto resultado = categoriaService.buscarPorId(idBuscado);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(idBuscado, resultado.getId());
        assertEquals("Centrífugas", resultado.getNombre());
    }

    @Test
    public void testBuscarPorId_FallaSiNoExiste(){
        // ID a buscar
        long idBuscado = 99L;

        // Defino el comportamiento del mock para simular que no se encuentra la categoría
        when(categoriaRepository.findById(idBuscado)).thenReturn(java.util.Optional.empty());

        assertThrows(CategoriaNoEncontradaException.class, // Espero que lance esta excepción
                () -> categoriaService.buscarPorId(idBuscado)); // Llamada al metodo a testear
    }


    // ---------------------------------------------------------
    //   TEST GET Todos o Aplicar Filtros - listarConFiltros()
    // ---------------------------------------------------------
    // No tienen caso de fallo porque si no encuentra nada, devuelve lista vacía

    //-- FILTRADO por NOMBRE --//
    //-- FILTRADO por NOMBRE --//
    @Test
    public void testListarConFiltros_FiltradoPorNombre() {
        String nombre = "Centrífugas";

        Categoria categoriaEnBBDD = new Categoria();
        categoriaEnBBDD.setId(1L);
        categoriaEnBBDD.setNombre("Centrífugas");

        Categoria categoriaOtra = new Categoria();
        categoriaOtra.setId(2L);
        categoriaOtra.setNombre("Microscopios");

        when(categoriaRepository.findAll()).thenReturn(List.of(categoriaEnBBDD, categoriaOtra));
        when(modelMapper.map(any(Categoria.class), eq(CategoriaOutDto.class)))
                .thenAnswer(invocation -> {
                    Categoria c = invocation.getArgument(0);
                    CategoriaOutDto dto = new CategoriaOutDto();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    return dto;
                });

        List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(nombre, null, null, null, null);

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Centrífugas", resultados.getFirst().getNombre());
    }

    //-- FILTRADO por ACTIVA --//
    @Test
    public void testListarConFiltros_FiltradoPorActiva() {
        Categoria c1 = new Categoria();
        c1.setId(1L); c1.setNombre("Centrífugas"); c1.setActiva(true);

        Categoria c2 = new Categoria();
        c2.setId(2L); c2.setNombre("Microscopios"); c2.setActiva(false);

        when(categoriaRepository.findAll()).thenReturn(List.of(c1, c2));
        when(modelMapper.map(any(Categoria.class), eq(CategoriaOutDto.class)))
                .thenAnswer(invocation -> {
                    Categoria c = invocation.getArgument(0);
                    CategoriaOutDto dto = new CategoriaOutDto();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    return dto;
                });

        List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(null, true, null, null, null);

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Centrífugas", resultados.getFirst().getNombre());
    }

    //-- FILTRADO por FECHA EXACTA --//
    @Test
    public void testListarConFiltros_FiltradoPorFechaExacta() {
        LocalDate fechaExacta = LocalDate.of(2025, 11, 23);

        Categoria c1 = new Categoria();
        c1.setId(1L); c1.setNombre("Centrífugas"); c1.setFechaCreacion(fechaExacta);

        Categoria c2 = new Categoria();
        c2.setId(2L); c2.setNombre("Microscopios"); c2.setFechaCreacion(LocalDate.of(2025,11,24));

        when(categoriaRepository.findAll()).thenReturn(List.of(c1, c2));
        when(modelMapper.map(any(Categoria.class), eq(CategoriaOutDto.class)))
                .thenAnswer(invocation -> {
                    Categoria c = invocation.getArgument(0);
                    CategoriaOutDto dto = new CategoriaOutDto();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    return dto;
                });

        List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(null, null, fechaExacta, null, null);

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Centrífugas", resultados.getFirst().getNombre());
    }

    //-- FILTRADO por RANGO COMPLETO --//
    @Test
    public void testListarConFiltros_FiltradoPorRangoCompleto() {
        LocalDate desde = LocalDate.of(2025,11,1);
        LocalDate hasta = LocalDate.of(2025,11,30);

        Categoria c1 = new Categoria();
        c1.setId(1L); c1.setNombre("Centrífugas"); c1.setFechaCreacion(LocalDate.of(2025,11,15));

        Categoria c2 = new Categoria();
        c2.setId(2L); c2.setNombre("Microscopios"); c2.setFechaCreacion(LocalDate.of(2025,12,1));

        when(categoriaRepository.findAll()).thenReturn(List.of(c1, c2));
        when(modelMapper.map(any(Categoria.class), eq(CategoriaOutDto.class)))
                .thenAnswer(invocation -> {
                    Categoria c = invocation.getArgument(0);
                    CategoriaOutDto dto = new CategoriaOutDto();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    return dto;
                });

        List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(null, null, null, desde, hasta);

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Centrífugas", resultados.getFirst().getNombre());
    }

    //-- FILTRADO por RANGO DESDE ABIERTO --//
    @Test
    public void testListarConFiltros_FiltradoPorRangoDesdeAbierto() {
        LocalDate desde = LocalDate.of(2025,11,1);
        LocalDate hoy = LocalDate.now();

        Categoria c1 = new Categoria();
        c1.setId(1L); c1.setNombre("Centrífugas"); c1.setFechaCreacion(LocalDate.of(2025,11,20));

        Categoria c2 = new Categoria();
        c2.setId(2L); c2.setNombre("Microscopios"); c2.setFechaCreacion(LocalDate.of(2025,10,20));

        when(categoriaRepository.findAll()).thenReturn(List.of(c1, c2));
        when(modelMapper.map(any(Categoria.class), eq(CategoriaOutDto.class)))
                .thenAnswer(invocation -> {
                    Categoria c = invocation.getArgument(0);
                    CategoriaOutDto dto = new CategoriaOutDto();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    return dto;
                });

        List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(null, null, null, desde, null);

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Centrífugas", resultados.getFirst().getNombre());
    }

    //-- FILTRADO por RANGO HASTA ABIERTO --//
    @Test
    public void testListarConFiltros_FiltradoPorRangoHastaAbierto() {
        LocalDate hasta = LocalDate.of(2025,11,30);

        Categoria c1 = new Categoria();
        c1.setId(1L); c1.setNombre("Centrífugas"); c1.setFechaCreacion(LocalDate.of(2025,11,15));

        Categoria c2 = new Categoria();
        c2.setId(2L); c2.setNombre("Microscopios"); c2.setFechaCreacion(LocalDate.of(2025,12,1));

        when(categoriaRepository.findAll()).thenReturn(List.of(c1, c2));
        when(modelMapper.map(any(Categoria.class), eq(CategoriaOutDto.class)))
                .thenAnswer(invocation -> {
                    Categoria c = invocation.getArgument(0);
                    CategoriaOutDto dto = new CategoriaOutDto();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    return dto;
                });

        List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(null, null, null, null, hasta);

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Centrífugas", resultados.get(0).getNombre());
    }

    // -- SIN FILTROS --> Devuelve todas las categorías -- //
    @Test
    public void testListarConFiltros_SinFiltros() {
        Categoria c1 = new Categoria(); c1.setId(1L); c1.setNombre("Centrífugas");
        Categoria c2 = new Categoria(); c2.setId(2L); c2.setNombre("Microscopios");

        when(categoriaRepository.findAll()).thenReturn(List.of(c1, c2));
        when(modelMapper.map(any(Categoria.class), eq(CategoriaOutDto.class)))
                .thenAnswer(invocation -> {
                    Categoria c = invocation.getArgument(0);
                    CategoriaOutDto dto = new CategoriaOutDto();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    return dto;
                });

        List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(null, null, null, null, null);

        assertNotNull(resultados);
        assertEquals(2, resultados.size());
        assertEquals("Centrífugas", resultados.get(0).getNombre());
        assertEquals("Microscopios", resultados.get(1).getNombre());
    }

    // ---------------------------------------------------------
    //                  TEST PUT / modificar()
    // ---------------------------------------------------------

    @Test
    public void testModificarCategoria_Exito() throws CategoriaNoEncontradaException {
        // Id de la categoría a modificar
        long id = 1L;

        // Categoria Update DTO con los nuevos datos
        CategoriaUpdateDto updateDto = new CategoriaUpdateDto();
        updateDto.setNombre("Centrífugas Modificadas");
        updateDto.setDescripcion("Descripción modificada");

        // Categoria existente en la BBDD antes de la modificación
        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(id);
        categoriaExistente.setId(id);
        categoriaExistente.setNombre("Centrífugas");
        categoriaExistente.setDescripcion("Descripción original");

        // Categoria después de la modificación
        Categoria categoriaModificada = new Categoria();
        categoriaModificada.setId(id);
        categoriaModificada.setNombre(updateDto.getNombre());
        categoriaModificada.setDescripcion(updateDto.getDescripcion());

        // CategoriaOutDto esperado
        CategoriaOutDto categoriaOutDto = new CategoriaOutDto();
        categoriaOutDto.setId(categoriaModificada.getId());
        categoriaOutDto.setNombre(categoriaModificada.getNombre());
        categoriaOutDto.setDescripcion(categoriaModificada.getDescripcion());

        // Defino el comportamiento de los mocks
        when(categoriaRepository.findById(id)).thenReturn(java.util.Optional.of(categoriaExistente)); // Mockeo la búsqueda de la categoría existente
        lenient().doNothing().when(modelMapper).map(updateDto, categoriaExistente); // Mockeo el mapeo de los datos del UpdateDto sobre la entidad existente
        when(categoriaRepository.save(categoriaExistente)).thenReturn(categoriaExistente); // Mockeo el guardado de la categoría modificada
        when(modelMapper.map(categoriaExistente, CategoriaOutDto.class)).thenReturn(categoriaOutDto); // Mockeo el mapeo a outDto de la categoría modificada


        // Llamo al metodo a testear
        CategoriaOutDto resultado = categoriaService.modificar(id, updateDto);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals(updateDto.getNombre(), resultado.getNombre());
        assertEquals(updateDto.getDescripcion(), resultado.getDescripcion());
    }

    @Test
    public void testModificarCategoria_FallaSiNoExisteCategoria() throws CategoriaNoEncontradaException {
        long idInexistente = 99L;

        // UpdateDto con los nuevos datos
        CategoriaUpdateDto updateDto = new CategoriaUpdateDto();
        updateDto.setNombre("Nombre Nuevo");

        // Defino el comportamiento del mock para simular que no se encuentra la categoría
        when(categoriaRepository.findById(idInexistente)).thenReturn(java.util.Optional.empty());

        // Espero que lance la excepción al intentar modificar una categoría inexistente
        assertThrows(CategoriaNoEncontradaException.class,
                () -> categoriaService.modificar(idInexistente, updateDto));
    }

    // ---------------------------------------------------------
    //                TEST DELETE / eliminar()
    // ---------------------------------------------------------

    @Test
    public void testEliminarCategoria_Exito() throws CategoriaNoEncontradaException {
        // Id de la categoría a eliminar
        long id = 1L;

        // Categoria existente en la BBDD antes de la eliminación
        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(id);
        categoriaExistente.setNombre("Centrífugas");

        // Defino el comportamiento de los mocks
        when(categoriaRepository.findById(id)).thenReturn(java.util.Optional.of(categoriaExistente)); // Mockeo la búsqueda de la categoría existente

        // Llamo al metodo a testear
        categoriaService.eliminar(id);

        // Verificaciones
        verify(categoriaRepository, times(1)).delete(categoriaExistente);
    }

    @Test
    public void testEliminarCategoria_FallaSiNoExiste(){
        // Id de la categoría a eliminar
        long id = 99L;

        // Defino el comportamiento del mock para simular que no se encuentra la categoría
        when(categoriaRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(CategoriaNoEncontradaException.class, // Espero que lance esta excepción
                () -> categoriaService.eliminar(id)); // Llamada al metodo a testear
    }
}
