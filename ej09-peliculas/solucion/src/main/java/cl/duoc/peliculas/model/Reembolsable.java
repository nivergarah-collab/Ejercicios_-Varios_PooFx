package cl.duoc.peliculas.model;

/**
 * Contrato que cumplen las funciones cuyas entradas pueden reembolsarse
 * (por ejemplo, cuando la función se suspende).
 * El estado no tiene setter: se cambia con marcarReembolsada().
 */
public interface Reembolsable {

    /**
     * Indica si las entradas de la función ya fueron reembolsadas.
     *
     * @return true si el reembolso ya se realizó
     */
    boolean estaReembolsada();

    /**
     * Registra que las entradas de la función ya fueron reembolsadas.
     */
    void marcarReembolsada();
}
