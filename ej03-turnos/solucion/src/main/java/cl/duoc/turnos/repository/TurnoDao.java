package cl.duoc.turnos.repository;

import cl.duoc.turnos.model.Turno;

import java.util.List;

public interface TurnoDao<T extends Turno> {
    List<T> leer();

    void guardar(List<T> turnos);
}
