package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.ReporteDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReporteDocumentoRepository extends JpaRepository<ReporteDocumento, Long> {
    List<ReporteDocumento> findByEstado(ReporteDocumento.EstadoReporte estado);
    List<ReporteDocumento> findByDocumentoId(Long documentoId);
} 