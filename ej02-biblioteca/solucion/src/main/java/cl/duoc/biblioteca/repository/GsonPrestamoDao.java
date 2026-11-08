package cl.duoc.biblioteca.repository;

import cl.duoc.biblioteca.model.Prestamo;
import cl.duoc.biblioteca.model.PrestamoAudiovisual;
import cl.duoc.biblioteca.model.PrestamoLibro;
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

/** Lee y escribe en un archivo JSON los préstamos de un solo tipo. */
public class GsonPrestamoDao<T extends Prestamo> implements PrestamoDao<T> {
    private final Path archivo;
    private final Type tipoLista;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GsonPrestamoDao(String archivo, Class<T> clase) {
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
            for (T prestamo : leidos) {
                validar(prestamo);
            }
            return leidos;
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_LECTURA), e);
        }
    }

    @Override
    public void guardar(List<T> prestamos) {
        try (Writer escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            gson.toJson(prestamos, tipoLista, escritor);
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_ESCRITURA), e);
        }
    }

    private String mensaje(String alerta) {
        return alerta + " " + archivo.getFileName() + ".";
    }

    /** Gson no llama a los setters: se revisan los datos leídos a mano. */
    private void validar(Prestamo prestamo) {
        try {
            if (prestamo == null) {
                throw new IllegalArgumentException("hay un registro vacío.");
            }
            prestamo.setSocio(prestamo.getSocio());
            prestamo.setDiasPrestamo(prestamo.getDiasPrestamo());
            prestamo.setMateriales(prestamo.getMateriales());
            if (prestamo instanceof PrestamoLibro libro) {
                libro.setTitulo(libro.getTitulo());
            }
            if (prestamo instanceof PrestamoAudiovisual audiovisual) {
                audiovisual.setFormato(audiovisual.getFormato());
                audiovisual.setDuracionMinutos(audiovisual.getDuracionMinutos());
            }
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException(
                    PersistenciaException.ALERTA_LECTURA + " " + archivo.getFileName()
                            + ": dato no válido, " + e.getMessage(), e);
        }
    }
}
