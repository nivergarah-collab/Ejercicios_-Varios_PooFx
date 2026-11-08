package cl.duoc.torneo.repository;

import cl.duoc.torneo.model.Inscripcion;

import java.util.List;

/** Operaciones de lectura y escritura de las inscripciones de un tipo. */
public interface InscripcionDao<T extends Inscripcion> {
    List<T> leer();

    void guardar(List<T> inscripciones);
}
