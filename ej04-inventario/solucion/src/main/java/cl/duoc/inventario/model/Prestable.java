package cl.duoc.inventario.model;

/**
 * Contrato que cumplen los artículos que pueden salir en préstamo a un cliente
 * o a un taller. El estado no tiene setter: se cambia con marcarPrestada().
 */
public interface Prestable {

    /**
     * Indica si el artículo está actualmente en préstamo.
     *
     * @return true si está prestado
     */
    boolean estaPrestada();

    /**
     * Registra que el artículo salió en préstamo.
     */
    void marcarPrestada();
}
