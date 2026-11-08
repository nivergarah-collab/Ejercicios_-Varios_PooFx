package cl.duoc.peliculas.repository;

import cl.duoc.peliculas.model.Funcion;
import cl.duoc.peliculas.model.FuncionRegular;
import cl.duoc.peliculas.model.FuncionPopular;
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

/** Lee y escribe en un archivo JSON las funciones de un solo tipo. */
public class GsonFuncionDao<T extends Funcion> implements FuncionDao<T> {
    private final Path archivo;
    private final Type tipoLista;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GsonFuncionDao(String archivo, Class<T> clase) {
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
            for (T funcion : leidas) {
                validar(funcion);
            }
            return leidas;
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_LECTURA), e);
        }
    }

    @Override
    public void guardar(List<T> funciones) {
        try (Writer escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            gson.toJson(funciones, tipoLista, escritor);
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_ESCRITURA), e);
        }
    }

    private String mensaje(String alerta) {
        return alerta + " " + archivo.getFileName() + ".";
    }

    /** Gson no llama a los setters: se revisan los datos leídos a mano. */
    private void validar(Funcion funcion) {
        try {
            if (funcion == null) {
                throw new IllegalArgumentException("hay un registro vacío.");
            }
            funcion.setPelicula(funcion.getPelicula());
            funcion.setDuracionMin(funcion.getDuracionMin());
            funcion.setEntradas(funcion.getEntradas());
            if (funcion instanceof FuncionRegular regular) {
                regular.setSala(regular.getSala());
            }
            if (funcion instanceof FuncionPopular popular) {
                popular.setDistribuidora(popular.getDistribuidora());
                popular.setDiasEnCartelera(popular.getDiasEnCartelera());
            }
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException(
                    PersistenciaException.ALERTA_LECTURA + " " + archivo.getFileName()
                            + ": dato no válido, " + e.getMessage(), e);
        }
    }
}
