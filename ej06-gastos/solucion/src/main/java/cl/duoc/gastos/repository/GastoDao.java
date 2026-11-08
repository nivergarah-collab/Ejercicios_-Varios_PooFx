package cl.duoc.gastos.repository;

import cl.duoc.gastos.model.Gasto;

import java.util.List;

public interface GastoDao<T extends Gasto> {
    List<T> leer();

    void guardar(List<T> gastos);
}
