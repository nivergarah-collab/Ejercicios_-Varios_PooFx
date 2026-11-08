package cl.duoc.peliculas.repository;

import cl.duoc.peliculas.model.Funcion;
import cl.duoc.peliculas.model.FuncionRegular;
import cl.duoc.peliculas.model.FuncionPopular;

import java.util.ArrayList;
import java.util.List;

/**
 * Mantiene en memoria las funciones y las deja siempre iguales a lo que hay
 * en funciones_regulares.json y funciones_populares.json. El acceso a los archivos lo hace cada DAO.
 */
public class FuncionRepository {
    public static final String ARCHIVO_REGULARES = "funciones_regulares.json";
    public static final String ARCHIVO_POPULARES = "funciones_populares.json";

    private final FuncionDao<FuncionRegular> regularDao;
    private final FuncionDao<FuncionPopular> popularDao;
    private final List<FuncionRegular> regulares = new ArrayList<>();
    private final List<FuncionPopular> populares = new ArrayList<>();

    public FuncionRepository() {
        this(new GsonFuncionDao<>(ARCHIVO_REGULARES, FuncionRegular.class),
                new GsonFuncionDao<>(ARCHIVO_POPULARES, FuncionPopular.class));
    }

    public FuncionRepository(FuncionDao<FuncionRegular> regularDao, FuncionDao<FuncionPopular> popularDao) {
        this.regularDao = regularDao;
        this.popularDao = popularDao;
    }

    /** Lee ambos archivos y, solo si ambos se leyeron bien, reemplaza la memoria. */
    public void cargar() {
        List<FuncionRegular> leidasFuncionRegulars = regularDao.leer();
        List<FuncionPopular> leidasFuncionPopulares = popularDao.leer();
        regulares.clear();
        regulares.addAll(leidasFuncionRegulars);
        populares.clear();
        populares.addAll(leidasFuncionPopulares);
    }

    /** Devuelve una copia: quien llama no puede modificar las listas internas. */
    public List<Funcion> listar() {
        List<Funcion> todas = new ArrayList<>(regulares);
        todas.addAll(populares);
        return todas;
    }

    public void registrar(Funcion funcion) {
        if (funcion instanceof FuncionRegular regular) {
            regulares.add(regular);
            try {
                regularDao.guardar(regulares);
            } catch (PersistenciaException e) {
                regulares.remove(regulares.size() - 1);
                throw e;
            }
        } else if (funcion instanceof FuncionPopular popular) {
            populares.add(popular);
            try {
                popularDao.guardar(populares);
            } catch (PersistenciaException e) {
                populares.remove(populares.size() - 1);
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Tipo de función no soportado");
        }
    }

    public boolean eliminar(Funcion funcion) {
        if (funcion instanceof FuncionRegular) {
            return eliminarDe(regulares, regularDao, funcion);
        } else if (funcion instanceof FuncionPopular) {
            return eliminarDe(populares, popularDao, funcion);
        }
        return false;
    }

    private <T extends Funcion> boolean eliminarDe(List<T> lista, FuncionDao<T> dao, Funcion funcion) {
        int posicion = indiceDe(lista, funcion);
        if (posicion < 0) {
            return false;
        }
        T eliminada = lista.remove(posicion);
        try {
            dao.guardar(lista);
        } catch (PersistenciaException e) {
            lista.add(posicion, eliminada);
            throw e;
        }
        return true;
    }

    private static int indiceDe(List<? extends Funcion> lista, Funcion funcion) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == funcion) {
                return i;
            }
        }
        return -1;
    }
}
