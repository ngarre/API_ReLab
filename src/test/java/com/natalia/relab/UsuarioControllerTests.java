package com.natalia.relab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natalia.relab.controller.UsuarioController;
import com.natalia.relab.dto.UsuarioInDto;
import com.natalia.relab.dto.UsuarioOutDto;
import com.natalia.relab.dto.UsuarioUpdateDto;
import com.natalia.relab.service.UsuarioService;
import exception.NicknameYaExisteException;
import exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class) // Levanta solo la capa web, no toda la aplicación
public class UsuarioControllerTests {

    @Autowired
    private MockMvc mockMvc; // Simula peticiones HTTP reales sin levantar el servidor

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper; // Convierte DTO a JSON real como Postman


    // ---------------------------------------------------------
    //           TEST POST - 201
    // ---------------------------------------------------------
    @Test
    void testAgregarUsuario_201() throws Exception {

        // Usuario que entra (request)
        UsuarioInDto inDto = new UsuarioInDto();
        inDto.setNickname("natalia");
        inDto.setPassword("1234");
        inDto.setEmail("natalia@email.com");
        inDto.setNombre("Natalia");

        // Usuario que devuelve el service (response)
        UsuarioOutDto outDto = new UsuarioOutDto();
        outDto.setId(1L);
        outDto.setNickname("natalia");
        outDto.setEmail("natalia@email.com");
        outDto.setNombre("Natalia");

        // Mock del service
        when(usuarioService.agregar(any(UsuarioInDto.class)))
                .thenReturn(outDto);

        // Ejecución + verificaciones
        mockMvc.perform(post("/usuarios")
                        // Dice a Spring MVC que el cuerpo de la petición está en formato JSON
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convierte objeto JAVA UsuarioInDto a JSON real
                        .content(objectMapper.writeValueAsString(inDto)))
                // Esperamos un 201
                .andExpect(status().isCreated())
                // Esperamos un JSON
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // jsonPath comprueba cuerpo JSON de la respuesta
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nickname").value("natalia"))
                .andExpect(jsonPath("$.email").value("natalia@email.com"))
                .andExpect(jsonPath("$.nombre").value("Natalia"));
    }

    // ---------------------------------------------------------
    //           TEST POST - 400
    // ---------------------------------------------------------
    @Test
    public void testAgregarUsuario_400_PorValidacion() throws Exception {
        UsuarioInDto inDto = new UsuarioInDto();
        inDto.setNickname("");          // inválido (no puede ser un String vacío
        inDto.setPassword("123");       // inválido (menos de 4 caracteres)
        inDto.setEmail("email-mal");    // inválido (mal formato)

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nickname").exists())
                .andExpect(jsonPath("$.errors.password").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    // ---------------------------------------------------------
    //           TEST Get by ID - 200
    // ---------------------------------------------------------
    @Test
    public void testListarUsuarioPorId_200_Exito() throws Exception {
        // --- Datos de prueba ---
        long usuarioId = 1L;
        UsuarioOutDto usuarioOutDto = new UsuarioOutDto();
        usuarioOutDto.setId(usuarioId);
        usuarioOutDto.setNickname("usuario1");
        usuarioOutDto.setEmail("usuario1@email.com");
        usuarioOutDto.setNombre("Nombre1");

        // --- Configuramos el comportamiento del mock ---
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(usuarioOutDto);

        // --- Llamada al endpoint GET /usuarios/{id} ---
        mockMvc.perform(get("/usuarios/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verificamos que devuelva 200
                .andExpect(jsonPath("$.id").value(usuarioId)) // Verificamos el ID
                .andExpect(jsonPath("$.nickname").value("usuario1"))
                .andExpect(jsonPath("$.email").value("usuario1@email.com"))
                .andExpect(jsonPath("$.nombre").value("Nombre1"));
    }


    // ---------------------------------------------------------
    //           TEST Get by ID - 404
    // ---------------------------------------------------------
    @Test
    public void testListarUsuarioPorId_404_NoEncontrado() throws Exception {
        // --- Datos de prueba ---
        long usuarioId = 99L;

        // --- Configuramos el mock para lanzar la excepción ---
        when(usuarioService.buscarPorId(usuarioId)).thenThrow(new UsuarioNoEncontradoException());

        // --- Llamada al endpoint GET /usuarios/{id} ---
        mockMvc.perform(get("/usuarios/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Verificamos que devuelva 404
                .andExpect(jsonPath("$.message").value("El usuario no existe")); // Mensaje definido en GlobalExceptionHandler
    }


    // ---------------------------------------------------------
    //           TEST Get Todos - 200 (SIN FILTROS)
    // ---------------------------------------------------------
    @Test
    public void testListarUsuarios_200_VariosUsuarios() throws Exception {
        // --- Datos de prueba ---
        UsuarioOutDto u1 = new UsuarioOutDto();
        u1.setId(1L);
        u1.setNickname("user1");

        UsuarioOutDto u2 = new UsuarioOutDto();
        u2.setId(2L);
        u2.setNickname("user2");

        List<UsuarioOutDto> usuarios = List.of(u1, u2);

        // --- Configuramos el mock ---
        when(usuarioService.listarConFiltros(null, null, null, null)).thenReturn(usuarios);

        // --- Llamada al endpoint sin filtros ---
        mockMvc.perform(get("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nickname").value("user1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nickname").value("user2"));
    }

    // ---------------------------------------------------------
    //           TEST Get Todos - 200 (FILTRO POR NICKNAME)
    // ---------------------------------------------------------
    @Test
    public void testListarUsuarios_200_UnSoloUsuarioPorNickname() throws Exception {
        // --- Datos de prueba ---
        long usuarioId = 1L;
        UsuarioOutDto usuario = new UsuarioOutDto();
        usuario.setId(usuarioId);
        usuario.setNickname("user1");

        // --- Configuramos el mock para devolver un solo usuario ---
        when(usuarioService.listarConFiltros("user1", null, null, null)).thenReturn(List.of(usuario));

        // --- Llamada al endpoint con filtro nickname ---
        mockMvc.perform(get("/usuarios")
                        .param("nickname", "user1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId)) // Devuelve objeto, no array
                .andExpect(jsonPath("$.nickname").value("user1"));
    }


    // ---------------------------------------------------------
    //           TEST Get Todos - 404 (FILTRO POR NICKNAME)
    // ---------------------------------------------------------
    @Test
    public void testListarUsuarios_404_PorNicknameNoEncontrado() throws Exception {
        // --- Datos de prueba ---
        String nickname = "inexistente";

        // --- Configuramos el mock para lanzar excepción ---
        when(usuarioService.listarConFiltros(nickname, null, null, null))
                .thenThrow(new UsuarioNoEncontradoException());

        // --- Llamada al endpoint ---
        mockMvc.perform(get("/usuarios")
                        .param("nickname", nickname)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }

    // -------------------------------------------
    //           TEST PUT - 200
    // -------------------------------------------
    @Test
    public void testEditarUsuario_200_Exito() throws Exception {
        long usuarioId = 5;

        UsuarioUpdateDto updateDto = new UsuarioUpdateDto();
        updateDto.setNickname("nuevoNick");
        updateDto.setEmail("nuevo@email.com");
        updateDto.setNombre("NuevoNombre");

        UsuarioOutDto outDto = new UsuarioOutDto();
        outDto.setId(usuarioId);
        outDto.setNickname("nuevoNick");
        outDto.setEmail("nuevo@email.com");
        outDto.setNombre("NuevoNombre");

        // Simula que el servicio devuelve el usuario actualizado
        when(usuarioService.modificar(eq(usuarioId), any(UsuarioUpdateDto.class))).thenReturn(outDto);

        mockMvc.perform(put("/usuarios/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId))
                .andExpect(jsonPath("$.nickname").value("nuevoNick"))
                .andExpect(jsonPath("$.email").value("nuevo@email.com"))
                .andExpect(jsonPath("$.nombre").value("NuevoNombre"));
    }

    // -----------------------------------------------
    //           TEST PUT - 400 (nickname duplicado)
    // -----------------------------------------------
    @Test
    public void testEditarUsuario_400_NicknameDuplicado() throws Exception {
        long usuarioId = 5;

        UsuarioUpdateDto updateDto = new UsuarioUpdateDto();
        updateDto.setNickname("existenteNick"); // Nickname que ya existe

        // Simula que el servicio lanza excepción por nickname duplicado
        when(usuarioService.modificar(eq(usuarioId), any(UsuarioUpdateDto.class)))
                .thenThrow(new NicknameYaExisteException());

        mockMvc.perform(put("/usuarios/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("nickname-duplicado"))
                .andExpect(jsonPath("$.message").value("El nickname ya está en uso"));
    }

    // -------------------------------------------------
    //           TEST PUT - 404 (Usuario no encontrado)
    // -------------------------------------------------
    @Test
    public void testEditarUsuario_404_NoEncontrado() throws Exception {
        long usuarioId = 999;

        UsuarioUpdateDto updateDto = new UsuarioUpdateDto();
        updateDto.setNickname("nuevoNick");

        // Simula que el servicio lanza excepción por usuario no encontrado
        when(usuarioService.modificar(eq(usuarioId), any(UsuarioUpdateDto.class)))
                .thenThrow(new UsuarioNoEncontradoException());

        mockMvc.perform(put("/usuarios/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }


    // -------------------------------------------------
    //           TEST DELETE - 204
    // -------------------------------------------------
    @Test
    public void testEliminarUsuario_204_Exito() throws Exception {
        long usuarioId = 5;

        // Simulamos que el servicio no lanza ninguna excepción
        doNothing().when(usuarioService).eliminar(usuarioId);

        mockMvc.perform(delete("/usuarios/{id}", usuarioId))
                .andExpect(status().isNoContent());
    }

    // -------------------------------------------------
    //           TEST DELETE - 404
    // -------------------------------------------------
    @Test
    public void testEliminarUsuario_404_NoEncontrado() throws Exception {
        long usuarioId = 999;

        // Simulamos que el servicio lanza la excepción de usuario no encontrado
        doThrow(new UsuarioNoEncontradoException()).when(usuarioService).eliminar(usuarioId);

        mockMvc.perform(delete("/usuarios/{id}", usuarioId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El usuario no existe"));
    }
}
