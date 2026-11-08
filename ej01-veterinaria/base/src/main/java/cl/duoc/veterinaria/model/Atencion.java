package cl.duoc.veterinaria.model;

/**
 * Atención realizada en la clínica veterinaria. Clase abstracta: se registra
 * siempre como Consulta o Vacunación.
 *
 * Reglas comunes: paciente no vacío, peso entre 1 y 150 kg y sesiones
 * contratadas entre 1 y 20.
 */
public abstract class Atencion {

    public static final int PESO_MINIMO = 1;
    public static final int PESO_MAXIMO = 150;
    public static final int SESIONES_MINIMAS = 1;
    public static final int SESIONES_MAXIMAS = 20;

    private String paciente;
    private int pesoKg;
    private int sesiones;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    protected Atencion() {
    }

    protected Atencion(String paciente, int pesoKg, int sesiones) {
        setPaciente(paciente);
        setPesoKg(pesoKg);
        setSesiones(sesiones);
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = validarPaciente(paciente);
    }

    public int getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(int pesoKg) {
        this.pesoKg = validarPeso(pesoKg);
    }

    public int getSesiones() {
        return sesiones;
    }

    public void setSesiones(int sesiones) {
        this.sesiones = validarSesiones(sesiones);
    }

    /** Costo de una sesión, en pesos, según las reglas de cada tipo de atención. */
    public abstract int calcularCosto();

    /** "Consulta" o "Vacunación": se muestra en la tabla sin preguntar por la clase concreta. */
    public abstract String getTipo();

    protected static String validarPaciente(String paciente) {
        if (paciente == null || paciente.isBlank()) {
            throw new IllegalArgumentException("El nombre del paciente no puede estar vacío.");
        }
        return paciente.trim();
    }

    protected static int validarPeso(int pesoKg) {
        if (pesoKg < PESO_MINIMO || pesoKg > PESO_MAXIMO) {
            throw new IllegalArgumentException("El peso debe estar entre " + PESO_MINIMO + " y "
                    + PESO_MAXIMO + " kg.");
        }
        return pesoKg;
    }

    protected static int validarSesiones(int sesiones) {
        if (sesiones < SESIONES_MINIMAS || sesiones > SESIONES_MAXIMAS) {
            throw new IllegalArgumentException("Las sesiones deben estar entre " + SESIONES_MINIMAS + " y "
                    + SESIONES_MAXIMAS + ".");
        }
        return sesiones;
    }

    /** Formato de pesos chilenos: $20.000. */
    public static String pesos(int monto) {
        return String.format("$%,d", monto).replace(',', '.');
    }

    @Override
    public String toString() {
        return getTipo() + " | " + paciente + " | " + pesoKg + " kg | " + sesiones + " ses. | "
                + pesos(calcularCosto());
    }
}
