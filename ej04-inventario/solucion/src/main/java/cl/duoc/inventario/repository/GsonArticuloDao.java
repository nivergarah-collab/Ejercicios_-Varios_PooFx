package cl.duoc.inventario.repository;

import cl.duoc.inventario.model.Articulo;
import cl.duoc.inventario.model.Herramienta;
import cl.duoc.inventario.model.Pintura;
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

/** Lee y escribe en un archivo JSON los artículos de un solo tipo. */
public class GsonArticuloDao<T extends Articulo> implements ArticuloDao<T> {
    private final Path archivo;
    private final Type tipoLista;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GsonArticuloDao(String archivo, Class<T> clase) {
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
            for (T articulo : leidos) {
                validar(articulo);
            }
            return leidos;
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_LECTURA), e);
        }
    }

    @Override
    public void guardar(List<T> articulos) {
        try (Writer escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            gson.toJson(articulos, tipoLista, escritor);
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException(mensaje(PersistenciaException.ALERTA_ESCRITURA), e);
        }
    }

    private String mensaje(String alerta) {
        return alerta + " " + archivo.getFileName() + ".";
    }

    /** Gson no llama a los setters: se revisan los datos leídos a mano, usando los del modelo. */
    private void validar(Articulo articulo) {
        try {
            if (articulo == null) {
                throw new IllegalArgumentException("hay un registro vacío.");
            }
            articulo.setNombre(articulo.getNombre());
            articulo.setStock(articulo.getStock());
            articulo.setAnaquel(articulo.getAnaquel());
            if (articulo instanceof Pintura pintura) {
                pintura.setLitrosEnvase(pintura.getLitrosEnvase());
            }
            if (articulo instanceof Herramienta herramienta) {
                herramienta.setMarca(herramienta.getMarca());
                herramienta.setMesesEnBodega(herramienta.getMesesEnBodega());
            }
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException(
                    PersistenciaException.ALERTA_LECTURA + " " + archivo.getFileName()
                            + ": dato no válido, " + e.getMessage(), e);
        }
    }
}
