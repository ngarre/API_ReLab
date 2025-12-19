package com.natalia.relab;

import com.natalia.relab.controller.ProductoController;
import com.natalia.relab.dto.ProductoInDto;
import com.natalia.relab.dto.ProductoOutDto;
import com.natalia.relab.dto.ProductoUpdateDto;
import com.natalia.relab.service.ProductoService;
import exception.CategoriaNoEncontradaException;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductoController.class)
public class ProductoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------------------------------------------------
    //                  TEST POST - 201
    // ---------------------------------------------------------
    @Test
    public void testAgregarProducto_201_Exito() throws Exception {
        ProductoInDto inputDto = new ProductoInDto();
        inputDto.setNombre("Producto Test");
        inputDto.setPrecio(100f);
        inputDto.setUsuarioId(1L);
        inputDto.setCategoriaId(2L);
        inputDto.setActivo(true);
        inputDto.setModo(false);
        inputDto.setDescripcion("Descripción de prueba");

        ProductoOutDto outputDto = new ProductoOutDto();
        outputDto.setId(10L);
        outputDto.setNombre("Producto Test");

        Mockito.when(productoService.agregarConImagen(any(ProductoInDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Producto Test"));
    }

    // ---------------------------------------------------------
    //          TEST POST - 400
    // ---------------------------------------------------------
    @Test
    public void testAgregarProducto_400_PorValidacion() throws Exception {
        ProductoInDto inputDto = new ProductoInDto();
        // nombre obligatorio no se envía → salta validación
        inputDto.setPrecio(100f);
        inputDto.setUsuarioId(1L);

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nombre").exists());
    }

    // ---------------------------------------------------------
    //          TEST POST - 404 (Usuario No Existe)
    // ---------------------------------------------------------
    @Test
    public void testAgregarProducto_404_UsuarioNoExiste() throws Exception {
        ProductoInDto inputDto = new ProductoInDto();
        inputDto.setNombre("Producto Test");
        inputDto.setPrecio(100f);
        inputDto.setUsuarioId(999L); // Usuario que no existe

        Mockito.when(productoService.agregarConImagen(any(ProductoInDto.class)))
                .thenThrow(new UsuarioNoEncontradoException());

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    //          TEST POST - 404 (Categoria No Existe)
    // ---------------------------------------------------------
    @Test
    public void testAgregarProducto_404_CategoriaNoExiste() throws Exception {
        ProductoInDto inputDto = new ProductoInDto();
        inputDto.setNombre("Producto Test");
        inputDto.setPrecio(100f);
        inputDto.setUsuarioId(1L);
        inputDto.setCategoriaId(999L); // Categoria que no existe

        Mockito.when(productoService.agregarConImagen(any(ProductoInDto.class)))
                .thenThrow(new CategoriaNoEncontradaException());

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    //          TEST GET by ID - 200
    // ---------------------------------------------------------
    @Test
    public void testListarProductoPorId_200_Exito() throws Exception {
        long productoId = 1L;

        ProductoOutDto productoDto = new ProductoOutDto();
        productoDto.setId(productoId);
        productoDto.setNombre("Producto Test");

        Mockito.when(productoService.buscarPorId(productoId)).thenReturn(productoDto);

        mockMvc.perform(get("/productos/{id}", productoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productoId))
                .andExpect(jsonPath("$.nombre").value("Producto Test"));
    }

    // ---------------------------------------------------------
    //          TEST GET by ID - 404
    // ---------------------------------------------------------
    @Test
    public void testListarProductoPorId_404_NoEncontrado() throws Exception {
        long productoId = 999L;

        Mockito.when(productoService.buscarPorId(productoId))
                .thenThrow(new ProductoNoEncontradoException());

        mockMvc.perform(get("/productos/{id}", productoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    //          TEST GET Todos - 200 (Filtrando por Nombre)
    // ---------------------------------------------------------
    @Test
    public void testListarProductos_200_FiltroNombre() throws Exception {
        ProductoOutDto p = new ProductoOutDto();
        p.setId(1L);
        p.setNombre("Producto Test");

        Mockito.when(productoService.listarConFiltrado(eq("Producto Test"), any(), any(), any()))
                .thenReturn(List.of(p));

        mockMvc.perform(get("/productos")
                        .param("nombre", "Producto Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Producto Test"));
    }

    // -------------------------------------------------------------
    // TEST GET Todos - 404 (Filtrando por categoría que no existe)
    // --------------------------------------------------------------
    @Test
    public void testListarProductos_404_CategoriaNoEncontrada() throws Exception {
        Long categoriaId = 999L;

        Mockito.when(productoService.listarConFiltrado(any(), any(), eq(categoriaId), any()))
                .thenThrow(new CategoriaNoEncontradaException());

        mockMvc.perform(get("/productos")
                        .param("categoriaId", categoriaId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------
    //           TEST PUT - 200
    // -------------------------------------------
    @Test
    public void testActualizarProducto_200_Exito() throws Exception {
        long id = 1L;
        ProductoUpdateDto updateDto = new ProductoUpdateDto();
        updateDto.setNombre("Producto Nuevo");
        updateDto.setDescripcion("Descripción nueva");

        ProductoOutDto outDto = new ProductoOutDto();
        outDto.setId(id);
        outDto.setNombre("Producto Nuevo");

        Mockito.when(productoService.actualizarConImagen(eq(id), any(ProductoUpdateDto.class)))
                .thenReturn(outDto);

        mockMvc.perform(put("/productos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Producto Nuevo"));
    }

    // ---------------------------------------------------------
    //             TEST PUT - 404 (Producto No Encontrado)
    // ---------------------------------------------------------
    @Test
    public void testActualizarProducto_404_ProductoNoEncontrado() throws Exception {
        long id = 999L;
        ProductoUpdateDto updateDto = new ProductoUpdateDto();
        updateDto.setNombre("Producto Inexistente");

        Mockito.when(productoService.actualizarConImagen(eq(id), any(ProductoUpdateDto.class)))
                .thenThrow(new ProductoNoEncontradoException());

        mockMvc.perform(put("/productos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    //          TEST PUT 400 - (Validación falla)
    // ---------------------------------------------------------
    @Test
    public void testActualizarProducto_400_BadRequest() throws Exception {
        long id = 1L;
        ProductoUpdateDto updateDto = new ProductoUpdateDto();
        updateDto.setNombre(""); // Nombre obligatorio, vacío genera validación fallida

        mockMvc.perform(put("/productos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------
    //           TEST DELETE - 204
    // -------------------------------------------------
    @Test
    public void testEliminarProducto_204_Exito() throws Exception {
        long id = 1L;

        // No necesitamos configurar productoService.eliminar si no devuelve nada
        mockMvc.perform(delete("/productos/{id}", id))
                .andExpect(status().isNoContent());
    }

    // ---------------------------------------------------------
    //          TEST DELETE - 404
    // ---------------------------------------------------------
    @Test
    public void testEliminarProducto_404_ProductoNoEncontrado() throws Exception {
        long id = 999L;

        Mockito.doThrow(new ProductoNoEncontradoException())
                .when(productoService).eliminar(eq(id));

        mockMvc.perform(delete("/productos/{id}", id))
                .andExpect(status().isNotFound());
    }
}

