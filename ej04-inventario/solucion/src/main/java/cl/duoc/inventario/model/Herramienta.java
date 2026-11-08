package cl.duoc.inventario.model;

/**
 * Herramienta. Puede salir en préstamo. Precio de venta base de $22.000; si
 * lleva más de 12 meses guardada en la bodega, el precio baja un 25 %.
 */
public class Herramienta extends Articulo implements Prestable {

    public static final int PRECIO_BASE = 22_000;
    public static final int DESCUENTO_LARGA_ESTADIA = 25;
    public static final int MESES_LARGA_ESTADIA = 12;
    public static final int MESES_MAXIMOS = 60;

    private String marca;
    private int mesesEnBodega;
    private boolean prestada;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public Herramienta() {
    }

    public Herramienta(String nombre, int stock, int anaquel, String marca, int mesesEnBodega) {
        super(nombre, stock, anaquel);
        setMarca(marca);
        setMesesEnBodega(mesesEnBodega);
        this.prestada = false;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        if (marca == null || marca.isBlank()) {
            throw new IllegalArgumentException("La marca no puede estar vacía.");
        }
        this.marca = marca.trim();
    }

    public int getMesesEnBodega() {
        return mesesEnBodega;
    }

    public void setMesesEnBodega(int mesesEnBodega) {
        if (mesesEnBodega < 0 || mesesEnBodega > MESES_MAXIMOS) {
            throw new IllegalArgumentException("Los meses en bodega deben estar entre 0 y "
                    + MESES_MAXIMOS + ".");
        }
        this.mesesEnBodega = mesesEnBodega;
    }

    @Override
    public boolean estaPrestada() {
        return prestada;
    }

    @Override
    public void marcarPrestada() {
        this.prestada = true;
    }

    @Override
    public int calcularPrecioVenta() {
        if (mesesEnBodega > MESES_LARGA_ESTADIA) {
            return PRECIO_BASE - PRECIO_BASE * DESCUENTO_LARGA_ESTADIA / 100;
        }
        return PRECIO_BASE;
    }

    @Override
    public String getTipo() {
        return "Herramienta";
    }
}
