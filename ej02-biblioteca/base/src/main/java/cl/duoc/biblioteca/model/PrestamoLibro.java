package cl.duoc.biblioteca.model;

/**
 * Préstamo de un libro. Se puede renovar.
 * Tarifa base de $1.800; si el libro es una novedad, la tarifa sube un 25 %.
 */
public class PrestamoLibro extends Prestamo implements Renovable {

    public static final int TARIFA_BASE = 1_800;
    public static final int RECARGO_NOVEDAD = 25;

    private String titulo;
    private boolean novedad;
    private boolean renovado;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public PrestamoLibro() {
    }

    public PrestamoLibro(String socio, int diasPrestamo, int materiales, String titulo, boolean novedad) {
        super(socio, diasPrestamo, materiales);
        setTitulo(titulo);
        setNovedad(novedad);
        this.renovado = false;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = validarTexto(titulo, "El título del libro");
    }

    public boolean isNovedad() {
        return novedad;
    }

    public void setNovedad(boolean novedad) {
        this.novedad = novedad;
    }

    @Override
    public boolean estaRenovado() {
        return renovado;
    }

    @Override
    public void renovar() {
        this.renovado = true;
    }

    @Override
    public int calcularTarifa() {
        if (novedad) {
            return TARIFA_BASE + TARIFA_BASE * RECARGO_NOVEDAD / 100;
        }
        return TARIFA_BASE;
    }

    @Override
    public String getTipo() {
        return "Libro";
    }
}
