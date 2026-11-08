package cl.duoc.agenda.model;

/**
 * Reunión de trabajo. Costo base de $7.500 por asistente; si la reunión es
 * virtual, el costo baja un 40 %.
 */
public class Reunion extends Compromiso {

    public static final String TIPO = "Reunión";
    public static final int COSTO_BASE = 7_500;
    public static final int DESCUENTO_VIRTUAL = 40;

    private String organizador;
    private boolean virtual;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public Reunion() {
    }

    public Reunion(String titulo, int duracionMin, int asistentes, String organizador, boolean virtual) {
        super(titulo, duracionMin, asistentes);
        setOrganizador(organizador);
        setVirtual(virtual);
    }

    public String getOrganizador() {
        return organizador;
    }

    public void setOrganizador(String organizador) {
        if (organizador == null || organizador.isBlank()) {
            throw new IllegalArgumentException("El nombre del organizador no puede estar vacío.");
        }
        this.organizador = organizador.trim();
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    @Override
    public int calcularCostoPorAsistente() {
        if (virtual) {
            return COSTO_BASE - COSTO_BASE * DESCUENTO_VIRTUAL / 100;
        }
        return COSTO_BASE;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
