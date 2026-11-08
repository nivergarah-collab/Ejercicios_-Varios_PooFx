package cl.duoc.torneo.repository;

import cl.duoc.torneo.model.Inscripcion;
import cl.duoc.torneo.model.InscripcionEquipo;
import cl.duoc.torneo.model.InscripcionIndividual;
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

/** Lee y escribe en un archivo JSON las inscripciones de un solo tipo. */
public class GsonInscripcionDao<T extends Inscripcion> implements InscripcionDao<T> {
    private final Path archivo;
    private final Type tipoLista;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GsonInscripcionDao(String archivo, Class<T> clase) {
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
            for (T inscripcion : leidas) {
                validar(inscripcion);
            }
            return leidas;
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_LECTURA), e);
        }
    }

    @Override
    public void guardar(List<T> inscripciones) {
        try (Writer escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            gson.toJson(inscripciones, tipoLista, escritor);
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_ESCRITURA), e);
        }
    }

    private String mensaje(String alerta) {
        return alerta + " " + archivo.getFileName() + ".";
    }

    /** Gson no llama a los setters: se revisan los datos leídos a mano. */
    private void validar(Inscripcion inscripcion) {
        try {
            if (inscripcion == null) {
                throw new IllegalArgumentException("hay un registro vacío.");
            }
            inscripcion.setNombre(inscripcion.getNombre());
            inscripcion.setPartidosJugados(inscripcion.getPartidosJugados());
            inscripcion.setRondas(inscripcion.getRondas());
            if (inscripcion instanceof InscripcionEquipo equipo) {
                equipo.setCapitan(equipo.getCapitan());
            }
            if (inscripcion instanceof InscripcionIndividual individual) {
                individual.setRanking(individual.getRanking());
                individual.setAniosFederado(individual.getAniosFederado());
            }
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException(
                    PersistenciaException.ALERTA_LECTURA + " " + archivo.getFileName()
                            + ": dato no válido. " + e.getMessage(), e);
        }
    }
}
