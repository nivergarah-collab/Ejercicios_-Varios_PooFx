package cl.duoc.biblioteca.repository;

import cl.duoc.biblioteca.model.Prestamo;

import java.util.List;

public interface PrestamoDao<T extends Prestamo> {
    List<T> leer();

    void guardar(List<T> prestamos);
}
