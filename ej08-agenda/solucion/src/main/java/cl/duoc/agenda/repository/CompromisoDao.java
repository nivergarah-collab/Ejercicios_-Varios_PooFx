package cl.duoc.agenda.repository;

import cl.duoc.agenda.model.Compromiso;

import java.util.List;

public interface CompromisoDao<T extends Compromiso> {
    List<T> leer();

    void guardar(List<T> compromisos);
}
