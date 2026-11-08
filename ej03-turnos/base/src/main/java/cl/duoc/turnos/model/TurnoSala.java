package cl.duoc.turnos.model;

/**
 * Turno de sala de reuniones. Participa en la confirmación previa del cliente.
 * Tarifa base de $8.000 por hora; si se reservó con 7 días de anticipación o
 * más, el total baja un 15 %.
 */
public class TurnoSala extends Turno implements Confirmable {

    public static final String TIPO = "Sala";
    public static final int TARIFA_HORA = 8_000;
    public static final int DESCUENTO_ANTICIPADO = 15;
    public static final int DIAS_RESERVA_ANTICIPADA = 7;
    public static final int ASISTENTES_MINIMOS = 1;
    public static final int ASISTENTES_MAXIMOS = 40;
    public static final int DIAS_MINIMOS = 0;
    public static final int DIAS_MAXIMOS = 60;

    private int asistentes;
    private int diasAnticipacion;
    private boolean confirmado;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public TurnoSala() {
    }

    public TurnoSala(String cliente, int horaInicio, int horas, int asistentes, int diasAnticipacion) {
        super(cliente, horaInicio, horas);
        setAsistentes(asistentes);
        setDiasAnticipacion(diasAnticipacion);
        this.confirmado = false;
    }

    public int getAsistentes() {
        return asistentes;
    }

    public void setAsistentes(int asistentes) {
        if (asistentes < ASISTENTES_MINIMOS || asistentes > ASISTENTES_MAXIMOS) {
            throw new IllegalArgumentException("Los asistentes deben estar entre " + ASISTENTES_MINIMOS
                    + " y " + ASISTENTES_MAXIMOS + ".");
        }
        this.asistentes = asistentes;
    }

    public int getDiasAnticipacion() {
        return diasAnticipacion;
    }

    public void setDiasAnticipacion(int diasAnticipacion) {
        if (diasAnticipacion < DIAS_MINIMOS || diasAnticipacion > DIAS_MAXIMOS) {
            throw new IllegalArgumentException("Los días de anticipación deben estar entre "
                    + DIAS_MINIMOS + " y " + DIAS_MAXIMOS + ".");
        }
        this.diasAnticipacion = diasAnticipacion;
    }

    @Override
    public boolean estaConfirmado() {
        return confirmado;
    }

    @Override
    public void confirmar() {
        this.confirmado = true;
    }

    @Override
    public int calcularTotal() {
        int subtotal = TARIFA_HORA * getHoras();
        if (diasAnticipacion >= DIAS_RESERVA_ANTICIPADA) {
            return subtotal - subtotal * DESCUENTO_ANTICIPADO / 100;
        }
        return subtotal;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
