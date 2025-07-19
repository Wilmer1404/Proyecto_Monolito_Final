package com.utplist.proyecto.service.impl;

import com.utplist.proyecto.exception.FeatureFlagNotFoundException;
import com.utplist.proyecto.model.FeatureFlag;
import com.utplist.proyecto.repository.FeatureFlagRepository;
import com.utplist.proyecto.service.IFeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeatureFlagServiceImpl implements IFeatureFlagService {
    private final FeatureFlagRepository repository;
    @Override
    public FeatureFlag getFeatureFlag(String nombre) {
        return repository.findByNombre(nombre)
                .orElseThrow(() -> new FeatureFlagNotFoundException(
                        "No existe la feature flag: " + nombre));
    }
    @Override
    public boolean isEnabled(String nombre) {
        return getFeatureFlag(nombre).getHabilitado();
    }

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