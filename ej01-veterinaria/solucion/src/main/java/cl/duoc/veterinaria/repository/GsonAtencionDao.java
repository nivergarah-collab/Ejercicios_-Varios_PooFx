package cl.duoc.veterinaria.repository;

import cl.duoc.veterinaria.model.Atencion;
import cl.duoc.veterinaria.model.Consulta;
import cl.duoc.veterinaria.model.Vacunacion;
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

/** Lee y escribe en un archivo JSON las atenciones de un solo tipo. */
public class GsonAtencionDao<T extends Atencion> implements AtencionDao<T> {
    private final Path archivo;
    private final Type tipoLista;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GsonAtencionDao(String archivo, Class<T> clase) {
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
            for (T atencion : leidas) {
                validar(atencion);
            }
            return leidas;
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_LECTURA), e);
        }
    }

    @Override
    public void guardar(List<T> atenciones) {
        try (Writer escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            gson.toJson(atenciones, tipoLista, escritor);
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_ESCRITURA), e);
        }
    }

    private String mensaje(String alerta) {
        return alerta + " " + archivo.getFileName() + ".";
    }

    /** Gson no llama a los setters: se revisan los datos leídos a mano. */
    private void validar(Atencion atencion) {
        try {
            if (atencion == null) {
                throw new IllegalArgumentException("hay un registro vacío.");
            }
            atencion.setPaciente(atencion.getPaciente());
            atencion.setPesoKg(atencion.getPesoKg());
            atencion.setSesiones(atencion.getSesiones());
            if (atencion instanceof Consulta consulta) {
                consulta.setVeterinario(consulta.getVeterinario());
            }
            if (atencion instanceof Vacunacion vacunacion) {
                vacunacion.setNumeroDosis(vacunacion.getNumeroDosis());
                vacunacion.setMesesDesdeUltimaDosis(vacunacion.getMesesDesdeUltimaDosis());
            }
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException(
                    PersistenciaException.ALERTA_LECTURA + " " + archivo.getFileName()
                            + ": dato no válido, " + e.getMessage(), e);
        }
    }
}
