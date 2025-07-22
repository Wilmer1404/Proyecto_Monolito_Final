package com.utplist.proyecto.observer;

import com.utplist.proyecto.model.Invitacion;
import com.utplist.proyecto.service.IInvitacionObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que implementa el patrón Observer para notificar a los observadores cuando se crea una invitación.
 */
@Component
public class InvitacionNotifier {

    private final List<IInvitacionObserver> observers = new ArrayList<>();

    @Autowired
    public InvitacionNotifier(List<IInvitacionObserver> observerList) {
        this.observers.addAll(observerList); // Spring inyecta todos los beans que implementan InvitacionObserver
    }

    /**
     * Notifica a todos los observadores registrados sobre una nueva invitación.
     * @param invitacion Invitación creada
     */
    public void notificarInvitacion(Invitacion invitacion) {
        observers.forEach(observer -> observer.onUsuarioInvitado(invitacion));
    }
}
