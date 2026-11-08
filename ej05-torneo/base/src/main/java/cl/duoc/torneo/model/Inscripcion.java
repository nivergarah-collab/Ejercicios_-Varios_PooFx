package cl.duoc.torneo.model;

/**
 * Inscripción de un participante en el torneo. Clase abstracta: se registra
 * siempre como inscripción de un Equipo o de un competidor Individual.
 *
 * Reglas comunes: nombre no vacío, partidos jugados entre 0 y 200 y rondas
 * en las que participará entre 1 y 12.
 */
public abstract class Inscripcion {

    public static final int PARTIDOS_MINIMOS = 0;
    public static final int PARTIDOS_MAXIMOS = 200;
    public static final int RONDAS_MINIMAS = 1;
    public static final int RONDAS_MAXIMAS = 12;

    private String nombre;
    private int partidosJugados;
    private int rondas;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    protected Inscripcion() {
    }

    protected Inscripcion(String nombre, int partidosJugados, int rondas) {
        setNombre(nombre);
        setPartidosJugados(partidosJugados);
        setRondas(rondas);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = validarTexto(nombre, "El nombre");
    }

    public int getPartidosJugados() {
        return partidosJugados;
    }

    public void setPartidosJugados(int partidosJugados) {
        this.partidosJugados = validarPartidos(partidosJugados);
    }

    public int getRondas() {
        return rondas;
    }

    public void setRondas(int rondas) {
        this.rondas = validarRondas(rondas);
    }

    /** Cuota de inscripción, en pesos, según las reglas de cada tipo de inscripción. */
    public abstract int calcularCuota();

    /** "Equipo" o "Individual": se muestra en la tabla sin preguntar por la clase concreta. */
    public abstract String getTipo();

    /** Valida un texto obligatorio; {@code campo} encabeza el mensaje (por ejemplo, "El nombre"). */
    protected static String validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return texto.trim();
    }

    protected static int validarPartidos(int partidosJugados) {
        if (partidosJugados < PARTIDOS_MINIMOS || partidosJugados > PARTIDOS_MAXIMOS) {
            throw new IllegalArgumentException("Los partidos jugados deben estar entre " + PARTIDOS_MINIMOS
                    + " y " + PARTIDOS_MAXIMOS + ".");
        }
        return partidosJugados;
    }

    protected static int validarRondas(int rondas) {
        if (rondas < RONDAS_MINIMAS || rondas > RONDAS_MAXIMAS) {
            throw new IllegalArgumentException("Las rondas deben estar entre " + RONDAS_MINIMAS + " y "
                    + RONDAS_MAXIMAS + ".");
        }
        return rondas;
    }

    /** Formato de pesos chilenos: $60.000. */
    public static String pesos(int monto) {
        return String.format("$%,d", monto).replace(',', '.');
    }

    @Override
    public String toString() {
        return getTipo() + " | " + nombre + " | " + partidosJugados + " partidos | " + rondas
                + " rondas | " + pesos(calcularCuota());
    }
}
