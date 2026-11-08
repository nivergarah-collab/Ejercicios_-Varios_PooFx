package cl.duoc.peliculas.model;

/**
 * Función regular. Precio base de la entrada de $6.500; si la función es en
 * 3D, el precio sube un 20 %.
 */
public class FuncionRegular extends Funcion {

    public static final String TIPO = "Función regular";
    public static final int PRECIO_BASE = 6_500;
    public static final int RECARGO_3D = 20;

    private String sala;
    private boolean es3D;

    /** Constructor sin parámetros, requerido para reconstruir objetos. */
    public FuncionRegular() {
    }

    public FuncionRegular(String pelicula, int duracionMin, int entradas, String sala, boolean es3D) {
        super(pelicula, duracionMin, entradas);
        setSala(sala);
        setEs3D(es3D);
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        if (sala == null || sala.isBlank()) {
            throw new IllegalArgumentException("El nombre de la sala no puede estar vacío.");
        }
        this.sala = sala.trim();
    }

    public boolean isEs3D() {
        return es3D;
    }

    public void setEs3D(boolean es3D) {
        this.es3D = es3D;
    }

    @Override
    public int calcularPrecioEntrada() {
        if (es3D) {
            return PRECIO_BASE + PRECIO_BASE * RECARGO_3D / 100;
        }
        return PRECIO_BASE;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }
}
