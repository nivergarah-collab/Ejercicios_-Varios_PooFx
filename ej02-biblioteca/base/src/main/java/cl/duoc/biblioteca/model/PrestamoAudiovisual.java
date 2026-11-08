package cl.duoc.biblioteca.model;

/**
 * Préstamo de material audiovisual (películas, documentales, etc.).
 * Tarifa base de $2.400; si el material dura más de 120 minutos, la tarifa sube un 20 %.
 */
public class PrestamoAudiovisual extends Prestamo {

    public static final int TARIFA_BASE = 2_400;
    public static final int RECARGO_DURACION_LARGA = 20;
    public static final int MINUTOS_DURACION_LARGA = 120;
    public static final int DURACION_MINIMA = 1;
    public static final int DURACION_MAXIMA = 600;

    private String formato;
    private int duracionMinutos;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public PrestamoAudiovisual() {
    }

    public PrestamoAudiovisual(String socio, int diasPrestamo, int materiales, String formato, int duracionMinutos) {
        super(socio, diasPrestamo, materiales);
        setFormato(formato);
        setDuracionMinutos(duracionMinutos);
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = validarTexto(formato, "El formato");
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        if (duracionMinutos < DURACION_MINIMA || duracionMinutos > DURACION_MAXIMA) {
            throw new IllegalArgumentException("La duración debe estar entre " + DURACION_MINIMA + " y "
                    + DURACION_MAXIMA + " minutos.");
        }
        this.duracionMinutos = duracionMinutos;
    }

    @Override
    public int calcularTarifa() {
        if (duracionMinutos > MINUTOS_DURACION_LARGA) {
            return TARIFA_BASE + TARIFA_BASE * RECARGO_DURACION_LARGA / 100;
        }
        return TARIFA_BASE;
    }

    @Override
    public String getTipo() {
        return "Audiovisual";
    }
}
