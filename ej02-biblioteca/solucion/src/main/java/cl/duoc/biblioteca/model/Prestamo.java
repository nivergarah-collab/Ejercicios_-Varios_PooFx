package cl.duoc.biblioteca.model;

/**
 * Préstamo de material de la biblioteca. Clase abstracta: se registra siempre
 * como PrestamoLibro o PrestamoAudiovisual.
 *
 * Reglas comunes: socio no vacío, plazo de préstamo entre 1 y 30 días y
 * materiales prestados entre 1 y 5.
 */
public abstract class Prestamo {

    public static final int DIAS_MINIMOS = 1;
    public static final int DIAS_MAXIMOS = 30;
    public static final int MATERIALES_MINIMOS = 1;
    public static final int MATERIALES_MAXIMOS = 5;

    private String socio;
    private int diasPrestamo;
    private int materiales;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    protected Prestamo() {
    }

    protected Prestamo(String socio, int diasPrestamo, int materiales) {
        setSocio(socio);
        setDiasPrestamo(diasPrestamo);
        setMateriales(materiales);
    }

    public String getSocio() {
        return socio;
    }

    public void setSocio(String socio) {
        this.socio = validarTexto(socio, "El nombre del socio");
    }

    public int getDiasPrestamo() {
        return diasPrestamo;
    }

    public void setDiasPrestamo(int diasPrestamo) {
        this.diasPrestamo = validarDias(diasPrestamo);
    }

    public int getMateriales() {
        return materiales;
    }

    public void setMateriales(int materiales) {
        this.materiales = validarMateriales(materiales);
    }

    /** Tarifa de préstamo de un material, en pesos, según las reglas de cada tipo de préstamo. */
    public abstract int calcularTarifa();

    /** "Libro" o "Audiovisual": se muestra en la tabla sin preguntar por la clase concreta. */
    public abstract String getTipo();

    /** Texto obligatorio: no puede ser nulo ni estar vacío o solo con espacios. */
    protected static String validarTexto(String texto, String nombre) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(nombre + " no puede estar vacío.");
        }
        return texto.trim();
    }

    protected static int validarDias(int dias) {
        if (dias < DIAS_MINIMOS || dias > DIAS_MAXIMOS) {
            throw new IllegalArgumentException("El plazo de préstamo debe estar entre " + DIAS_MINIMOS + " y "
                    + DIAS_MAXIMOS + " días.");
        }
        return dias;
    }

    protected static int validarMateriales(int materiales) {
        if (materiales < MATERIALES_MINIMOS || materiales > MATERIALES_MAXIMOS) {
            throw new IllegalArgumentException("Los materiales prestados deben estar entre " + MATERIALES_MINIMOS
                    + " y " + MATERIALES_MAXIMOS + ".");
        }
        return materiales;
    }

    /** Formato de pesos chilenos: $2.250. */
    public static String pesos(int monto) {
        return String.format("$%,d", monto).replace(',', '.');
    }

    @Override
    public String toString() {
        return getTipo() + " | " + socio + " | " + diasPrestamo + " días | " + materiales + " mat. | "
                + pesos(calcularTarifa());
    }
}
