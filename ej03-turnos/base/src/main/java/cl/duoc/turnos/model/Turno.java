package cl.duoc.turnos.model;

/**
 * Turno reservado en el Centro Deportivo Los Aromos. Clase abstracta: se
 * reserva siempre como turno de Cancha o de Sala.
 *
 * Reglas comunes: cliente no vacío, hora de inicio entre las 8 y las 21 horas
 * y duración contratada entre 1 y 6 horas.
 */
public abstract class Turno {

    public static final int HORA_INICIO_MINIMA = 8;
    public static final int HORA_INICIO_MAXIMA = 21;
    public static final int HORAS_MINIMAS = 1;
    public static final int HORAS_MAXIMAS = 6;

    private String cliente;
    private int horaInicio;
    private int horas;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    protected Turno() {
    }

    protected Turno(String cliente, int horaInicio, int horas) {
        setCliente(cliente);
        setHoraInicio(horaInicio);
        setHoras(horas);
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = validarCliente(cliente);
    }

    public int getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(int horaInicio) {
        this.horaInicio = validarHoraInicio(horaInicio);
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = validarHoras(horas);
    }

    /** Total a pagar por el turno completo, en pesos, según las reglas de cada tipo. */
    public abstract int calcularTotal();

    /** "Cancha" o "Sala": se muestra en la tabla sin preguntar por la clase concreta. */
    public abstract String getTipo();

    protected static String validarCliente(String cliente) {
        if (cliente == null || cliente.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        return cliente.trim();
    }

    protected static int validarHoraInicio(int horaInicio) {
        if (horaInicio < HORA_INICIO_MINIMA || horaInicio > HORA_INICIO_MAXIMA) {
            throw new IllegalArgumentException("La hora de inicio debe estar entre las " + HORA_INICIO_MINIMA
                    + " y las " + HORA_INICIO_MAXIMA + " horas.");
        }
        return horaInicio;
    }

    protected static int validarHoras(int horas) {
        if (horas < HORAS_MINIMAS || horas > HORAS_MAXIMAS) {
            throw new IllegalArgumentException("Las horas del turno deben estar entre " + HORAS_MINIMAS
                    + " y " + HORAS_MAXIMAS + ".");
        }
        return horas;
    }

    /** Formato de pesos chilenos: $20.000. */
    public static String pesos(int monto) {
        return String.format("$%,d", monto).replace(',', '.');
    }

    @Override
    public String toString() {
        return getTipo() + " | " + cliente + " | " + horaInicio + ":00 h | " + horas + " h | "
                + pesos(calcularTotal());
    }
}
