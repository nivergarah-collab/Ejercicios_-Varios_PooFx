package cl.duoc.playlist.model;

/**
 * Podcast. Participa en la sincronización con el dispositivo para escucharlo sin conexión.
 * Licencia de $400 por minuto; si es uno de los primeros 10 episodios
 * (ciclo inicial), la licencia baja un 25 %.
 */
public class Podcast extends Pista implements Sincronizable {

    public static final String TIPO = "Podcast";
    public static final int COSTO_POR_MINUTO = 400;
    public static final int DESCUENTO_CICLO_INICIAL = 25;
    public static final int ULTIMO_EPISODIO_INICIAL = 10;
    public static final int EPISODIO_MINIMO = 1;
    public static final int EPISODIO_MAXIMO = 500;

    private int episodio;
    private boolean sincronizado;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public Podcast() {
    }

    public Podcast(String titulo, String artista, int duracionMin, int episodio) {
        super(titulo, artista, duracionMin);
        setEpisodio(episodio);
        this.sincronizado = false;
    }

    public int getEpisodio() {
        return episodio;
    }

    public void setEpisodio(int episodio) {
        if (episodio < EPISODIO_MINIMO || episodio > EPISODIO_MAXIMO) {
            throw new IllegalArgumentException("El número de episodio debe estar entre " + EPISODIO_MINIMO
                    + " y " + EPISODIO_MAXIMO + ".");
        }
        this.episodio = episodio;
    }

    @Override
    public boolean estaSincronizado() {
        return sincronizado;
    }

    @Override
    public void marcarSincronizado() {
        this.sincronizado = true;
    }

    @Override
    public int calcularCostoLicencia() {
        int base = COSTO_POR_MINUTO * getDuracionMin();
        if (episodio <= ULTIMO_EPISODIO_INICIAL) {
            return base - base * DESCUENTO_CICLO_INICIAL / 100;
        }
        return base;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
