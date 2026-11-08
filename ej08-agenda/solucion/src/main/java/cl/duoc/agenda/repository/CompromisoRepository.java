package cl.duoc.agenda.repository;

import cl.duoc.agenda.model.Compromiso;
import cl.duoc.agenda.model.Evento;
import cl.duoc.agenda.model.Reunion;

import java.util.ArrayList;
import java.util.List;

/**
 * Mantiene en memoria los compromisos y las deja siempre iguales a lo que hay
 * en reuniones.json y eventos.json. El acceso a los archivos lo hace cada DAO.
 */
public class CompromisoRepository {
    public static final String ARCHIVO_REUNIONES = "reuniones.json";
    public static final String ARCHIVO_EVENTOS = "eventos.json";

    private final CompromisoDao<Reunion> reunionDao;
    private final CompromisoDao<Evento> eventoDao;
    private final List<Reunion> reuniones = new ArrayList<>();
    private final List<Evento> eventos = new ArrayList<>();

    public CompromisoRepository() {
        this(new GsonCompromisoDao<>(ARCHIVO_REUNIONES, Reunion.class),
                new GsonCompromisoDao<>(ARCHIVO_EVENTOS, Evento.class));
    }

    public CompromisoRepository(CompromisoDao<Reunion> reunionDao, CompromisoDao<Evento> eventoDao) {
        this.reunionDao = reunionDao;
        this.eventoDao = eventoDao;
    }

    /** Lee ambos archivos y, solo si ambos se leyeron bien, reemplaza la memoria. */
    public void cargar() {
        List<Reunion> leidasReuniones = reunionDao.leer();
        List<Evento> leidosEventos = eventoDao.leer();
        reuniones.clear();
        reuniones.addAll(leidasReuniones);
        eventos.clear();
        eventos.addAll(leidosEventos);
    }

    /** Devuelve una copia: quien llama no puede modificar las listas internas. */
    public List<Compromiso> listar() {
        List<Compromiso> todas = new ArrayList<>(reuniones);
        todas.addAll(eventos);
        return todas;
    }

    public void registrar(Compromiso compromiso) {
        if (compromiso instanceof Reunion reunion) {
            reuniones.add(reunion);
            try {
                reunionDao.guardar(reuniones);
            } catch (PersistenciaException e) {
                reuniones.remove(reuniones.size() - 1);
                throw e;
            }
        } else if (compromiso instanceof Evento evento) {
            eventos.add(evento);
            try {
                eventoDao.guardar(eventos);
            } catch (PersistenciaException e) {
                eventos.remove(eventos.size() - 1);
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Tipo de compromiso no soportado");
        }
    }

    public boolean eliminar(Compromiso compromiso) {
        if (compromiso instanceof Reunion) {
            return eliminarDe(reuniones, reunionDao, compromiso);
        } else if (compromiso instanceof Evento) {
            return eliminarDe(eventos, eventoDao, compromiso);
        }
        return false;
    }

    private <T extends Compromiso> boolean eliminarDe(List<T> lista, CompromisoDao<T> dao, Compromiso compromiso) {
        int posicion = indiceDe(lista, compromiso);
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

    private static int indiceDe(List<? extends Compromiso> lista, Compromiso compromiso) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == compromiso) {
                return i;
            }
        }
        return -1;
    }
}
