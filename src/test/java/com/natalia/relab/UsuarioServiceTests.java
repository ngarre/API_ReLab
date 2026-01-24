package com.natalia.relab;

import com.natalia.relab.dto.UsuarioInDto;
import com.natalia.relab.dto.UsuarioOutDto;
import com.natalia.relab.dto.UsuarioUpdateDto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import com.natalia.relab.service.UsuarioService;
import exception.NicknameYaExisteException;
import exception.UsuarioNoEncontradoException;
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
                "usuario1",
                "pass1234",
                "Nombre1",
                "Apellido1",
                "email",
                LocalDate.of(1999, 4, 2),
                true,
                "empresa",
                false,
                100.0f,
                null,
                null,
                "Mi dirección"
        );

        // Esto mockea el resultado de volcar el DTO a la entidad con ModelMapper
        Usuario usuarioMapeado = new Usuario();

        // Aquí se mockea el usuario que ya está en la BBDD tras hacer el save y que se ha creado a partir de los datos del InDto.
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

        // Llamo al metodo a testear
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

    // ---------------------------------------------------------
    //           TEST GET by ID - buscarPorId()
    // ---------------------------------------------------------

    @Test
    public void testBuscarPorId_Exito() throws UsuarioNoEncontradoException {
        // Datos de ejemplo
        long idBuscado = 10;

        // Usuario que simula estar en la BBDD
        Usuario usuarioEnBBDD = new Usuario();
        usuarioEnBBDD.setId(idBuscado);
        usuarioEnBBDD.setNickname("usuario10");

        // UsuarioOutDto que se espera como resultado
        UsuarioOutDto outDto = new UsuarioOutDto();
        outDto.setId(idBuscado);
        outDto.setNickname("usuario10");

        // Defino el comportamiento de los mocks
        when(usuarioRepository.findById(idBuscado)).thenReturn(java.util.Optional.of(usuarioEnBBDD)); // Simula que el usuario se encuentra en la BBDD
        when(modelMapper.map(usuarioEnBBDD, UsuarioOutDto.class)).thenReturn(outDto); // Simula el mapeo a OutDto

        // Llamo al metodo a testear
        UsuarioOutDto resultado = usuarioService.buscarPorId(idBuscado);

        // Verificaciones (aserciones)
        assertNotNull(resultado); // Compruebo que el resultado no es nulo
        assertEquals(idBuscado, resultado.getId()); // Compruebo que el ID del OutDto es el esperado
        assertEquals("usuario10", resultado.getNickname()); // Compruebo que el nickname del OutDto es el esperado
    }

    @Test
    public void testBuscarPorId_FallaSiNoExiste() {
        long idInexistente = 999;

        when(usuarioRepository.findById(idInexistente)).thenReturn(java.util.Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, // Espero que se lance esta excepción
                () -> usuarioService.buscarPorId(idInexistente)); // Al llamar al metodo con un ID que no existe

    }

    // ---------------------------------------------------------
    //   TEST GET Todos o Aplicar Filtros - listarConFiltros()
    // ---------------------------------------------------------

    //-- FILTRADO por LOGIN (nickname + password) --//

    @Test
    public void testListarConFiltros_Login_Exito() throws UsuarioNoEncontradoException {
        // Datos de ejemplo
        String nickname = "usuario1";
        String password = "pass1234";

        // Usuario que simula estar en la BBDD
        Usuario usuarioEnBBDD = new Usuario();
        usuarioEnBBDD.setId(1);
        usuarioEnBBDD.setNickname(nickname);
        usuarioEnBBDD.setPassword(password);

        // UsuarioOutDto que se espera como resultado
        UsuarioOutDto outDto = new UsuarioOutDto();
        outDto.setId(1);
        outDto.setNickname(nickname);

        // Defino el comportamiento de los mocks
        when(usuarioRepository.findByNicknameAndPassword(nickname, password))
                .thenReturn(java.util.Optional.of(usuarioEnBBDD)); // Simula que el usuario se encuentra en la BBDD
        when(modelMapper.map(usuarioEnBBDD, UsuarioOutDto.class)).thenReturn(outDto); // Simula el mapeo a OutDto

        // Llamo al metodo a testear
        java.util.List<UsuarioOutDto> resultado = usuarioService.listarConFiltros(nickname, password, null, null); // Filtro por login

        // Verificaciones (aserciones)
        assertNotNull(resultado); // Compruebo que el resultado no es nulo
        assertEquals(1, resultado.size()); // Compruebo que se devuelve un solo usuario
        assertEquals(nickname, resultado.getFirst().getNickname()); // Compruebo que el nickname del OutDto es el esperado
    }

    @Test
    public void testListarConFiltros_Login_FallaSiNoExiste(){
        // Datos de ejemplo
        String nickname = "usuarioInexistente";
        String password = "pass1234";

        // Defino el comportamiento de los mocks
        when(usuarioRepository.findByNicknameAndPassword(nickname, password))
                .thenReturn(java.util.Optional.empty()); // Simula que el usuario NO se encuentra en la BBDD

        // Llamo al metodo a testear y espero que lance la excepción
        assertThrows(UsuarioNoEncontradoException.class,
                () -> usuarioService.listarConFiltros(nickname, password, null, null));
    }


    //-- FILTRADO por NICKNAME --//
    // No tiene caso de fallo porque si no hay usuarios con ese nickname, devuelve lista vacía.
    @Test
    public void testListarConFiltros_Nickname_Exito() throws UsuarioNoEncontradoException {
        String nickname = "usuario1";

        // Simulo un usuario que está en la BBDD
        Usuario usuarioEnBBDD = new Usuario();
        usuarioEnBBDD.setId(1);
        usuarioEnBBDD.setNickname(nickname);

        // UsuarioOutDto que se espera como resultado
        UsuarioOutDto outDto = new UsuarioOutDto();
        outDto.setId(1);
        outDto.setNickname(nickname);

        // Defino el comportamiento de los mocks
        when(usuarioRepository.findAll())
                .thenReturn(java.util.List.of(usuarioEnBBDD));
        when(modelMapper.map(usuarioEnBBDD, UsuarioOutDto.class))
                .thenReturn(outDto);

        List<UsuarioOutDto> resultado =
                usuarioService.listarConFiltros(nickname, null, null, null);

        // Verificaciones (aserciones)
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(nickname, resultado.getFirst().getNickname());
    }



    // -- FILTRADO por tipoUsuario -- //
    // No tiene caso de fallo porque si no hay usuarios de ese tipo, devuelve lista vacía.

    @Test
    public void testListarConFiltros_TipoUsuario() throws UsuarioNoEncontradoException {
        String tipoUsuario = "empresa";

        Usuario usuarioEnBBDD = new Usuario();
        usuarioEnBBDD.setId(1);
        usuarioEnBBDD.setTipoUsuario(tipoUsuario);

        UsuarioOutDto outDto = new UsuarioOutDto();
        outDto.setId(1);
        outDto.setTipoUsuario(tipoUsuario);

        when(usuarioRepository.findAll())
                .thenReturn(java.util.List.of(usuarioEnBBDD));
        when(modelMapper.map(usuarioEnBBDD, UsuarioOutDto.class))
                .thenReturn(outDto);

        List<UsuarioOutDto> resultado =
                usuarioService.listarConFiltros(null, null, tipoUsuario, null);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(tipoUsuario, resultado.getFirst().getTipoUsuario());
    }


    // -- FILTRADO por cuentaActiva -- //
    // No tiene caso de fallo porque si no hay usuarios con ese estado, devuelve lista vacía.

    @Test
    public void testListarConFiltros_CuentaActiva() throws UsuarioNoEncontradoException {
        boolean cuentaActiva = true;

        Usuario usuarioEnBBDD = new Usuario();
        usuarioEnBBDD.setId(1);
        usuarioEnBBDD.setCuentaActiva(cuentaActiva);

        UsuarioOutDto outDto = new UsuarioOutDto();
        outDto.setId(1);
        outDto.setCuentaActiva(cuentaActiva);

        when(usuarioRepository.findAll())
                .thenReturn(java.util.List.of(usuarioEnBBDD));
        when(modelMapper.map(usuarioEnBBDD, UsuarioOutDto.class))
                .thenReturn(outDto);

        List<UsuarioOutDto> resultado =
                usuarioService.listarConFiltros(null, null, null, cuentaActiva);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.getFirst().isCuentaActiva());
    }

    // -- FILTROS COMBINADOS -- //
    @Test
    public void testListarConFiltros_FiltrosCombinados_Exito() throws UsuarioNoEncontradoException {

        String nickname = "JuanPerez";
        String tipoUsuario = "empresa";
        boolean cuentaActiva = true;

        // Usuario que SÍ cumple todos los filtros
        Usuario u1 = new Usuario();
        u1.setId(1);
        u1.setNickname(nickname);
        u1.setTipoUsuario(tipoUsuario);
        u1.setCuentaActiva(true);

        // Usuario que NO cumple (cuenta inactiva)
        Usuario u2 = new Usuario();
        u2.setId(2);
        u2.setNickname(nickname);
        u2.setTipoUsuario(tipoUsuario);
        u2.setCuentaActiva(false);

        // Usuario que NO cumple (otro tipo)
        Usuario u3 = new Usuario();
        u3.setId(3);
        u3.setNickname(nickname);
        u3.setTipoUsuario("particular");
        u3.setCuentaActiva(true);


        // UsuarioOutDto que se espera como resultado
        UsuarioOutDto outDto = new UsuarioOutDto();
        outDto.setId(1);
        outDto.setNickname(nickname);
        outDto.setTipoUsuario(tipoUsuario);
        outDto.setCuentaActiva(true);

        // Defino el comportamiento de los mocks
        when(usuarioRepository.findAll()).thenReturn(java.util.List.of(u1, u2, u3));
        when(modelMapper.map(u1, UsuarioOutDto.class)).thenReturn(outDto);

        // Llamo al metodo a testear
        List<UsuarioOutDto> resultado =
                usuarioService.listarConFiltros(
                        nickname,
                        null,
                        tipoUsuario,
                        cuentaActiva
                );

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(nickname, resultado.getFirst().getNickname());
        assertEquals(tipoUsuario, resultado.getFirst().getTipoUsuario());
        assertTrue(resultado.getFirst().isCuentaActiva());
    }



    // -- SIN FILTROS --> Devuelve todos los usuarios -- //

    @Test
    public void testListarConFiltros_SinFiltros_Exito() throws UsuarioNoEncontradoException {

        // Usuarios que simulan estar en la BBDD
        Usuario usuario1 = new Usuario();
        usuario1.setId(1);
        Usuario usuario2 = new Usuario();
        usuario2.setId(2);

        // UsuarioOutDto que se esperan como resultado
        UsuarioOutDto outDto1 = new UsuarioOutDto();
        outDto1.setId(1);
        UsuarioOutDto outDto2 = new UsuarioOutDto();
        outDto2.setId(2);

        // Defino el comportamiento de los mocks
        when(usuarioRepository.findAll())
                .thenReturn(java.util.List.of(usuario1, usuario2)); // Simula que hay dos usuarios en la BBDD
        when(modelMapper.map(usuario1, UsuarioOutDto.class)).thenReturn(outDto1); // Simula el mapeo a OutDto
        when(modelMapper.map(usuario2, UsuarioOutDto.class)).thenReturn(outDto2); // Simula el mapeo a OutDto

        // Llamo al metodo a testear
        java.util.List<UsuarioOutDto> resultado = usuarioService.listarConFiltros(null, null, null, null); // Sin filtros

        // Verificaciones (aserciones)
        assertNotNull(resultado); // Compruebo que el resultado no es nulo
        assertEquals(2, resultado.size()); // Compruebo que se devuelven dos usuarios
    }

    // ---------------------------------------------------------
    //                  TEST PUT / modificar()
    // ---------------------------------------------------------

    @Test
    public void testModificar_Exito() throws UsuarioNoEncontradoException, NicknameYaExisteException {
        long idAModificar = 5;

        // UsuarioUpdateDto con los datos a modificar
        UsuarioUpdateDto updateDto = new UsuarioUpdateDto();
        updateDto.setNickname("nuevoNick");
        updateDto.setEmail("nuevo@email.com");
        updateDto.setNombre("NuevoNombre");

        // Usuario que simula estar en la BBDD antes de la modificación
        Usuario usuarioEnBBDD = new Usuario();
        usuarioEnBBDD.setId(5);
        usuarioEnBBDD.setNickname("viejoNick");
        usuarioEnBBDD.setEmail("viejo@email.com");
        usuarioEnBBDD.setNombre("ViejoNombre");

        // Usuario que simula estar en la BBDD después de la modificación
        Usuario usuarioModificado = new Usuario();
        usuarioModificado.setId(5);
        usuarioModificado.setNickname("nuevoNick");
        usuarioModificado.setEmail("nuevo@email.com");
        usuarioModificado.setNombre("NuevoNombre");

        // UsuarioOutDto que se espera como resultado
        UsuarioOutDto outDto = new UsuarioOutDto();
        outDto.setId(5);
        outDto.setNickname("nuevoNick");
        outDto.setEmail("nuevo@email.com");
        outDto.setNombre("NuevoNombre");

        // Defino el comportamiento de los mocks
        when(usuarioRepository.findById(idAModificar))
                .thenReturn(java.util.Optional.of(usuarioEnBBDD)); // Simula que el usuario se encuentra en la BBDD
        when(usuarioRepository.existsByNickname("nuevoNick"))
                .thenReturn(false); // Simula que el nuevo nickname NO está en uso por otro usuario
        lenient().doNothing().when(modelMapper).map(updateDto, usuarioEnBBDD); // Ignoramos el mapeo de updateDto sobre usuarioEnBBDD
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioModificado); // Simula guardar y devolver el usuario modificado


        // map(usuarioModificado → outDto)
        when(modelMapper.map(usuarioModificado, UsuarioOutDto.class))
                .thenReturn(outDto);

        // Llamo al metodo a testear
        UsuarioOutDto resultado = usuarioService.modificar(idAModificar, updateDto);

        // Verificaciones (aserciones)
        assertNotNull(resultado); // Compruebo que el resultado no es nulo
        assertEquals(idAModificar, resultado.getId()); // Compruebo que el ID del OutDto es el esperado
        assertEquals("nuevoNick", resultado.getNickname()); // Compruebo que el nickname del OutDto es el esperado
        assertEquals("nuevo@email.com", resultado.getEmail());
        assertEquals("NuevoNombre", resultado.getNombre());
    }

    @Test
    public void testModificar_FallaSiNoExiste() {
        long idInexistente = 999;

        // UsuarioUpdateDto con los datos a modificar
        UsuarioUpdateDto updateDto = new UsuarioUpdateDto();
        updateDto.setNickname("cualquierNick");

        // Defino el comportamiento de los mocks
        when(usuarioRepository.findById(idInexistente))
                .thenReturn(java.util.Optional.empty());

        // Verifico que se lanza la excepción al intentar modificar un usuario inexistente
        assertThrows(UsuarioNoEncontradoException.class,
                () -> usuarioService.modificar(idInexistente, updateDto));
    }


    // ---------------------------------------------------------
    //                TEST DELETE / eliminar()
    // ---------------------------------------------------------

    @Test
    public void testEliminar_Exito() throws UsuarioNoEncontradoException {
        long idAEliminar = 10;

        // Usuario que simula estar en la BBDD antes de la eliminación
        Usuario usuarioEnBBDD = new Usuario();
        usuarioEnBBDD.setId(idAEliminar);

        // Defino el comportamiento de los mocks
        when(usuarioRepository.findById(idAEliminar))
                .thenReturn(java.util.Optional.of(usuarioEnBBDD)); // Simula que el usuario se encuentra en la BBDD

        // Llamo al metodo a testear
        usuarioService.eliminar(idAEliminar);

        // Verificaciones
        verify(usuarioRepository, times(1)).delete(usuarioEnBBDD); // Verifico que se llamó a delete con el usuario correcto
    }

    @Test
    public void testEliminar_FallaSiNoExiste() {
        long idInexistente = 999;

        when(usuarioRepository.findById(idInexistente))
                .thenReturn(java.util.Optional.empty());

        // Verifico que se lanza la excepción al intentar eliminar un usuario inexistente
        assertThrows(UsuarioNoEncontradoException.class,
                () -> usuarioService.eliminar(idInexistente));

        // Verifico que no se llamó a delete
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }
}




