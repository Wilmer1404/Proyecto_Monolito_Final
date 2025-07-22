package com.utplist.proyecto.service;

import com.utplist.proyecto.exception.FeatureFlagNotFoundException;
import com.utplist.proyecto.model.FeatureFlag;

public interface IFeatureFlagService {
    FeatureFlag getFeatureFlag(String nombre) throws FeatureFlagNotFoundException;
    boolean isEnabled(String nombre) throws FeatureFlagNotFoundException;
    void habilitar(String nombre);
} 