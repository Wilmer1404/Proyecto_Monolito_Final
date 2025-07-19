package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
    Optional<FeatureFlag> findByNombre(String nombre);
} 