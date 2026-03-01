package com.natalia.relab.service;

import com.natalia.relab.dto.ServiciosInDto;
import com.natalia.relab.dto.ServiciosOutDto;
import com.natalia.relab.model.Servicios;
import com.natalia.relab.repository.ServiciosRepository;
import exception.ServicioNoEncontradoException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ServiciosService {

    private static final Logger log = LoggerFactory.getLogger(ServiciosService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ServiciosRepository serviciosRepository;

    // --- POST
    public ServiciosOutDto agregar(ServiciosInDto serviciosInDto){
        log.info("Intentando crear nuevo servicio");
        log.debug("Datos recibidos: {}", serviciosInDto);

        // Creo servicio
        Servicios servicios = modelMapper.map(serviciosInDto, Servicios.class);
        // Fecha automática del sistema
        servicios.setFecha(LocalDate.now());

        // Guardar y devolver DTO
        Servicios guardado  = serviciosRepository.save(servicios);

        log.info("Servicio creado con ID {}", guardado.getId());
        return mapToOutDto(guardado);
    }

    // --- GET todos los servicios
    public List<ServiciosOutDto> listar() {
        log.info("Intentando listar servicios");
        return serviciosRepository.findAll()
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // --- GET por idUsuario
    public List<ServiciosOutDto> buscarPorIdUsuario(long idUsuario) {
        log.info("Buscando servicios por ID de usuario {}", idUsuario);
        return serviciosRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(this::mapToOutDto)
                .toList();
    }

    //--- DELETE: Eliminar servicios por idUsuario
    // -- Este metodo se empleará desde la app de android
    public void eliminarPorIdUsuario(long idUsuario) throws ServicioNoEncontradoException {
        log.info("Eliminando servicios por ID de usuario {}", idUsuario);
        List<Servicios> servicios = serviciosRepository.findByIdUsuario(idUsuario);

        if (servicios.isEmpty()) {
            log.warn("No existen servicios para el usuario {}", idUsuario);
            throw new ServicioNoEncontradoException();
        }

        serviciosRepository.deleteAll(servicios);
        log.info("Servicios eliminados correctamente para el usuario {}", idUsuario);
    }

    // --- DELETE
    public void eliminar(long id) throws ServicioNoEncontradoException {
        log.warn("Intentando eliminar servicio con ID {}", id);
        Servicios servicios = serviciosRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Servicio con ID {} no encontrado para eliminación", id);
                    return new ServicioNoEncontradoException();
                });
        serviciosRepository.delete(servicios);
        log.info("Servicio eliminado con ID {}", id);
    }


    // --- Metodo auxiliar privado para mapear y no repetir código
    private ServiciosOutDto mapToOutDto(Servicios servicios) {
        return modelMapper.map(servicios, ServiciosOutDto.class);
    }
}
