package cl.duoc.peliculas.repository;

import cl.duoc.peliculas.model.Funcion;

import java.util.List;

public interface FuncionDao<T extends Funcion> {
    List<T> leer();

    void guardar(List<T> funciones);
}
