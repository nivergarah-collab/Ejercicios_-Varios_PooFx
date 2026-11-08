package cl.duoc.veterinaria.model;

/**
 * Vacunación. Participa en el aviso de la próxima dosis al dueño.
 * Costo base por sesión de $15.000; si la dosis anterior se aplicó hace 6
 * meses o menos (paciente al día), el costo baja un 20 %.
 */
public class Vacunacion extends Atencion implements Recordable {

    public static final String TIPO = "Vacunación";
    public static final int COSTO_BASE = 15_000;
    public static final int DESCUENTO_PACIENTE_AL_DIA = 20;
    public static final int MESES_PACIENTE_AL_DIA = 6;
    public static final int DOSIS_MINIMA = 1;
    public static final int DOSIS_MAXIMA = 12;
    public static final int MESES_MINIMOS = 0;
    public static final int MESES_MAXIMOS = 120;

    private int numeroDosis;
    private int mesesDesdeUltimaDosis;
    private boolean recordatorioEnviado;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public Vacunacion() {
    }

    public Vacunacion(String paciente, int pesoKg, int sesiones, int numeroDosis, int mesesDesdeUltimaDosis) {
        super(paciente, pesoKg, sesiones);
        setNumeroDosis(numeroDosis);
        setMesesDesdeUltimaDosis(mesesDesdeUltimaDosis);
        this.recordatorioEnviado = false;
    }

    public int getNumeroDosis() {
        return numeroDosis;
    }

    public void setNumeroDosis(int numeroDosis) {
        if (numeroDosis < DOSIS_MINIMA || numeroDosis > DOSIS_MAXIMA) {
            throw new IllegalArgumentException("El número de dosis debe estar entre " + DOSIS_MINIMA
                    + " y " + DOSIS_MAXIMA + ".");
        }
        this.numeroDosis = numeroDosis;
    }

    public int getMesesDesdeUltimaDosis() {
        return mesesDesdeUltimaDosis;
    }

    public void setMesesDesdeUltimaDosis(int mesesDesdeUltimaDosis) {
        if (mesesDesdeUltimaDosis < MESES_MINIMOS || mesesDesdeUltimaDosis > MESES_MAXIMOS) {
            throw new IllegalArgumentException("Los meses desde la última dosis deben estar entre "
                    + MESES_MINIMOS + " y " + MESES_MAXIMOS + ".");
        }
        this.mesesDesdeUltimaDosis = mesesDesdeUltimaDosis;
    }

    @Override
    public boolean estaRecordatorioEnviado() {
        return recordatorioEnviado;
    }

    @Override
    public void marcarRecordatorioEnviado() {
        this.recordatorioEnviado = true;
    }

    @Override
    public int calcularCosto() {
        if (mesesDesdeUltimaDosis <= MESES_PACIENTE_AL_DIA) {
            return COSTO_BASE - COSTO_BASE * DESCUENTO_PACIENTE_AL_DIA / 100;
        }
        return COSTO_BASE;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
