package cl.duoc.gastos.model;

/**
 * Cuenta de un servicio del departamento (luz, internet, gas). Participa en
 * el reembolso a quien adelantó el pago. El aporte base es el monto dividido
 * entre los participantes; si la cuenta se pagó con 10 días o más de
 * anticipación, el aporte baja un 8 %.
 */
public class GastoServicio extends Gasto implements Reembolsable {

    public static final String TIPO = "Servicio";
    public static final int DESCUENTO_PAGO_ANTICIPADO = 8;
    public static final int DIAS_PAGO_ANTICIPADO = 10;
    public static final int DIAS_MINIMOS = 0;
    public static final int DIAS_MAXIMOS = 60;

    private String proveedor;
    private int diasDeAnticipo;
    private boolean reembolsado;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public GastoServicio() {
    }

    public GastoServicio(String descripcion, int montoTotal, int participantes, String proveedor, int diasDeAnticipo) {
        super(descripcion, montoTotal, participantes);
        setProveedor(proveedor);
        setDiasDeAnticipo(diasDeAnticipo);
        this.reembolsado = false;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        if (proveedor == null || proveedor.isBlank()) {
            throw new IllegalArgumentException("El proveedor del servicio no puede estar vacío.");
        }
        this.proveedor = proveedor.trim();
    }

    public int getDiasDeAnticipo() {
        return diasDeAnticipo;
    }

    public void setDiasDeAnticipo(int diasDeAnticipo) {
        if (diasDeAnticipo < DIAS_MINIMOS || diasDeAnticipo > DIAS_MAXIMOS) {
            throw new IllegalArgumentException("Los días de anticipo deben estar entre " + DIAS_MINIMOS
                    + " y " + DIAS_MAXIMOS + ".");
        }
        this.diasDeAnticipo = diasDeAnticipo;
    }

    @Override
    public boolean estaReembolsado() {
        return reembolsado;
    }

    @Override
    public void marcarReembolsado() {
        this.reembolsado = true;
    }

    @Override
    public int calcularAporte() {
        int base = aporteBase();
        if (diasDeAnticipo >= DIAS_PAGO_ANTICIPADO) {
            return base - base * DESCUENTO_PAGO_ANTICIPADO / 100;
        }
        return base;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
