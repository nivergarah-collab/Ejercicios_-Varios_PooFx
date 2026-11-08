package cl.duoc.agenda.repository;

import cl.duoc.agenda.model.Compromiso;
import cl.duoc.agenda.model.Reunion;
import cl.duoc.agenda.model.Evento;
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

/** Lee y escribe en un archivo JSON los compromisos de un solo tipo. */
public class GsonCompromisoDao<T extends Compromiso> implements CompromisoDao<T> {
    private final Path archivo;
    private final Type tipoLista;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GsonCompromisoDao(String archivo, Class<T> clase) {
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
            for (T compromiso : leidas) {
                validar(compromiso);
            }
            return leidas;
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_LECTURA), e);
        }
    }

    @Override
    public void guardar(List<T> compromisos) {
        try (Writer escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            gson.toJson(compromisos, tipoLista, escritor);
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_ESCRITURA), e);
        }
    }

    private String mensaje(String alerta) {
        return alerta + " " + archivo.getFileName() + ".";
    }

    /** Gson no llama a los setters: se revisan los datos leídos a mano. */
    private void validar(Compromiso compromiso) {
        try {
            if (compromiso == null) {
                throw new IllegalArgumentException("hay un registro vacío.");
            }
            compromiso.setTitulo(compromiso.getTitulo());
            compromiso.setDuracionMin(compromiso.getDuracionMin());
            compromiso.setAsistentes(compromiso.getAsistentes());
            if (compromiso instanceof Reunion reunion) {
                reunion.setOrganizador(reunion.getOrganizador());
            }
            if (compromiso instanceof Evento evento) {
                evento.setLugar(evento.getLugar());
                evento.setDiasAnticipacion(evento.getDiasAnticipacion());
            }
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException(
                    PersistenciaException.ALERTA_LECTURA + " " + archivo.getFileName()
                            + ": dato no válido, " + e.getMessage(), e);
        }
    }
}
