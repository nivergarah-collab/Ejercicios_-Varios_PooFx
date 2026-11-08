package cl.duoc.gastos.model;

/**
 * Gasto compartido entre los residentes de un departamento. Clase abstracta:
 * se registra siempre como GastoSalida o GastoServicio.
 *
 * Reglas comunes: descripción no vacía, monto total entre $1.000 y $5.000.000
 * y participantes (personas que se reparten el gasto) entre 1 y 20.
 */
public abstract class Gasto {

    public static final int MONTO_MINIMO = 1_000;
    public static final int MONTO_MAXIMO = 5_000_000;
    public static final int PARTICIPANTES_MINIMOS = 1;
    public static final int PARTICIPANTES_MAXIMOS = 20;

    private String descripcion;
    private int montoTotal;
    private int participantes;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    protected Gasto() {
    }

    protected Gasto(String descripcion, int montoTotal, int participantes) {
        setDescripcion(descripcion);
        setMontoTotal(montoTotal);
        setParticipantes(participantes);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = validarDescripcion(descripcion);
    }

    public int getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(int montoTotal) {
        this.montoTotal = validarMonto(montoTotal);
    }

    public int getParticipantes() {
        return participantes;
    }

    public void setParticipantes(int participantes) {
        this.participantes = validarParticipantes(participantes);
    }

    /** Aporte de cada participante, en pesos, según las reglas de cada tipo de gasto. */
    public abstract int calcularAporte();

    /** "Salida" o "Servicio": se muestra en la tabla sin preguntar por la clase concreta. */
    public abstract String getTipo();

    /** Parte que le toca a cada participante antes de recargos o descuentos (división entera). */
    protected int aporteBase() {
        return montoTotal / participantes;
    }

    protected static String validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción del gasto no puede estar vacía.");
        }
        return descripcion.trim();
    }

    protected static int validarMonto(int montoTotal) {
        if (montoTotal < MONTO_MINIMO || montoTotal > MONTO_MAXIMO) {
            throw new IllegalArgumentException("El monto total debe estar entre " + pesos(MONTO_MINIMO)
                    + " y " + pesos(MONTO_MAXIMO) + ".");
        }
        return montoTotal;
    }

    protected static int validarParticipantes(int participantes) {
        if (participantes < PARTICIPANTES_MINIMOS || participantes > PARTICIPANTES_MAXIMOS) {
            throw new IllegalArgumentException("Los participantes deben estar entre " + PARTICIPANTES_MINIMOS
                    + " y " + PARTICIPANTES_MAXIMOS + ".");
        }
        return participantes;
    }

    /** Formato de pesos chilenos: $20.000. */
    public static String pesos(int monto) {
        return String.format("$%,d", monto).replace(',', '.');
    }

    @Override
    public String toString() {
        return getTipo() + " | " + descripcion + " | " + pesos(montoTotal) + " | " + participantes
                + " pers. | " + pesos(calcularAporte());
    }
}
