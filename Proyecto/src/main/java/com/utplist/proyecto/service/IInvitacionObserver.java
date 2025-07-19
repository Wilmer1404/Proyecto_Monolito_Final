package com.utplist.proyecto.service;

import com.utplist.proyecto.model.Invitacion;

public interface IInvitacionObserver {
    void onUsuarioInvitado(Invitacion invitacion);
}
