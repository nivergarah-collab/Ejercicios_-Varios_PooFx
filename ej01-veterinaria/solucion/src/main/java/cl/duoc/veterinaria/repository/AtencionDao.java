package cl.duoc.veterinaria.repository;

import cl.duoc.veterinaria.model.Atencion;

import java.util.List;

public interface AtencionDao<T extends Atencion> {
    List<T> leer();

    void guardar(List<T> atenciones);
}
