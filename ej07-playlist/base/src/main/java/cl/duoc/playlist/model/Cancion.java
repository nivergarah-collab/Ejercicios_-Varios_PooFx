package cl.duoc.playlist.model;

/**
 * Canción. Licencia de $700 por minuto; si es un estreno, la licencia sube un 30 %.
 */
public class Cancion extends Pista {

    public static final String TIPO = "Canción";
    public static final int COSTO_POR_MINUTO = 700;
    public static final int RECARGO_ESTRENO = 30;

    private String album;
    private boolean estreno;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public Cancion() {
    }

    public Cancion(String titulo, String artista, int duracionMin, String album, boolean estreno) {
        super(titulo, artista, duracionMin);
        setAlbum(album);
        setEstreno(estreno);
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        if (album == null || album.isBlank()) {
            throw new IllegalArgumentException("El nombre del álbum no puede estar vacío.");
        }
        this.album = album.trim();
    }

    public boolean isEstreno() {
        return estreno;
    }

    public void setEstreno(boolean estreno) {
        this.estreno = estreno;
    }

    @Override
    public int calcularCostoLicencia() {
        int base = COSTO_POR_MINUTO * getDuracionMin();
        if (estreno) {
            return base + base * RECARGO_ESTRENO / 100;
        }
        return base;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
