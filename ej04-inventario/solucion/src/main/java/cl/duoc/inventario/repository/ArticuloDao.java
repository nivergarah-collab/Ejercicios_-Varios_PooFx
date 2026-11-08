package cl.duoc.inventario.repository;

import cl.duoc.inventario.model.Articulo;

import java.util.List;

public interface ArticuloDao<T extends Articulo> {
    List<T> leer();

    void guardar(List<T> articulos);
}
