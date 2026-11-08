package cl.duoc.playlist.repository;

import cl.duoc.playlist.model.Cancion;
import cl.duoc.playlist.model.Pista;
import cl.duoc.playlist.model.Podcast;

import java.util.ArrayList;
import java.util.List;

/**
 * Mantiene en memoria las pistas y las deja siempre iguales a lo que hay
 * en canciones.json y podcasts.json. El acceso a los archivos lo hace cada DAO.
 */
public class PistaRepository {
    public static final String ARCHIVO_CANCIONES = "canciones.json";
    public static final String ARCHIVO_PODCASTS = "podcasts.json";

    private final PistaDao<Cancion> cancionDao;
    private final PistaDao<Podcast> podcastDao;
    private final List<Cancion> canciones = new ArrayList<>();
    private final List<Podcast> podcasts = new ArrayList<>();

    public PistaRepository() {
        this(new GsonPistaDao<>(ARCHIVO_CANCIONES, Cancion.class),
                new GsonPistaDao<>(ARCHIVO_PODCASTS, Podcast.class));
    }

    public PistaRepository(PistaDao<Cancion> cancionDao, PistaDao<Podcast> podcastDao) {
        this.cancionDao = cancionDao;
        this.podcastDao = podcastDao;
    }

    /** Lee ambos archivos y, solo si ambos se leyeron bien, reemplaza la memoria. */
    public void cargar() {
        List<Cancion> leidasCanciones = cancionDao.leer();
        List<Podcast> leidosPodcasts = podcastDao.leer();
        canciones.clear();
        canciones.addAll(leidasCanciones);
        podcasts.clear();
        podcasts.addAll(leidosPodcasts);
    }

    /** Devuelve una copia: quien llama no puede modificar las listas internas. */
    public List<Pista> listar() {
        List<Pista> todas = new ArrayList<>(canciones);
        todas.addAll(podcasts);
        return todas;
    }

    public void registrar(Pista pista) {
        if (pista instanceof Cancion cancion) {
            canciones.add(cancion);
            try {
                cancionDao.guardar(canciones);
            } catch (PersistenciaException e) {
                canciones.remove(canciones.size() - 1);
                throw e;
            }
        } else if (pista instanceof Podcast podcast) {
            podcasts.add(podcast);
            try {
                podcastDao.guardar(podcasts);
            } catch (PersistenciaException e) {
                podcasts.remove(podcasts.size() - 1);
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Tipo de pista no soportado");
        }
    }

    public boolean eliminar(Pista pista) {
        if (pista instanceof Cancion) {
            return eliminarDe(canciones, cancionDao, pista);
        } else if (pista instanceof Podcast) {
            return eliminarDe(podcasts, podcastDao, pista);
        }
        return false;
    }

    private <T extends Pista> boolean eliminarDe(List<T> lista, PistaDao<T> dao, Pista pista) {
        int posicion = indiceDe(lista, pista);
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

    private static int indiceDe(List<? extends Pista> lista, Pista pista) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == pista) {
                return i;
            }
        }
        return -1;
    }
}
