package cl.duoc.biblioteca.model;

/**
 * Contrato que cumplen los préstamos que pueden renovarse.
 * El estado no tiene setter: se cambia con renovar().
 */
public interface Renovable {

    /**
     * Indica si el préstamo ya fue renovado.
     *
     * @return true si el préstamo ya fue renovado
     */
    boolean estaRenovado();

    /**
     * Registra que el préstamo fue renovado.
     */
    void renovar();
}
