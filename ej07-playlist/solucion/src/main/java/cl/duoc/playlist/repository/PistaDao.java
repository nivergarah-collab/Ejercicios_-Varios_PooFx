package cl.duoc.playlist.repository;

import cl.duoc.playlist.model.Pista;

import java.util.List;

public interface PistaDao<T extends Pista> {
    List<T> leer();

    void guardar(List<T> pistas);
}
