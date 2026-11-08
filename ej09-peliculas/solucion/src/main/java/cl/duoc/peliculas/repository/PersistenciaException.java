package cl.duoc.peliculas.repository;

public class PersistenciaException extends RuntimeException {
    public static final String ALERTA_LECTURA = "Error al leer archivo";
    public static final String ALERTA_ESCRITURA = "Error al escribir archivo";

    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }
}
