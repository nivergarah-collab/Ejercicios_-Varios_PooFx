package cl.duoc.torneo.repository;

import cl.duoc.torneo.model.Inscripcion;
import cl.duoc.torneo.model.InscripcionEquipo;
import cl.duoc.torneo.model.InscripcionIndividual;

import java.util.ArrayList;
import java.util.List;

/**
 * Mantiene en memoria las inscripciones y las deja siempre iguales a lo que hay
 * en equipos.json e individuales.json. El acceso a los archivos lo hace cada DAO.
 */
public class InscripcionRepository {
    public static final String ARCHIVO_EQUIPOS = "equipos.json";
    public static final String ARCHIVO_INDIVIDUALES = "individuales.json";

    private final InscripcionDao<InscripcionEquipo> equipoDao;
    private final InscripcionDao<InscripcionIndividual> individualDao;
    private final List<InscripcionEquipo> equipos = new ArrayList<>();
    private final List<InscripcionIndividual> individuales = new ArrayList<>();

    public InscripcionRepository() {
        this(new GsonInscripcionDao<>(ARCHIVO_EQUIPOS, InscripcionEquipo.class),
                new GsonInscripcionDao<>(ARCHIVO_INDIVIDUALES, InscripcionIndividual.class));
    }

    public InscripcionRepository(InscripcionDao<InscripcionEquipo> equipoDao,
                                 InscripcionDao<InscripcionIndividual> individualDao) {
        this.equipoDao = equipoDao;
        this.individualDao = individualDao;
    }

    /** Lee ambos archivos y, solo si ambos se leyeron bien, reemplaza la memoria. */
    public void cargar() {
        List<InscripcionEquipo> leidosEquipos = equipoDao.leer();
        List<InscripcionIndividual> leidosIndividuales = individualDao.leer();
        equipos.clear();
        equipos.addAll(leidosEquipos);
        individuales.clear();
        individuales.addAll(leidosIndividuales);
    }

    /** Devuelve una copia: quien llama no puede modificar las listas internas. */
    public List<Inscripcion> listar() {
        List<Inscripcion> todas = new ArrayList<>(equipos);
        todas.addAll(individuales);
        return todas;
    }

    /** Agrega y guarda; si el guardado falla, deshace el cambio en memoria y relanza el error. */
    public void registrar(Inscripcion inscripcion) {
        if (inscripcion instanceof InscripcionEquipo equipo) {
            equipos.add(equipo);
            try {
                equipoDao.guardar(equipos);
            } catch (PersistenciaException e) {
                equipos.remove(equipos.size() - 1);
                throw e;
            }
        } else if (inscripcion instanceof InscripcionIndividual individual) {
            individuales.add(individual);
            try {
                individualDao.guardar(individuales);
            } catch (PersistenciaException e) {
                individuales.remove(individuales.size() - 1);
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Tipo de inscripción no soportado");
        }
    }

    /** Quita y guarda; si el guardado falla, devuelve la inscripción a su lugar y relanza el error. */
    public boolean eliminar(Inscripcion inscripcion) {
        if (inscripcion instanceof InscripcionEquipo) {
            return eliminarDe(equipos, equipoDao, inscripcion);
        } else if (inscripcion instanceof InscripcionIndividual) {
            return eliminarDe(individuales, individualDao, inscripcion);
        }
        return false;
    }

    private <T extends Inscripcion> boolean eliminarDe(List<T> lista, InscripcionDao<T> dao,
                                                       Inscripcion inscripcion) {
        int posicion = indiceDe(lista, inscripcion);
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

    private static int indiceDe(List<? extends Inscripcion> lista, Inscripcion inscripcion) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == inscripcion) {
                return i;
            }
        }
        return -1;
    }
}
