-- Insertar feature flags necesarias
INSERT INTO feature_flags (nombre, habilitado) 
VALUES 
    ('invitaciones', true),
    ('suscripciones', true),
    ('solicitudes_edicion', true)
ON CONFLICT (nombre) 
DO UPDATE SET habilitado = EXCLUDED.habilitado; 