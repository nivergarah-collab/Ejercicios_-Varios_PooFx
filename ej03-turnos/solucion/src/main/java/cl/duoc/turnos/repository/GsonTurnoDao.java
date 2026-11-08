package cl.duoc.turnos.repository;

import cl.duoc.turnos.model.Turno;
import cl.duoc.turnos.model.TurnoCancha;
import cl.duoc.turnos.model.TurnoSala;
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

/** Lee y escribe en un archivo JSON los turnos de un solo tipo. */
public class GsonTurnoDao<T extends Turno> implements TurnoDao<T> {
    private final Path archivo;
    private final Type tipoLista;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GsonTurnoDao(String archivo, Class<T> clase) {
        this.archivo = Path.of(archivo);
        this.tipoLista = TypeToken.getParameterized(List.class, clase).getType();
    }

    @Override
    public List<T> leer() {
        if (Files.notExists(archivo)) {
            return new ArrayList<>();
        }
        try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            List<T> leidos = gson.fromJson(lector, tipoLista);
            if (leidos == null) {
                return new ArrayList<>();
            }
            for (T turno : leidos) {
                validar(turno);
            }
            return leidos;
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_LECTURA), e);
        }
    }

    @Override
    public void guardar(List<T> turnos) {
        try (Writer escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            gson.toJson(turnos, tipoLista, escritor);
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_ESCRITURA), e);
        }
    }

    private String mensaje(String alerta) {
        return alerta + " " + archivo.getFileName() + ".";
    }

    /** Gson no llama a los setters: se revisan los datos leídos a mano. */
    private void validar(Turno turno) {
        try {
            if (turno == null) {
                throw new IllegalArgumentException("hay un registro vacío.");
            }
            turno.setCliente(turno.getCliente());
            turno.setHoraInicio(turno.getHoraInicio());
            turno.setHoras(turno.getHoras());
            if (turno instanceof TurnoCancha cancha) {
                cancha.setNumeroCancha(cancha.getNumeroCancha());
            }
            if (turno instanceof TurnoSala sala) {
                sala.setAsistentes(sala.getAsistentes());
                sala.setDiasAnticipacion(sala.getDiasAnticipacion());
            }
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException(
                    PersistenciaException.ALERTA_LECTURA + " " + archivo.getFileName()
                            + ": dato no válido, " + e.getMessage(), e);
        }
    }
}
