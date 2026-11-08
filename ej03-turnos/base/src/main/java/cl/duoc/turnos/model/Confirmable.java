package cl.duoc.turnos.model;

/**
 * Contrato que cumplen los turnos que exigen una confirmación previa
 * antes de usarse (por ejemplo, una sala reservada con anticipación).
 * El estado no tiene setter: se cambia con confirmar().
 */
public interface Confirmable {

    /**
     * Indica si el turno ya fue confirmado por el cliente.
     *
     * @return true si el turno está confirmado
     */
    boolean estaConfirmado();

    /**
     * Registra que el turno fue confirmado.
     */
    void confirmar();
}
