package cl.duoc.agenda.model;

/**
 * Compromiso de la agenda de la oficina. Clase abstracta: se registra siempre
 * como Reunión o como Evento.
 *
 * Reglas comunes: título no vacío, duración entre 15 y 480 minutos y
 * asistentes entre 1 y 200.
 */
public abstract class Compromiso {

    public static final int DURACION_MINIMA = 15;
    public static final int DURACION_MAXIMA = 480;
    public static final int ASISTENTES_MINIMOS = 1;
    public static final int ASISTENTES_MAXIMOS = 200;

    private String titulo;
    private int duracionMin;
    private int asistentes;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    protected Compromiso() {
    }

    protected Compromiso(String titulo, int duracionMin, int asistentes) {
        setTitulo(titulo);
        setDuracionMin(duracionMin);
        setAsistentes(asistentes);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = validarTitulo(titulo);
    }

    public int getDuracionMin() {
        return duracionMin;
    }

    public void setDuracionMin(int duracionMin) {
        this.duracionMin = validarDuracion(duracionMin);
    }

    public int getAsistentes() {
        return asistentes;
    }

    public void setAsistentes(int asistentes) {
        this.asistentes = validarAsistentes(asistentes);
    }

    /** Costo por asistente, en pesos, según las reglas de cada tipo de compromiso. */
    public abstract int calcularCostoPorAsistente();

    /** "Reunión" o "Evento": se muestra en la tabla sin preguntar por la clase concreta. */
    public abstract String getTipo();

    protected static String validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        return titulo.trim();
    }

    protected static int validarDuracion(int duracionMin) {
        if (duracionMin < DURACION_MINIMA || duracionMin > DURACION_MAXIMA) {
            throw new IllegalArgumentException("La duración debe estar entre " + DURACION_MINIMA + " y "
                    + DURACION_MAXIMA + " minutos.");
        }
        return duracionMin;
    }

    protected static int validarAsistentes(int asistentes) {
        if (asistentes < ASISTENTES_MINIMOS || asistentes > ASISTENTES_MAXIMOS) {
            throw new IllegalArgumentException("El número de asistentes debe estar entre " + ASISTENTES_MINIMOS
                    + " y " + ASISTENTES_MAXIMOS + ".");
        }
        return asistentes;
    }

    /** Formato de pesos chilenos: $7.500. */
    public static String pesos(int monto) {
        return String.format("$%,d", monto).replace(',', '.');
    }

    @Override
    public String toString() {
        return getTipo() + " | " + titulo + " | " + duracionMin + " min | " + asistentes + " asist. | "
                + pesos(calcularCostoPorAsistente());
    }
}
