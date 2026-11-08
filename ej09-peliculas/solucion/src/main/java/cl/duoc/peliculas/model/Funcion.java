package cl.duoc.peliculas.model;

/**
 * Función de cine. Clase abstracta: se registra siempre como función regular
 * o como función popular.
 *
 * Reglas comunes: película no vacía, duración entre 60 y 240 minutos y
 * entradas vendidas entre 1 y 300.
 */
public abstract class Funcion {

    public static final int DURACION_MINIMA = 60;
    public static final int DURACION_MAXIMA = 240;
    public static final int ENTRADAS_MINIMAS = 1;
    public static final int ENTRADAS_MAXIMAS = 300;

    private String pelicula;
    private int duracionMin;
    private int entradas;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    protected Funcion() {
    }

    protected Funcion(String pelicula, int duracionMin, int entradas) {
        setPelicula(pelicula);
        setDuracionMin(duracionMin);
        setEntradas(entradas);
    }

    public String getPelicula() {
        return pelicula;
    }

    public void setPelicula(String pelicula) {
        this.pelicula = validarPelicula(pelicula);
    }

    public int getDuracionMin() {
        return duracionMin;
    }

    public void setDuracionMin(int duracionMin) {
        this.duracionMin = validarDuracion(duracionMin);
    }

    public int getEntradas() {
        return entradas;
    }

    public void setEntradas(int entradas) {
        this.entradas = validarEntradas(entradas);
    }

    /** Precio de una entrada, en pesos, según las reglas de cada tipo de función. */
    public abstract int calcularPrecioEntrada();

    /** "Función regular" o "Función popular": se muestra en la tabla sin preguntar por la clase concreta. */
    public abstract String getTipo();

    protected static String validarPelicula(String pelicula) {
        if (pelicula == null || pelicula.isBlank()) {
            throw new IllegalArgumentException("El nombre de la película no puede estar vacío.");
        }
        return pelicula.trim();
    }

    protected static int validarDuracion(int duracionMin) {
        if (duracionMin < DURACION_MINIMA || duracionMin > DURACION_MAXIMA) {
            throw new IllegalArgumentException("La duración debe estar entre " + DURACION_MINIMA + " y "
                    + DURACION_MAXIMA + " minutos.");
        }
        return duracionMin;
    }

    protected static int validarEntradas(int entradas) {
        if (entradas < ENTRADAS_MINIMAS || entradas > ENTRADAS_MAXIMAS) {
            throw new IllegalArgumentException("Las entradas vendidas deben estar entre " + ENTRADAS_MINIMAS
                    + " y " + ENTRADAS_MAXIMAS + ".");
        }
        return entradas;
    }

    /** Formato de pesos chilenos: $6.500. */
    public static String pesos(int monto) {
        return String.format("$%,d", monto).replace(',', '.');
    }

    @Override
    public String toString() {
        return getTipo() + " | " + pelicula + " | " + duracionMin + " min | " + entradas + " entradas | "
                + pesos(calcularPrecioEntrada());
    }
}
