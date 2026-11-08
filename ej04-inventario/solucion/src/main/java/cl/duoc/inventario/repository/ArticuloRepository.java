package cl.duoc.inventario.repository;

import cl.duoc.inventario.model.Articulo;
import cl.duoc.inventario.model.Herramienta;
import cl.duoc.inventario.model.Pintura;

import java.util.ArrayList;
import java.util.List;

/**
 * Mantiene en memoria los artículos de la bodega y los deja siempre iguales a lo que hay
 * en pinturas.json y herramientas.json. El acceso a los archivos lo hace cada DAO.
 */
public class ArticuloRepository {
    public static final String ARCHIVO_PINTURAS = "pinturas.json";
    public static final String ARCHIVO_HERRAMIENTAS = "herramientas.json";

    private final ArticuloDao<Pintura> pinturaDao;
    private final ArticuloDao<Herramienta> herramientaDao;
    private final List<Pintura> pinturas = new ArrayList<>();
    private final List<Herramienta> herramientas = new ArrayList<>();

    public ArticuloRepository() {
        this(new GsonArticuloDao<>(ARCHIVO_PINTURAS, Pintura.class),
                new GsonArticuloDao<>(ARCHIVO_HERRAMIENTAS, Herramienta.class));
    }

    public ArticuloRepository(ArticuloDao<Pintura> pinturaDao, ArticuloDao<Herramienta> herramientaDao) {
        this.pinturaDao = pinturaDao;
        this.herramientaDao = herramientaDao;
    }

    /** Lee ambos archivos y, solo si ambos se leyeron bien, reemplaza la memoria. */
    public void cargar() {
        List<Pintura> leidasPinturas = pinturaDao.leer();
        List<Herramienta> leidasHerramientas = herramientaDao.leer();
        pinturas.clear();
        pinturas.addAll(leidasPinturas);
        herramientas.clear();
        herramientas.addAll(leidasHerramientas);
    }

    /** Devuelve una copia: quien llama no puede modificar las listas internas. */
    public List<Articulo> listar() {
        List<Articulo> todos = new ArrayList<>(pinturas);
        todos.addAll(herramientas);
        return todos;
    }

    public void registrar(Articulo articulo) {
        if (articulo instanceof Pintura pintura) {
            agregarA(pinturas, pinturaDao, pintura);
        } else if (articulo instanceof Herramienta herramienta) {
            agregarA(herramientas, herramientaDao, herramienta);
        } else {
            throw new IllegalArgumentException("Tipo de artículo no soportado");
        }
    }

    public boolean eliminar(Articulo articulo) {
        if (articulo instanceof Pintura) {
            return eliminarDe(pinturas, pinturaDao, articulo);
        } else if (articulo instanceof Herramienta) {
            return eliminarDe(herramientas, herramientaDao, articulo);
        }
        return false;
    }

    /** Agrega y guarda; si guardar falla, deshace el cambio en memoria y relanza el error. */
    private <T extends Articulo> void agregarA(List<T> lista, ArticuloDao<T> dao, T articulo) {
        lista.add(articulo);
        try {
            dao.guardar(lista);
        } catch (PersistenciaException e) {
            lista.remove(lista.size() - 1);
            throw e;
        }
    }

    private <T extends Articulo> boolean eliminarDe(List<T> lista, ArticuloDao<T> dao, Articulo articulo) {
        int posicion = indiceDe(lista, articulo);
        if (posicion < 0) {
            return false;
        }
        T eliminado = lista.remove(posicion);
        try {
            dao.guardar(lista);
        } catch (PersistenciaException e) {
            lista.add(posicion, eliminado);
            throw e;
        }
        return true;
    }

    private static int indiceDe(List<? extends Articulo> lista, Articulo articulo) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == articulo) {
                return i;
            }
        }
        return -1;
    }
}
