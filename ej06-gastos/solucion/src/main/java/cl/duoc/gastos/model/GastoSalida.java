package cl.duoc.gastos.model;

/**
 * Gasto de una salida grupal (restaurante, cine, paseo). El aporte base es el
 * monto dividido entre los participantes; si se dejó propina, el aporte sube
 * un 10 %.
 */
public class GastoSalida extends Gasto {

    public static final String TIPO = "Salida";
    public static final int RECARGO_PROPINA = 10;

    private String lugar;
    private boolean conPropina;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public GastoSalida() {
    }

    public GastoSalida(String descripcion, int montoTotal, int participantes, String lugar, boolean conPropina) {
        super(descripcion, montoTotal, participantes);
        setLugar(lugar);
        setConPropina(conPropina);
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        if (lugar == null || lugar.isBlank()) {
            throw new IllegalArgumentException("El lugar de la salida no puede estar vacío.");
        }
        this.lugar = lugar.trim();
    }

    public boolean isConPropina() {
        return conPropina;
    }

    public void setConPropina(boolean conPropina) {
        this.conPropina = conPropina;
    }

    @Override
    public int calcularAporte() {
        int base = aporteBase();
        if (conPropina) {
            return base + base * RECARGO_PROPINA / 100;
        }
        return base;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
