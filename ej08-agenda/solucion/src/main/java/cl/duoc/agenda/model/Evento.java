package cl.duoc.agenda.model;

/**
 * Evento con invitados. Participa en la confirmación de asistencia.
 * Costo base de $9.000 por asistente; si se agenda con 7 días o menos de
 * anticipación (organización de última hora), el costo sube un 35 %.
 */
public class Evento extends Compromiso implements Confirmable {

    public static final String TIPO = "Evento";
    public static final int COSTO_BASE = 9_000;
    public static final int RECARGO_ULTIMA_HORA = 35;
    public static final int DIAS_ULTIMA_HORA = 7;
    public static final int ANTICIPACION_MINIMA = 0;
    public static final int ANTICIPACION_MAXIMA = 365;

    private String lugar;
    private int diasAnticipacion;
    private boolean confirmado;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public Evento() {
    }

    public Evento(String titulo, int duracionMin, int asistentes, String lugar, int diasAnticipacion) {
        super(titulo, duracionMin, asistentes);
        setLugar(lugar);
        setDiasAnticipacion(diasAnticipacion);
        this.confirmado = false;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        if (lugar == null || lugar.isBlank()) {
            throw new IllegalArgumentException("El lugar no puede estar vacío.");
        }
        this.lugar = lugar.trim();
    }

    public int getDiasAnticipacion() {
        return diasAnticipacion;
    }

    public void setDiasAnticipacion(int diasAnticipacion) {
        if (diasAnticipacion < ANTICIPACION_MINIMA || diasAnticipacion > ANTICIPACION_MAXIMA) {
            throw new IllegalArgumentException("Los días de anticipación deben estar entre "
                    + ANTICIPACION_MINIMA + " y " + ANTICIPACION_MAXIMA + ".");
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
    public int calcularCostoPorAsistente() {
        if (diasAnticipacion <= DIAS_ULTIMA_HORA) {
            return COSTO_BASE + COSTO_BASE * RECARGO_ULTIMA_HORA / 100;
        }
        return COSTO_BASE;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
