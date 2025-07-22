package com.utplist.proyecto.service.impl;

import com.utplist.proyecto.exception.FeatureFlagNotFoundException;
import com.utplist.proyecto.model.FeatureFlag;
import com.utplist.proyecto.repository.FeatureFlagRepository;
import com.utplist.proyecto.service.IFeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio para la gestión de feature flags (banderas de funcionalidad).
 * Permite consultar y habilitar flags en la base de datos.
 */
@Service
@RequiredArgsConstructor
public class FeatureFlagServiceImpl implements IFeatureFlagService {
    private final FeatureFlagRepository repository;

    /**
     * Obtiene una feature flag por su nombre.
     * @param nombre Nombre de la feature flag
     * @return FeatureFlag encontrada
     */
    @Override
    public FeatureFlag getFeatureFlag(String nombre) {
        return repository.findByNombre(nombre)
                .orElseThrow(() -> new FeatureFlagNotFoundException(
                        "No existe la feature flag: " + nombre));
    }

    /**
     * Verifica si una feature flag está habilitada.
     * @param nombre Nombre de la feature flag
     * @return true si está habilitada, false si no
     */
    @Override
    public boolean isEnabled(String nombre) {
        return getFeatureFlag(nombre).getHabilitado();
    }

    /**
     * Habilita una feature flag por su nombre. Si no existe, la crea.
     * @param nombre Nombre de la feature flag
     */
    @Override
    public void habilitar(String nombre) {
        try {
            FeatureFlag flag = getFeatureFlag(nombre);
            flag.setHabilitado(true);
            repository.save(flag);
        } catch (FeatureFlagNotFoundException e) {
            // Si no existe, crear la feature flag
            FeatureFlag newFlag = FeatureFlag.builder()
                    .nombre(nombre)
                    .habilitado(true)
                    .build();
            repository.save(newFlag);
        }
    }
} 