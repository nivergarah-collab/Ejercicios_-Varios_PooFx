package cl.duoc.veterinaria.model;

/**
 * Consulta médica. Costo base por sesión de $20.000; si es una consulta de
 * urgencia, el costo sube un 50 %.
 */
public class Consulta extends Atencion {

    public static final String TIPO = "Consulta";
    public static final int COSTO_BASE = 20_000;
    public static final int RECARGO_URGENCIA = 50;

    private String veterinario;
    private boolean urgente;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public Consulta() {
    }

    public Consulta(String paciente, int pesoKg, int sesiones, String veterinario, boolean urgente) {
        super(paciente, pesoKg, sesiones);
        setVeterinario(veterinario);
        setUrgente(urgente);
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        if (veterinario == null || veterinario.isBlank()) {
            throw new IllegalArgumentException("El nombre del veterinario no puede estar vacío.");
        }
        this.veterinario = veterinario.trim();
    }

    public boolean isUrgente() {
        return urgente;
    }

    public void setUrgente(boolean urgente) {
        this.urgente = urgente;
    }

    @Override
    public int calcularCosto() {
        if (urgente) {
            return COSTO_BASE + COSTO_BASE * RECARGO_URGENCIA / 100;
        }
        return COSTO_BASE;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
