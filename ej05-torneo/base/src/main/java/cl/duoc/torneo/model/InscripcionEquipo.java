package cl.duoc.torneo.model;

/**
 * Inscripción de un equipo. Cuota base de $60.000; si el equipo pide cancha
 * techada, la cuota sube un 25 %.
 */
public class InscripcionEquipo extends Inscripcion {

    public static final int CUOTA_BASE = 60_000;
    public static final int RECARGO_CANCHA_TECHADA = 25;

    private String capitan;
    private boolean canchaTechada;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public InscripcionEquipo() {
    }

    public InscripcionEquipo(String nombre, int partidosJugados, int rondas, String capitan,
                             boolean canchaTechada) {
        super(nombre, partidosJugados, rondas);
        setCapitan(capitan);
        setCanchaTechada(canchaTechada);
    }

    public String getCapitan() {
        return capitan;
    }

    public void setCapitan(String capitan) {
        this.capitan = validarTexto(capitan, "El capitán");
    }

    public boolean isCanchaTechada() {
        return canchaTechada;
    }

    public void setCanchaTechada(boolean canchaTechada) {
        this.canchaTechada = canchaTechada;
    }

    @Override
    public int calcularCuota() {
        if (canchaTechada) {
            return CUOTA_BASE + CUOTA_BASE * RECARGO_CANCHA_TECHADA / 100;
        }
        return CUOTA_BASE;
    }

    @Override
    public String getTipo() {
        return "Equipo";
    }
}
