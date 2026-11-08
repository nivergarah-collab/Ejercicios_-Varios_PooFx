package cl.duoc.peliculas.model;

/**
 * Función popular. Sus entradas pueden reembolsarse. Precio base de la
 * entrada de $4.800; si la película lleva 30 días o más en cartelera, el
 * precio baja un 25 %.
 */
public class FuncionPopular extends Funcion implements Reembolsable {

    public static final String TIPO = "Función popular";
    public static final int PRECIO_BASE = 4_800;
    public static final int DESCUENTO_LARGA_CARTELERA = 25;
    public static final int DIAS_LARGA_CARTELERA = 30;
    public static final int DIAS_MINIMOS = 0;
    public static final int DIAS_MAXIMOS = 365;

    private String distribuidora;
    private int diasEnCartelera;
    private boolean reembolsada;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public FuncionPopular() {
    }

    public FuncionPopular(String pelicula, int duracionMin, int entradas, String distribuidora, int diasEnCartelera) {
        super(pelicula, duracionMin, entradas);
        setDistribuidora(distribuidora);
        setDiasEnCartelera(diasEnCartelera);
        this.reembolsada = false;
    }

    public String getDistribuidora() {
        return distribuidora;
    }

    public void setDistribuidora(String distribuidora) {
        if (distribuidora == null || distribuidora.isBlank()) {
            throw new IllegalArgumentException("El nombre de la distribuidora no puede estar vacío.");
        }
        this.distribuidora = distribuidora.trim();
    }

    public int getDiasEnCartelera() {
        return diasEnCartelera;
    }

    public void setDiasEnCartelera(int diasEnCartelera) {
        if (diasEnCartelera < DIAS_MINIMOS || diasEnCartelera > DIAS_MAXIMOS) {
            throw new IllegalArgumentException("Los días en cartelera deben estar entre " + DIAS_MINIMOS
                    + " y " + DIAS_MAXIMOS + ".");
        }
        this.diasEnCartelera = diasEnCartelera;
    }

    @Override
    public boolean estaReembolsada() {
        return reembolsada;
    }

    @Override
    public void marcarReembolsada() {
        this.reembolsada = true;
    }

    @Override
    public int calcularPrecioEntrada() {
        if (diasEnCartelera >= DIAS_LARGA_CARTELERA) {
            return PRECIO_BASE - PRECIO_BASE * DESCUENTO_LARGA_CARTELERA / 100;
        }
        return PRECIO_BASE;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
