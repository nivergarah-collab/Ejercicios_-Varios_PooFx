package cl.duoc.gastos.repository;

import cl.duoc.gastos.model.Gasto;
import cl.duoc.gastos.model.GastoSalida;
import cl.duoc.gastos.model.GastoServicio;

import java.util.ArrayList;
import java.util.List;

/**
 * Mantiene en memoria los gastos y las deja siempre iguales a lo que hay
 * en salidas.json y servicios.json. El acceso a los archivos lo hace cada DAO.
 */
public class GastoRepository {
    public static final String ARCHIVO_SALIDAS = "salidas.json";
    public static final String ARCHIVO_SERVICIOS = "servicios.json";

    private final GastoDao<GastoSalida> salidaDao;
    private final GastoDao<GastoServicio> servicioDao;
    private final List<GastoSalida> salidas = new ArrayList<>();
    private final List<GastoServicio> servicios = new ArrayList<>();

    public GastoRepository() {
        this(new GsonGastoDao<>(ARCHIVO_SALIDAS, GastoSalida.class),
                new GsonGastoDao<>(ARCHIVO_SERVICIOS, GastoServicio.class));
    }

    public GastoRepository(GastoDao<GastoSalida> salidaDao, GastoDao<GastoServicio> servicioDao) {
        this.salidaDao = salidaDao;
        this.servicioDao = servicioDao;
    }

    /** Lee ambos archivos y, solo si ambos se leyeron bien, reemplaza la memoria. */
    public void cargar() {
        List<GastoSalida> leidasSalidas = salidaDao.leer();
        List<GastoServicio> leidasServicios = servicioDao.leer();
        salidas.clear();
        salidas.addAll(leidasSalidas);
        servicios.clear();
        servicios.addAll(leidasServicios);
    }

    /** Devuelve una copia: quien llama no puede modificar las listas internas. */
    public List<Gasto> listar() {
        List<Gasto> todas = new ArrayList<>(salidas);
        todas.addAll(servicios);
        return todas;
    }

    public void registrar(Gasto gasto) {
        if (gasto instanceof GastoSalida salida) {
            salidas.add(salida);
            try {
                salidaDao.guardar(salidas);
            } catch (PersistenciaException e) {
                salidas.remove(salidas.size() - 1);
                throw e;
            }
        } else if (gasto instanceof GastoServicio servicio) {
            servicios.add(servicio);
            try {
                servicioDao.guardar(servicios);
            } catch (PersistenciaException e) {
                servicios.remove(servicios.size() - 1);
                throw e;
            }
        } else {
            throw new IllegalArgumentException("Tipo de gasto no soportado");
        }
    }

    public boolean eliminar(Gasto gasto) {
        if (gasto instanceof GastoSalida) {
            return eliminarDe(salidas, salidaDao, gasto);
        } else if (gasto instanceof GastoServicio) {
            return eliminarDe(servicios, servicioDao, gasto);
        }
        return false;
    }

    private <T extends Gasto> boolean eliminarDe(List<T> lista, GastoDao<T> dao, Gasto gasto) {
        int posicion = indiceDe(lista, gasto);
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

    private static int indiceDe(List<? extends Gasto> lista, Gasto gasto) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == gasto) {
                return i;
            }
        }
        return -1;
    }
}
