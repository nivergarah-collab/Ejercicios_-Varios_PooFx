package cl.duoc.torneo.model;

/**
 * Contrato que cumplen las inscripciones que pueden recibir una beca de la organización.
 * El estado no tiene setter: se cambia con marcarBecado().
 */
public interface Becable {

    /**
     * Indica si la inscripción ya fue becada.
     *
     * @return true si tiene beca
     */
    boolean estaBecado();

    /**
     * Registra que la inscripción recibió la beca.
     */
    void marcarBecado();
}
