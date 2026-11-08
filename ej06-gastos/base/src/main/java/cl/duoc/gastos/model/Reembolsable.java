package cl.duoc.gastos.model;

/**
 * Contrato que cumplen los gastos que se pueden reembolsar al residente que
 * adelantó el pago (por ejemplo, una cuenta de servicio pagada por una sola
 * persona). El estado no tiene setter: se cambia con marcarReembolsado().
 */
public interface Reembolsable {

    /**
     * Indica si el gasto ya fue reembolsado a quien adelantó el pago.
     *
     * @return true si el reembolso ya se realizó
     */
    boolean estaReembolsado();

    /**
     * Registra que el reembolso ya se realizó.
     */
    void marcarReembolsado();
}
