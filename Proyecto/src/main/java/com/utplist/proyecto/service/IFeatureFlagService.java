package com.utplist.proyecto.service;

import com.utplist.proyecto.exception.FeatureFlagNotFoundException;
import com.utplist.proyecto.model.FeatureFlag;

/**
 * Interfaz de servicio para la gestión de feature flags (banderas de funcionalidad).
 * Define operaciones para consultar y habilitar flags.
 */
public interface IFeatureFlagService {
    /**
     * Obtiene una feature flag por su nombre.
     * @param nombre Nombre de la feature flag
     * @return FeatureFlag encontrada
     */
    FeatureFlag getFeatureFlag(String nombre) throws FeatureFlagNotFoundException;
    /**
     * Verifica si una feature flag está habilitada.
     * @param nombre Nombre de la feature flag
     * @return true si está habilitada, false si no
     */
    boolean isEnabled(String nombre) throws FeatureFlagNotFoundException;
    /**
     * Habilita una feature flag por su nombre.
     * @param nombre Nombre de la feature flag
     */
    void habilitar(String nombre);
} 