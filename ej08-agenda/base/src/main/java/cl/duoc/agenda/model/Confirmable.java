package cl.duoc.agenda.model;

/**
 * Contrato que cumplen los compromisos que requieren confirmar la asistencia
 * de los invitados (por ejemplo, un evento con reserva de local).
 * El estado no tiene setter: se cambia con confirmar().
 */
public interface Confirmable {

    /**
     * Indica si la asistencia de los invitados ya fue confirmada.
     *
     * @return true si el compromiso está confirmado
     */
    boolean estaConfirmado();

    /**
     * Registra que la asistencia de los invitados ya fue confirmada.
     */
    void confirmar();
}
