package cl.duoc.inventario.model;

/**
 * Artículo guardado en la bodega. Clase abstracta: se registra siempre
 * como Pintura o Herramienta.
 *
 * Reglas comunes: nombre no vacío, stock entre 1 y 500 unidades y número
 * de anaquel entre 1 y 40.
 */
public abstract class Articulo {

    public static final int STOCK_MINIMO = 1;
    public static final int STOCK_MAXIMO = 500;
    public static final int ANAQUEL_MINIMO = 1;
    public static final int ANAQUEL_MAXIMO = 40;

    private String nombre;
    private int stock;
    private int anaquel;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    protected Articulo() {
    }

    protected Articulo(String nombre, int stock, int anaquel) {
        setNombre(nombre);
        setStock(stock);
        setAnaquel(anaquel);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = validarNombre(nombre);
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = validarStock(stock);
    }

    public int getAnaquel() {
        return anaquel;
    }

    public void setAnaquel(int anaquel) {
        this.anaquel = validarAnaquel(anaquel);
    }

    /** Precio de venta de una unidad, en pesos, según las reglas de cada tipo de artículo. */
    public abstract int calcularPrecioVenta();

    /** "Pintura" o "Herramienta": se muestra en la tabla sin preguntar por la clase concreta. */
    public abstract String getTipo();

    protected static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        return nombre.trim();
    }

    protected static int validarStock(int stock) {
        if (stock < STOCK_MINIMO || stock > STOCK_MAXIMO) {
            throw new IllegalArgumentException("El stock debe estar entre " + STOCK_MINIMO + " y "
                    + STOCK_MAXIMO + " unidades.");
        }
        return stock;
    }

    protected static int validarAnaquel(int anaquel) {
        if (anaquel < ANAQUEL_MINIMO || anaquel > ANAQUEL_MAXIMO) {
            throw new IllegalArgumentException("El anaquel debe estar entre " + ANAQUEL_MINIMO + " y "
                    + ANAQUEL_MAXIMO + ".");
        }
        return anaquel;
    }

    /** Formato de pesos chilenos: $12.000. */
    public static String pesos(int monto) {
        return String.format("$%,d", monto).replace(',', '.');
    }

    @Override
    public String toString() {
        return getTipo() + " | " + nombre + " | " + stock + " u. | anaquel " + anaquel + " | "
                + pesos(calcularPrecioVenta());
    }
}
