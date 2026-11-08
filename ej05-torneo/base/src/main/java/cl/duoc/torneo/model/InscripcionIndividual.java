package cl.duoc.torneo.model;

/**
 * Inscripción de un competidor individual. Participa en el sistema de becas.
 * Cuota base de $18.000; si lleva más de 3 años federado, la cuota baja un 20 %.
 */
public class InscripcionIndividual extends Inscripcion implements Becable {

    public static final int CUOTA_BASE = 18_000;
    public static final int DESCUENTO_FEDERADO_ANTIGUO = 20;
    public static final int ANIOS_FEDERADO_ANTIGUO = 3;
    public static final int RANKING_MINIMO = 1;
    public static final int RANKING_MAXIMO = 500;
    public static final int ANIOS_MINIMOS = 0;
    public static final int ANIOS_MAXIMOS = 40;

    private int ranking;
    private int aniosFederado;
    private boolean becado;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public InscripcionIndividual() {
    }

    public InscripcionIndividual(String nombre, int partidosJugados, int rondas, int ranking,
                                 int aniosFederado) {
        super(nombre, partidosJugados, rondas);
        setRanking(ranking);
        setAniosFederado(aniosFederado);
        this.becado = false;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        if (ranking < RANKING_MINIMO || ranking > RANKING_MAXIMO) {
            throw new IllegalArgumentException("El ranking debe estar entre " + RANKING_MINIMO + " y "
                    + RANKING_MAXIMO + ".");
        }
        this.ranking = ranking;
    }

    public int getAniosFederado() {
        return aniosFederado;
    }

    public void setAniosFederado(int aniosFederado) {
        if (aniosFederado < ANIOS_MINIMOS || aniosFederado > ANIOS_MAXIMOS) {
            throw new IllegalArgumentException("Los años federado deben estar entre " + ANIOS_MINIMOS
                    + " y " + ANIOS_MAXIMOS + ".");
        }
        this.aniosFederado = aniosFederado;
    }

    @Override
    public boolean estaBecado() {
        return becado;
    }

    @Override
    public void marcarBecado() {
        this.becado = true;
    }

    @Override
    public int calcularCuota() {
        if (aniosFederado > ANIOS_FEDERADO_ANTIGUO) {
            return CUOTA_BASE - CUOTA_BASE * DESCUENTO_FEDERADO_ANTIGUO / 100;
        }
        return CUOTA_BASE;
    }

    @Override
    public String getTipo() {
        return "Individual";
    }
}
