package com.natalia.relab;

import com.natalia.relab.controller.UsuarioController;
import com.natalia.relab.dto.*;
import com.natalia.relab.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

class UsuarioControllerTests {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private Authentication authentication;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
    }

    @Test
    void testMiPerfil() throws Exception {
        Long userId = 1L;
        when(authentication.getPrincipal()).thenReturn(userId);

        UsuarioMobileOutDto dto = new UsuarioMobileOutDto();
        dto.setId(userId);
        dto.setNickname("natalia");

        when(usuarioService.obtenerPerfil(userId)).thenReturn(dto);

        mockMvc.perform(get("/usuarios/me").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.nickname").value("natalia"));

        verify(usuarioService, times(1)).obtenerPerfil(userId);
    }

    @Test
    void testCheckNickname() throws Exception {
        String nickname = "natalia";
        when(usuarioService.nicknameExiste(nickname)).thenReturn(true);

        mockMvc.perform(get("/usuarios/check-nickname")
                        .param("nickname", nickname))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(usuarioService, times(1)).nicknameExiste(nickname);
    }

    @Test
    void testListarTodos() throws Exception {
        UsuarioOutDto user1 = new UsuarioOutDto();
        user1.setId(1L);
        user1.setNickname("natalia");

        when(usuarioService.listarConFiltros(null, null, null, null))
                .thenReturn(List.of(user1));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nickname").value("natalia"));

        verify(usuarioService, times(1)).listarConFiltros(null, null, null, null);
    }

    @Test
    void testAgregarUsuario() throws Exception {
        UsuarioInDto inDto = new UsuarioInDto();
        inDto.setNickname("nuevo");
        inDto.setPassword("1234");       // Obligatorio y >=4 caracteres
        inDto.setSaldo(0f);              // Obligatorio >=0

        UsuarioOutDto outDto = new UsuarioOutDto();
        outDto.setId(2L);
        outDto.setNickname("nuevo");

        when(usuarioService.agregar(inDto)).thenReturn(outDto);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nickname").value("nuevo"));

        verify(usuarioService, times(1)).agregar(inDto);
    }

    @Test
    void testEditarUsuarioAutorizado() throws Exception {
        Long id = 1L;
        when(authentication.getPrincipal()).thenReturn(id);

        UsuarioUpdateDto updateDto = new UsuarioUpdateDto();
        updateDto.setNickname("editado");

        UsuarioOutDto outDto = new UsuarioOutDto();
        outDto.setId(id);
        outDto.setNickname("editado");

        when(usuarioService.modificar(id, updateDto)).thenReturn(outDto);

        mockMvc.perform(put("/usuarios/{id}", id)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nickname").value("editado"));

        verify(usuarioService, times(1)).modificar(id, updateDto);
    }

    @Test
    void testEditarUsuarioNoAutorizado() throws Exception {
        Long id = 1L;
        Long otroId = 2L;
        when(authentication.getPrincipal()).thenReturn(otroId);

        UsuarioUpdateDto updateDto = new UsuarioUpdateDto();
        updateDto.setNickname("editado");

        mockMvc.perform(put("/usuarios/{id}", id)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());

        verify(usuarioService, never()).modificar(anyLong(), any());
    }

    @Test
    void testEliminarUsuarioAutorizado() throws Exception {
        Long id = 1L;
        when(authentication.getPrincipal()).thenReturn(id);

        doNothing().when(usuarioService).eliminar(id);

        mockMvc.perform(delete("/usuarios/{id}", id).principal(authentication))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).eliminar(id);
    }

    @Test
    void testEliminarUsuarioNoAutorizado() throws Exception {
        Long id = 1L;
        Long otroId = 2L;
        when(authentication.getPrincipal()).thenReturn(otroId);

        mockMvc.perform(delete("/usuarios/{id}", id).principal(authentication))
                .andExpect(status().isForbidden());

        verify(usuarioService, never()).eliminar(anyLong());
    }

    @Test
    void testEliminarCuentaAutorizado() throws Exception {
        Long id = 1L;
        when(authentication.getPrincipal()).thenReturn(id);

        doNothing().when(usuarioService).eliminarCuenta(id);

        mockMvc.perform(delete("/usuarios/{id}/cuenta", id).principal(authentication))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).eliminarCuenta(id);
    }

    @Test
    void testEliminarCuentaNoAutorizado() throws Exception {
        Long id = 1L;
        Long otroId = 2L;
        when(authentication.getPrincipal()).thenReturn(otroId);

        mockMvc.perform(delete("/usuarios/{id}/cuenta", id).principal(authentication))
                .andExpect(status().isForbidden());

        verify(usuarioService, never()).eliminarCuenta(anyLong());
    }
}