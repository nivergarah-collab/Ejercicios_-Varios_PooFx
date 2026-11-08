package cl.duoc.turnos.repository;

import cl.duoc.turnos.model.Turno;
import cl.duoc.turnos.model.TurnoCancha;
import cl.duoc.turnos.model.TurnoSala;

import java.util.ArrayList;
import java.util.List;

/**
 * Mantiene en memoria los turnos y los deja siempre iguales a lo que hay
 * en canchas.json y salas.json. El acceso a los archivos lo hace cada DAO.
 */
public class TurnoRepository {
    public static final String ARCHIVO_CANCHAS = "canchas.json";
    public static final String ARCHIVO_SALAS = "salas.json";

    private final TurnoDao<TurnoCancha> canchaDao;
    private final TurnoDao<TurnoSala> salaDao;
    private final List<TurnoCancha> canchas = new ArrayList<>();
    private final List<TurnoSala> salas = new ArrayList<>();

    public TurnoRepository() {
        this(new GsonTurnoDao<>(ARCHIVO_CANCHAS, TurnoCancha.class),
                new GsonTurnoDao<>(ARCHIVO_SALAS, TurnoSala.class));
    }

    public TurnoRepository(TurnoDao<TurnoCancha> canchaDao, TurnoDao<TurnoSala> salaDao) {
        this.canchaDao = canchaDao;
        this.salaDao = salaDao;
    }

    /** Lee ambos archivos y, solo si ambos se leyeron bien, reemplaza la memoria. */
    public void cargar() {
        List<TurnoCancha> leidasCanchas = canchaDao.leer();
        List<TurnoSala> leidasSalas = salaDao.leer();
        canchas.clear();
        canchas.addAll(leidasCanchas);
        salas.clear();
        salas.addAll(leidasSalas);
    }

    /** Devuelve una copia: quien llama no puede modificar las listas internas. */
    public List<Turno> listar() {
        List<Turno> todos = new ArrayList<>(canchas);
        todos.addAll(salas);
        return todos;
    }

    public void registrar(Turno turno) {
        if (turno instanceof TurnoCancha cancha) {
            canchas.add(cancha);
            try {
                canchaDao.guardar(canchas);
            } catch (PersistenciaException e) {
                canchas.remove(canchas.size() - 1);
                throw e;
            }
        } else if (turno instanceof TurnoSala sala) {
            salas.add(sala);
            try {
                salaDao.guardar(salas);
            } catch (PersistenciaException e) {
                salas.remove(salas.size() - 1);
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Tipo de turno no soportado");
        }
    }

    public boolean eliminar(Turno turno) {
        if (turno instanceof TurnoCancha) {
            return eliminarDe(canchas, canchaDao, turno);
        } else if (turno instanceof TurnoSala) {
            return eliminarDe(salas, salaDao, turno);
        }
        return false;
    }

    private <T extends Turno> boolean eliminarDe(List<T> lista, TurnoDao<T> dao, Turno turno) {
        int posicion = indiceDe(lista, turno);
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

    private static int indiceDe(List<? extends Turno> lista, Turno turno) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == turno) {
                return i;
            }
        }
        return -1;
    }
}
