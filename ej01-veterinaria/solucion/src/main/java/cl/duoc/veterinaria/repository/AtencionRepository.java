package cl.duoc.veterinaria.repository;

import cl.duoc.veterinaria.model.Atencion;
import cl.duoc.veterinaria.model.Consulta;
import cl.duoc.veterinaria.model.Vacunacion;

import java.util.ArrayList;
import java.util.List;

/**
 * Mantiene en memoria las atenciones y las deja siempre iguales a lo que hay
 * en consultas.json y vacunaciones.json. El acceso a los archivos lo hace cada DAO.
 */
public class AtencionRepository {
    public static final String ARCHIVO_CONSULTAS = "consultas.json";
    public static final String ARCHIVO_VACUNACIONES = "vacunaciones.json";

    private final AtencionDao<Consulta> consultaDao;
    private final AtencionDao<Vacunacion> vacunacionDao;
    private final List<Consulta> consultas = new ArrayList<>();
    private final List<Vacunacion> vacunaciones = new ArrayList<>();

    public AtencionRepository() {
        this(new GsonAtencionDao<>(ARCHIVO_CONSULTAS, Consulta.class),
                new GsonAtencionDao<>(ARCHIVO_VACUNACIONES, Vacunacion.class));
    }

    public AtencionRepository(AtencionDao<Consulta> consultaDao, AtencionDao<Vacunacion> vacunacionDao) {
        this.consultaDao = consultaDao;
        this.vacunacionDao = vacunacionDao;
    }

    /** Lee ambos archivos y, solo si ambos se leyeron bien, reemplaza la memoria. */
    public void cargar() {
        List<Consulta> leidasConsultas = consultaDao.leer();
        List<Vacunacion> leidasVacunaciones = vacunacionDao.leer();
        consultas.clear();
        consultas.addAll(leidasConsultas);
        vacunaciones.clear();
        vacunaciones.addAll(leidasVacunaciones);
    }

    /** Devuelve una copia: quien llama no puede modificar las listas internas. */
    public List<Atencion> listar() {
        List<Atencion> todas = new ArrayList<>(consultas);
        todas.addAll(vacunaciones);
        return todas;
    }

    public void registrar(Atencion atencion) {
        if (atencion instanceof Consulta consulta) {
            consultas.add(consulta);
            try {
                consultaDao.guardar(consultas);
            } catch (PersistenciaException e) {
                consultas.remove(consultas.size() - 1);
                throw e;
            }
        } else if (atencion instanceof Vacunacion vacunacion) {
            vacunaciones.add(vacunacion);
            try {
                vacunacionDao.guardar(vacunaciones);
            } catch (PersistenciaException e) {
                vacunaciones.remove(vacunaciones.size() - 1);
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Tipo de atención no soportado");
        }
    }

    public boolean eliminar(Atencion atencion) {
        if (atencion instanceof Consulta) {
            return eliminarDe(consultas, consultaDao, atencion);
        } else if (atencion instanceof Vacunacion) {
            return eliminarDe(vacunaciones, vacunacionDao, atencion);
        }
        return false;
    }

    private <T extends Atencion> boolean eliminarDe(List<T> lista, AtencionDao<T> dao, Atencion atencion) {
        int posicion = indiceDe(lista, atencion);
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

    private static int indiceDe(List<? extends Atencion> lista, Atencion atencion) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == atencion) {
                return i;
            }
        }
        return -1;
    }
}
