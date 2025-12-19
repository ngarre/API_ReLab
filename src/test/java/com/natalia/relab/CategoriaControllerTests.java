package com.natalia.relab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natalia.relab.controller.CategoriaController;
import com.natalia.relab.dto.CategoriaInDto;
import com.natalia.relab.dto.CategoriaOutDto;
import com.natalia.relab.dto.CategoriaUpdateDto;
import com.natalia.relab.service.CategoriaService;
import exception.CategoriaNoEncontradaException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------------------------------------------------
    //           TEST POST - 201
    // ---------------------------------------------------------
    @Test
    void testAgregarCategoria_201() throws Exception {
        // Categoria que entra (request)
        CategoriaInDto inDto = new CategoriaInDto();
        inDto.setNombre("Centrífugas");
        inDto.setDescripcion("Categoría de las centrífugas");

        // Categoría que devuelve el service (response)
        CategoriaOutDto outDto = new CategoriaOutDto();
        outDto.setId(1L);
        outDto.setNombre("Centrífugas");
        outDto.setDescripcion("Categoría de las centrífugas");

        // Mock del service
        when(categoriaService.agregar(any(CategoriaInDto.class)))
                .thenReturn(outDto);

        // Ejecución + verificaciones
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON) // Indica que el cuerpo es JSON
                        .content(objectMapper.writeValueAsString(inDto))) // Convierte objeto Java a JSON
                .andExpect(status().isCreated()) // Esperamos un 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Centrífugas"))
                .andExpect(jsonPath("$.descripcion").value("Categoría de las centrífugas"));
    }

    // ---------------------------------------------------------
    //           TEST POST - 400
    // ---------------------------------------------------------
    @Test
    public void testAgregarCategoria_400_PorValidacion() throws Exception {
        // Datos que provocan error de validación
        CategoriaInDto inDto = new CategoriaInDto();
        inDto.setNombre(""); // Nombre vacío → NotBlank fallará
        inDto.setDescripcion("abcd"); // Descripción demasiado corta si hay validación mínima

        // Ejecución + verificaciones
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isBadRequest()) // Esperamos un 400
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.descripcion").exists());
    }

    // ---------------------------------------------------------
    //           TEST Get by ID - 200
    // ---------------------------------------------------------
    @Test
    public void testListarCategoriaPorId_200_Exito() throws Exception {
        // --- Datos de prueba ---
        long categoriaId = 1L;
        CategoriaOutDto categoriaOutDto = new CategoriaOutDto();
        categoriaOutDto.setId(categoriaId);
        categoriaOutDto.setNombre("Microscopios");
        categoriaOutDto.setDescripcion("La categoría de los microscopios");

        // --- Configuramos el comportamiento del mock ---
        when(categoriaService.buscarPorId(categoriaId)).thenReturn(categoriaOutDto);

        // --- Llamada al endpoint GET /categorias/{id} ---
        mockMvc.perform(get("/categorias/{id}", categoriaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verificamos que devuelva 200
                .andExpect(jsonPath("$.id").value(categoriaId)) // Verificamos el ID
                .andExpect(jsonPath("$.nombre").value("Microscopios"))
                .andExpect(jsonPath("$.descripcion").value("La categoría de los microscopios"));

    }

    // ---------------------------------------------------------
    //           TEST Get by ID - 404
    // ---------------------------------------------------------
    @Test
    public void testListarCategoriaPorId_404_NoEncontrada() throws Exception {
        long categoriaId = 99L;

        when(categoriaService.buscarPorId(categoriaId)).thenThrow(new CategoriaNoEncontradaException());

        mockMvc.perform(get("/categorias/{id}", categoriaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Verificamos que devuelva 404
                .andExpect(jsonPath("$.message").value("La categoria no existe"));
    }

    // ---------------------------------------------------------
    //           TEST Get Todas - 200 (SIN FILTROS)
    // ---------------------------------------------------------

    @Test
    public void testListarCategorias_200_VariasCategorias() throws Exception {
        CategoriaOutDto c1 = new CategoriaOutDto();
        c1.setId(1L);
        c1.setNombre("Centrífugas");

        CategoriaOutDto c2 = new CategoriaOutDto();
        c2.setId(2L);
        c2.setNombre("Microscopios");

        List<CategoriaOutDto> categorias = List.of(c1, c2);

        when(categoriaService.listarConFiltros(null, null, null, null, null)).thenReturn(categorias);

        mockMvc.perform(get("/categorias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Centrífugas"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nombre").value("Microscopios"));

    }

    // ---------------------------------------------------------
    //           TEST Get Todos - 200 (FILTRO POR NOMBRE)
    // ---------------------------------------------------------
    @Test
    public void testListarCategorias_200_UnaSolaCategoriaPorNombre() throws Exception {
        long categoriaId = 1L;
        CategoriaOutDto categoria = new CategoriaOutDto();
        categoria.setId(categoriaId);
        categoria.setNombre("Microscopios");

        when(categoriaService.listarConFiltros("Microscopios", null, null, null,  null)).thenReturn(List.of(categoria));

        mockMvc.perform(get("/categorias")
                    .param("nombre", "Microscopios")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(categoriaId))
                .andExpect(jsonPath("$[0].nombre").value("Microscopios"));
    }

    // -------------------------------------------
    //           TEST PUT - 200
    // -------------------------------------------
    @Test
    public void testActualizarCategoria_200() throws Exception {
        long id = 1L;

        CategoriaUpdateDto updateDto = new CategoriaUpdateDto();
        updateDto.setNombre("Microscopios Actualizados");
        updateDto.setDescripcion("Descripción válida de más de 10 caracteres");
        updateDto.setActiva(true);
        updateDto.setTasaComision(0.2f);

        CategoriaOutDto outDto = new CategoriaOutDto();
        outDto.setId(id);
        outDto.setNombre(updateDto.getNombre());
        outDto.setDescripcion(updateDto.getDescripcion());

        when(categoriaService.modificar(id, updateDto)).thenReturn(outDto);

        mockMvc.perform(put("/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value(updateDto.getNombre()))
                .andExpect(jsonPath("$.descripcion").value(updateDto.getDescripcion()));
    }

    // -----------------------------------------------
    //           TEST PUT - 400
    // -----------------------------------------------
    @Test
    public void testActualizarCategoria_400_PorValidacion() throws Exception {
        long id = 1L;

        CategoriaUpdateDto updateDto = new CategoriaUpdateDto();
        updateDto.setNombre(""); // inválido
        updateDto.setDescripcion("Corta"); // inválido (<10 caracteres)

        mockMvc.perform(put("/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.descripcion").exists());
    }

    // -------------------------------------------------
    //        TEST PUT - 404 (Categoria No Encontrada)
    // -------------------------------------------------
    @Test
    public void testActualizarCategoria_404_CategoriaNoEncontrada() throws Exception {
        long id = 999L; // ID inexistente

        CategoriaUpdateDto updateDto = new CategoriaUpdateDto();
        updateDto.setNombre("Nombre válido");
        updateDto.setDescripcion("Descripción válida de más de 10 caracteres");
        updateDto.setActiva(true);
        updateDto.setTasaComision(0.5f);

        // Simulamos que el servicio lanza excepción
        when(categoriaService.modificar(id, updateDto))
                .thenThrow(new CategoriaNoEncontradaException());

        mockMvc.perform(put("/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------
    //           TEST DELETE - 204
    // -------------------------------------------------
    @Test
    public void testEliminarCategoria_204() throws Exception {
        long id = 1L;

        // Mock del servicio: no lanza excepción, elimina correctamente
        doNothing().when(categoriaService).eliminar(id);

        mockMvc.perform(delete("/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // -------------------------------------------------
    //           TEST DELETE - 404
    // -------------------------------------------------
    @Test
    public void testEliminarCategoria_404_CategoriaNoEncontrada() throws Exception {
        long id = 999L;

        // Mock del servicio: lanza excepción indicando que no existe la categoría
        doThrow(new CategoriaNoEncontradaException()).when(categoriaService).eliminar(id);

        mockMvc.perform(delete("/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}



