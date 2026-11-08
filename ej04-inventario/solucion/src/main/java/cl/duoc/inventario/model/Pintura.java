package cl.duoc.inventario.model;

/**
 * Pintura. Precio de venta del envase: $3.200 por cada litro que contiene;
 * si es anti-óxido, el precio sube un 15 %.
 */
public class Pintura extends Articulo {

    public static final int PRECIO_POR_LITRO = 3_200;
    public static final int RECARGO_ANTIOXIDO = 15;
    public static final int LITROS_MINIMOS = 1;
    public static final int LITROS_MAXIMOS = 20;

    private int litrosEnvase;
    private boolean antioxido;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public Pintura() {
    }

    public Pintura(String nombre, int stock, int anaquel, int litrosEnvase, boolean antioxido) {
        super(nombre, stock, anaquel);
        setLitrosEnvase(litrosEnvase);
        setAntioxido(antioxido);
    }

    public int getLitrosEnvase() {
        return litrosEnvase;
    }

    public void setLitrosEnvase(int litrosEnvase) {
        if (litrosEnvase < LITROS_MINIMOS || litrosEnvase > LITROS_MAXIMOS) {
            throw new IllegalArgumentException("Los litros del envase deben estar entre " + LITROS_MINIMOS
                    + " y " + LITROS_MAXIMOS + ".");
        }
        this.litrosEnvase = litrosEnvase;
    }

    public boolean isAntioxido() {
        return antioxido;
    }

    public void setAntioxido(boolean antioxido) {
        this.antioxido = antioxido;
    }

    @Override
    public int calcularPrecioVenta() {
        int base = litrosEnvase * PRECIO_POR_LITRO;
        if (antioxido) {
            return base + base * RECARGO_ANTIOXIDO / 100;
        }
        return base;
    }

    @Override
    public String getTipo() {
        return "Pintura";
    }
}
