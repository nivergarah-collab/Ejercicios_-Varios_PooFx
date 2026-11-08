package cl.duoc.playlist.model;

/**
 * Pista de la playlist. Clase abstracta: se registra siempre como Canción
 * o Podcast.
 *
 * Reglas comunes: título e intérprete no vacíos y duración entre 1 y 240 minutos.
 */
public abstract class Pista {

    public static final int DURACION_MINIMA = 1;
    public static final int DURACION_MAXIMA = 240;

    private String titulo;
    private String artista;
    private int duracionMin;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    protected Pista() {
    }

    protected Pista(String titulo, String artista, int duracionMin) {
        setTitulo(titulo);
        setArtista(artista);
        setDuracionMin(duracionMin);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = validarTitulo(titulo);
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = validarArtista(artista);
    }

    public int getDuracionMin() {
        return duracionMin;
    }

    public void setDuracionMin(int duracionMin) {
        this.duracionMin = validarDuracion(duracionMin);
    }

    /** Costo de la licencia de la pista, en pesos, según las reglas de cada tipo. */
    public abstract int calcularCostoLicencia();

    /** "Canción" o "Podcast": se muestra en la tabla sin preguntar por la clase concreta. */
    public abstract String getTipo();

    protected static String validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de la pista no puede estar vacío.");
        }
        return titulo.trim();
    }

    protected static String validarArtista(String artista) {
        if (artista == null || artista.isBlank()) {
            throw new IllegalArgumentException("El nombre del artista no puede estar vacío.");
        }
        return artista.trim();
    }

    protected static int validarDuracion(int duracionMin) {
        if (duracionMin < DURACION_MINIMA || duracionMin > DURACION_MAXIMA) {
            throw new IllegalArgumentException("La duración debe estar entre " + DURACION_MINIMA + " y "
                    + DURACION_MAXIMA + " minutos.");
        }
        return duracionMin;
    }

    /** Formato de pesos chilenos: $3.640. */
    public static String pesos(int monto) {
        return String.format("$%,d", monto).replace(',', '.');
    }

    @Override
    public String toString() {
        return getTipo() + " | " + titulo + " | " + artista + " | " + duracionMin + " min | "
                + pesos(calcularCostoLicencia());
    }
}
