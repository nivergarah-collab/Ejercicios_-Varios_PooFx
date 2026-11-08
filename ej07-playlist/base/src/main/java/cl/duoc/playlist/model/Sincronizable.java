package cl.duoc.playlist.model;

/**
 * Contrato que cumplen las pistas que se pueden sincronizar con el dispositivo
 * para escucharlas sin conexión (por ejemplo, un episodio de podcast).
 * El estado no tiene setter: se cambia con marcarSincronizado().
 */
public interface Sincronizable {

    /**
     * Indica si la pista ya fue sincronizada con el dispositivo.
     *
     * @return true si la pista está sincronizada
     */
    boolean estaSincronizado();

    /**
     * Registra que la pista ya fue sincronizada.
     */
    void marcarSincronizado();
}
