package cl.duoc.turnos.model;

/**
 * Turno de cancha de pádel. Tarifa base de $12.000 por hora; si se contrata
 * con iluminación, el total se incrementa en un 25 %.
 */
public class TurnoCancha extends Turno {

    public static final String TIPO = "Cancha";
    public static final int TARIFA_HORA = 12_000;
    public static final int RECARGO_ILUMINACION = 25;
    public static final int CANCHA_MINIMA = 1;
    public static final int CANCHA_MAXIMA = 8;

    private int numeroCancha;
    private boolean conIluminacion;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public TurnoCancha() {
    }

    public TurnoCancha(String cliente, int horaInicio, int horas, int numeroCancha, boolean conIluminacion) {
        super(cliente, horaInicio, horas);
        setNumeroCancha(numeroCancha);
        setConIluminacion(conIluminacion);
    }

    public int getNumeroCancha() {
        return numeroCancha;
    }

    public void setNumeroCancha(int numeroCancha) {
        if (numeroCancha < CANCHA_MINIMA || numeroCancha > CANCHA_MAXIMA) {
            throw new IllegalArgumentException("El número de cancha debe estar entre " + CANCHA_MINIMA
                    + " y " + CANCHA_MAXIMA + ".");
        }
        this.numeroCancha = numeroCancha;
    }

    public boolean isConIluminacion() {
        return conIluminacion;
    }

    public void setConIluminacion(boolean conIluminacion) {
        this.conIluminacion = conIluminacion;
    }

    @Override
    public int calcularTotal() {
        int subtotal = TARIFA_HORA * getHoras();
        if (conIluminacion) {
            return subtotal + subtotal * RECARGO_ILUMINACION / 100;
        }
        return subtotal;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
