package cl.duoc.biblioteca.repository;

import cl.duoc.biblioteca.model.Prestamo;
import cl.duoc.biblioteca.model.PrestamoAudiovisual;
import cl.duoc.biblioteca.model.PrestamoLibro;

import java.util.ArrayList;
import java.util.List;

/**
 * Mantiene en memoria los préstamos y los deja siempre iguales a lo que hay
 * en prestamos-libros.json y prestamos-audiovisuales.json. El acceso a los
 * archivos lo hace cada DAO.
 */
public class PrestamoRepository {
    public static final String ARCHIVO_LIBROS = "prestamos-libros.json";
    public static final String ARCHIVO_AUDIOVISUALES = "prestamos-audiovisuales.json";

    private final PrestamoDao<PrestamoLibro> libroDao;
    private final PrestamoDao<PrestamoAudiovisual> audiovisualDao;
    private final List<PrestamoLibro> libros = new ArrayList<>();
    private final List<PrestamoAudiovisual> audiovisuales = new ArrayList<>();

    public PrestamoRepository() {
        this(new GsonPrestamoDao<>(ARCHIVO_LIBROS, PrestamoLibro.class),
                new GsonPrestamoDao<>(ARCHIVO_AUDIOVISUALES, PrestamoAudiovisual.class));
    }

    public PrestamoRepository(PrestamoDao<PrestamoLibro> libroDao, PrestamoDao<PrestamoAudiovisual> audiovisualDao) {
        this.libroDao = libroDao;
        this.audiovisualDao = audiovisualDao;
    }

    /** Lee ambos archivos y, solo si ambos se leyeron bien, reemplaza la memoria. */
    public void cargar() {
        List<PrestamoLibro> leidosLibros = libroDao.leer();
        List<PrestamoAudiovisual> leidosAudiovisuales = audiovisualDao.leer();
        libros.clear();
        libros.addAll(leidosLibros);
        audiovisuales.clear();
        audiovisuales.addAll(leidosAudiovisuales);
    }

    /** Devuelve una copia: quien llama no puede modificar las listas internas. */
    public List<Prestamo> listar() {
        List<Prestamo> todos = new ArrayList<>(libros);
        todos.addAll(audiovisuales);
        return todos;
    }

    public void registrar(Prestamo prestamo) {
        if (prestamo instanceof PrestamoLibro libro) {
            libros.add(libro);
            try {
                libroDao.guardar(libros);
            } catch (PersistenciaException e) {
                libros.remove(libros.size() - 1);
                throw e;
            }
        } else if (prestamo instanceof PrestamoAudiovisual audiovisual) {
            audiovisuales.add(audiovisual);
            try {
                audiovisualDao.guardar(audiovisuales);
            } catch (PersistenciaException e) {
                audiovisuales.remove(audiovisuales.size() - 1);
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Tipo de préstamo no soportado");
        }
    }

    public boolean eliminar(Prestamo prestamo) {
        if (prestamo instanceof PrestamoLibro) {
            return eliminarDe(libros, libroDao, prestamo);
        } else if (prestamo instanceof PrestamoAudiovisual) {
            return eliminarDe(audiovisuales, audiovisualDao, prestamo);
        }
        return false;
    }

    private <T extends Prestamo> boolean eliminarDe(List<T> lista, PrestamoDao<T> dao, Prestamo prestamo) {
        int posicion = indiceDe(lista, prestamo);
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

    private static int indiceDe(List<? extends Prestamo> lista, Prestamo prestamo) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == prestamo) {
                return i;
            }
        }
        return -1;
    }
}
