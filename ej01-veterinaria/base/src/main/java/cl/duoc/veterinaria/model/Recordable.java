package cl.duoc.veterinaria.model;

/**
 * Contrato que cumplen las atenciones que generan un recordatorio al dueño
 * del paciente (por ejemplo, la próxima dosis de una vacuna).
 * El estado no tiene setter: se cambia con marcarRecordatorioEnviado().
 */
public interface Recordable {

    /**
     * Indica si el recordatorio ya fue enviado al dueño del paciente.
     *
     * @return true si el recordatorio fue enviado
     */
    boolean estaRecordatorioEnviado();

    /**
     * Registra que el recordatorio ya fue enviado.
     */
    void marcarRecordatorioEnviado();
}
