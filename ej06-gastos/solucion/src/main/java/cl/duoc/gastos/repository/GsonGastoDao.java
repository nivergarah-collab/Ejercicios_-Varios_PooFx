package cl.duoc.gastos.repository;

import cl.duoc.gastos.model.Gasto;
import cl.duoc.gastos.model.GastoSalida;
import cl.duoc.gastos.model.GastoServicio;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Lee y escribe en un archivo JSON los gastos de un solo tipo. */
public class GsonGastoDao<T extends Gasto> implements GastoDao<T> {
    private final Path archivo;
    private final Type tipoLista;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GsonGastoDao(String archivo, Class<T> clase) {
        this.archivo = Path.of(archivo);
        this.tipoLista = TypeToken.getParameterized(List.class, clase).getType();
    }

    @Override
    public List<T> leer() {
        if (Files.notExists(archivo)) {
            return new ArrayList<>();
        }
        try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            List<T> leidas = gson.fromJson(lector, tipoLista);
            if (leidas == null) {
                return new ArrayList<>();
            }
            for (T gasto : leidas) {
                validar(gasto);
            }
            return leidas;
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_LECTURA), e);
        }
    }

    @Override
    public void guardar(List<T> gastos) {
        try (Writer escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            gson.toJson(gastos, tipoLista, escritor);
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_ESCRITURA), e);
        }
    }

    private String mensaje(String alerta) {
        return alerta + " " + archivo.getFileName() + ".";
    }

    /** Gson no llama a los setters: se revisan los datos leídos a mano. */
    private void validar(Gasto gasto) {
        try {
            if (gasto == null) {
                throw new IllegalArgumentException("hay un registro vacío.");
            }
            gasto.setDescripcion(gasto.getDescripcion());
            gasto.setMontoTotal(gasto.getMontoTotal());
            gasto.setParticipantes(gasto.getParticipantes());
            if (gasto instanceof GastoSalida salida) {
                salida.setLugar(salida.getLugar());
            }
            if (gasto instanceof GastoServicio servicio) {
                servicio.setProveedor(servicio.getProveedor());
                servicio.setDiasDeAnticipo(servicio.getDiasDeAnticipo());
            }
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException(
                    PersistenciaException.ALERTA_LECTURA + " " + archivo.getFileName()
                            + ": dato no válido, " + e.getMessage(), e);
        }
    }
}
