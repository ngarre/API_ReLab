package com.natalia.relab;

import com.natalia.relab.dto.CategoriaInDto;
import com.natalia.relab.dto.CategoriaOutDto;
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
import static org.mockito.Mockito.when;

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
    @Test
    public void testListarConFiltros_FiltradoPorNombre(){
        // Nombre por el que se quiere filtrar
        String nombre = "Centrífugas";

        // Categoria que simula estar en la BBDD
        Categoria categoriaEnBBDD = new Categoria();
        categoriaEnBBDD.setId(1L);
        categoriaEnBBDD.setNombre(nombre);

        // CategoriaOutDto que se espera como resultado
        CategoriaOutDto categoriaOutDto = new CategoriaOutDto();
        categoriaOutDto.setId(categoriaEnBBDD.getId());
        categoriaOutDto.setNombre(categoriaEnBBDD.getNombre());

        // Defino el comportamiento de los mocks
        when(categoriaRepository.findByNombreContainingIgnoreCase(nombre)).thenReturn(java.util.Optional.of(categoriaEnBBDD)); // Mockeo la búsqueda por nombre
        when(modelMapper.map(categoriaEnBBDD, CategoriaOutDto.class)).thenReturn(categoriaOutDto); // Mockeo el mapeo a DTO

        // Llamo al metodo a testear
        java.util.List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(nombre, null, null, null, null);

        // Verificaciones
        assertNotNull(resultados); // Verifico que no sea nulo
        assertEquals(1, resultados.size()); // Verifico que haya un solo resultado
        assertEquals(nombre, resultados.getFirst().getNombre()); // Verifico que el nombre coincida
    }

    //-- FILTRADO por ACTIVA --//
    @Test
    public void testListarConFiltros_FiltradoPorActiva() {
        // Defino el filtro
        boolean activa = true;

        // Categoria que simula estar en la BBDD
        Categoria categoriaEnBBDD = new Categoria();
        categoriaEnBBDD.setId(1L);
        categoriaEnBBDD.setNombre("Centrífugas");

        // CategoriaOutDto que se espera como resultado
        CategoriaOutDto categoriaOutDto = new CategoriaOutDto();
        categoriaOutDto.setId(categoriaEnBBDD.getId());
        categoriaOutDto.setNombre(categoriaEnBBDD.getNombre());

        // Defino el comportamiento de los mocks
        when(categoriaRepository.findByActiva(activa)).thenReturn(java.util.List.of(categoriaEnBBDD)); // Simulo que la categoria está en la BBDD
        when(modelMapper.map(categoriaEnBBDD, CategoriaOutDto.class)).thenReturn(categoriaOutDto); // Mockeo el mapeo a DTO

        // Llamo al metodo a testear
        java.util.List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(null, activa, null, null, null);

        // Verificaciones
        assertNotNull(resultados); // Verifico que no sea nulo
        assertEquals(1, resultados.size()); // Verifico que haya un solo resultado
        assertEquals("Centrífugas", resultados.getFirst().getNombre()); // Verifico que el nombre coincida
    }

    // -- FILTRADOS por FECHA DE CREACIÓN --//
    @Test
    public void testListarConFiltros_FiltradoPorFechaExacta() {
        LocalDate fechaExacta = LocalDate.of(2025, 11, 23);

        // Categoria que simula estar en la BBDD
        Categoria categoriaEnBBDD = new Categoria();
        categoriaEnBBDD.setId(1L);
        categoriaEnBBDD.setNombre("Centrífugas");
        categoriaEnBBDD.setFechaCreacion(fechaExacta);

        // CategoriaOutDto esperado
        CategoriaOutDto categoriaOutDto = new CategoriaOutDto();
        categoriaOutDto.setId(categoriaEnBBDD.getId());
        categoriaOutDto.setNombre(categoriaEnBBDD.getNombre());

        // Mockeo comportamiento del repositorio
        when(categoriaRepository.findByFechaCreacion(fechaExacta)).thenReturn(List.of(categoriaEnBBDD));
        when(modelMapper.map(categoriaEnBBDD, CategoriaOutDto.class)).thenReturn(categoriaOutDto);

        // Llamo al metodo a testear
        List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(null, null, fechaExacta, null, null);

        // Verificaciones
        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Centrífugas", resultados.getFirst().getNombre());
    }

    @Test
    public void testListarConFiltros_FiltradoPorRangoCompleto() {
        LocalDate desde = LocalDate.of(2025, 11, 1);
        LocalDate hasta = LocalDate.of(2025, 11, 30);

        // Categoria que simula estar en la BBDD
        Categoria categoriaEnBBDD = new Categoria();
        categoriaEnBBDD.setId(2L);
        categoriaEnBBDD.setNombre("Microscopios");
        categoriaEnBBDD.setFechaCreacion(LocalDate.of(2025, 11, 15)); // Fecha dentro del rango

        // CategoriaOutDto esperado
        CategoriaOutDto categoriaOutDto = new CategoriaOutDto();
        categoriaOutDto.setId(categoriaEnBBDD.getId());
        categoriaOutDto.setNombre(categoriaEnBBDD.getNombre());

        // Mockeo comportamiento del repositorio
        when(categoriaRepository.findByFechaCreacionBetween(desde, hasta)).thenReturn(List.of(categoriaEnBBDD));
        when(modelMapper.map(categoriaEnBBDD, CategoriaOutDto.class)).thenReturn(categoriaOutDto);

        // Llamo al metodo a testear
        List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(null, null, null, desde, hasta);

        // Verificaciones
        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Microscopios", resultados.getFirst().getNombre());
    }

    @Test
    public void testListarConFiltros_FiltradoPorRangoDesdeAbierto() {
        LocalDate desde = LocalDate.of(2025, 11, 1);

        // Categoria que simula estar en la BBDD
        Categoria categoriaEnBBDD = new Categoria();
        categoriaEnBBDD.setId(3L);
        categoriaEnBBDD.setNombre("Tubos de ensayo");
        categoriaEnBBDD.setFechaCreacion(LocalDate.of(2025, 11, 20)); // Fecha después del "desde"

        // CategoriaOutDto esperado
        CategoriaOutDto categoriaOutDto = new CategoriaOutDto();
        categoriaOutDto.setId(categoriaEnBBDD.getId());
        categoriaOutDto.setNombre(categoriaEnBBDD.getNombre());

        // Mockeo comportamiento del repositorio
        when(categoriaRepository.findByFechaCreacionBetween(desde, LocalDate.now()))
                .thenReturn(List.of(categoriaEnBBDD));
        when(modelMapper.map(categoriaEnBBDD, CategoriaOutDto.class)).thenReturn(categoriaOutDto);

        // Llamo al metodo a testear
        List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(null, null, null, desde, null);

        // Verificaciones
        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Tubos de ensayo", resultados.getFirst().getNombre());
    }

    // -- SIN FILTROS --> Devuelve todas las categorías -- //

    @Test
    public void testListarConFiltros_SinFiltros() {

        // Categorias que simulan estar en la BBDD
        Categoria categoria1 = new Categoria();
        categoria1.setId(1L);
        categoria1.setNombre("Centrífugas");

        Categoria categoria2 = new Categoria();
        categoria2.setId(2L);
        categoria2.setNombre("Microscopios");

        // CategoriaOutDto esperados
        CategoriaOutDto categoriaOutDto1 = new CategoriaOutDto();
        categoriaOutDto1.setId(categoria1.getId());
        categoriaOutDto1.setNombre(categoria1.getNombre());

        CategoriaOutDto categoriaOutDto2 = new CategoriaOutDto();
        categoriaOutDto2.setId(categoria2.getId());
        categoriaOutDto2.setNombre(categoria2.getNombre());

        // Stub del repositorio y mapeos
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria1, categoria2));
        when(modelMapper.map(categoria1, CategoriaOutDto.class)).thenReturn(categoriaOutDto1);
        when(modelMapper.map(categoria2, CategoriaOutDto.class)).thenReturn(categoriaOutDto2);

        // Llamo al metodo a testear
        List<CategoriaOutDto> resultados = categoriaService.listarConFiltros(null, null, null, null, null);

        // Verificaciones
        assertNotNull(resultados);
        assertEquals(2, resultados.size());
        assertEquals("Centrífugas", resultados.get(0).getNombre());
        assertEquals("Microscopios", resultados.get(1).getNombre());
    }

    // ---------------------------------------------------------
    //                  TEST PUT / modificar()
    // ---------------------------------------------------------
    // TODO
}
