package com.utplist.proyecto.service;

import com.utplist.proyecto.model.Invitacion;

/**
 * Interfaz para observar eventos de invitación de usuarios a documentos.
 * Permite reaccionar cuando un usuario es invitado.
 */
public interface IInvitacionObserver {
    /**
     * Método que se ejecuta cuando un usuario es invitado a un documento.
     * @param invitacion Invitación creada
     */
    void onUsuarioInvitado(Invitacion invitacion);
}
