package com.natalia.relab;

import com.natalia.relab.dto.UsuarioInDto;
import com.natalia.relab.dto.UsuarioOutDto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import com.natalia.relab.service.UsuarioService;
import exception.NicknameYaExisteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTests {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ModelMapper modelMapper;

    // ---------------------------------------------------------
    //           TEST POST - agregar()
    // ---------------------------------------------------------

    @Test
    public void testAgregar_Exito(){
        // Campos para UsuarioInDto
        UsuarioInDto usuarioInDto = new UsuarioInDto(
                "usuario1",                               // nickname
                "pass1234",                                        // password
                "Nombre1",                                         // nombre
                "Apellido1",                                       // apellido
                "email",                                           // email
                LocalDate.of(1999, 4, 2),   // fechaNacimiento
                true,                                              // cuentaActiva
                "empresa",                                         // tipoUsuario
                false,                                             // admin
                100.0f,                                            // saldo
                null,                                              // latitud
                null                                               // longitud
        );

        // Esto mockea el resultado de volcar el DTO a la entidad con ModelMapper
        Usuario usuarioMapeado = new Usuario();

        // Aquí se mockea el usuario que ya está en la BBDD tras hacer el save y que se ha crado a partir de los datos del InDto.
        Usuario guardado = new Usuario();
        guardado.setId(42);
        guardado.setNickname("usuario1");

        // Mockeamos un ejemplo de UsuarioOutDto que se devolvería tras mapear el usuario guardado
        UsuarioOutDto outDto = new UsuarioOutDto();
        outDto.setId(42);
        outDto.setNickname("usuario1");

        // Definimos el comportamiento de los mocks
        when(usuarioRepository.existsByNickname("usuario1")).thenReturn(false);
        when(modelMapper.map(usuarioInDto, Usuario.class)).thenReturn(usuarioMapeado);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(guardado);
        when(modelMapper.map(guardado, UsuarioOutDto.class)).thenReturn(outDto);

        // Llamamos al metodo a testear
        UsuarioOutDto resultado = usuarioService.agregar(usuarioInDto);

        // Verificaciones (aserciones)
        assertNotNull(resultado); // Compruebo que el resultado no es nulo
        assertEquals(42, resultado.getId()); // Compruebo que el ID del OutDto es el esperado
        assertEquals("usuario1", resultado.getNickname()); // Compruebo que el nickname del OutDto es el esperado
    }

    @Test
    public void testAgregar_FallaSiNicknameExiste() {
        UsuarioInDto inDto = new UsuarioInDto();
        inDto.setNickname("usuarioExistente");
        inDto.setPassword("abcd");

        when(usuarioRepository.existsByNickname("usuarioExistente")).thenReturn(true);

        assertThrows(NicknameYaExisteException.class, () -> usuarioService.agregar(inDto));

        // Aseguramos que no se intenta guardar nada si ya existe el nickname
        verify(usuarioRepository, never()).save(any());
    }
}




