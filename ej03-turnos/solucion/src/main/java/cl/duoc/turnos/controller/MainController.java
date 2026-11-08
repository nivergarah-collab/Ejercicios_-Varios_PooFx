package cl.duoc.turnos.controller;

import cl.duoc.turnos.model.Turno;
import cl.duoc.turnos.model.TurnoCancha;
import cl.duoc.turnos.model.TurnoSala;
import cl.duoc.turnos.repository.PersistenciaException;
import cl.duoc.turnos.repository.TurnoRepository;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class MainController {

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtCliente;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoras;

    @FXML private HBox boxCancha;
    @FXML private TextField txtNumeroCancha;
    @FXML private CheckBox chkIluminacion;

    @FXML private HBox boxSala;
    @FXML private TextField txtAsistentes;
    @FXML private TextField txtDiasAnticipacion;
    @FXML private CheckBox chkConfirmado;

    @FXML private TableView<Turno> tblTurnos;
    @FXML private TableColumn<Turno, String> colTipo;
    @FXML private TableColumn<Turno, String> colCliente;
    @FXML private TableColumn<Turno, Integer> colHoraInicio;
    @FXML private TableColumn<Turno, Integer> colHoras;
    @FXML private TableColumn<Turno, String> colTotal;

    @FXML private Label lblMensaje;

    private final TurnoRepository repositorio = new TurnoRepository();
    private final ObservableList<Turno> turnosTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll(TurnoCancha.TIPO, TurnoSala.TIPO);

        colTipo.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getTipo()));
        colCliente.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getCliente()));
        colHoraInicio.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getHoraInicio()));
        colHoras.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getHoras()));
        colTotal.setCellValueFactory(fila -> new SimpleStringProperty(Turno.pesos(fila.getValue().calcularTotal())));

        cmbTipo.valueProperty().addListener((observable, anterior, nuevo) -> actualizarCamposPorTipo());
        actualizarCamposPorTipo();

        tblTurnos.setItems(turnosTabla);

        try {
            repositorio.cargar();
            sincronizarTabla();
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Deja activos solo los datos del tipo elegido. */
    private void actualizarCamposPorTipo() {
        boolean esCancha = TurnoCancha.TIPO.equals(cmbTipo.getValue());
        boolean esSala = TurnoSala.TIPO.equals(cmbTipo.getValue());

        boxCancha.setDisable(!esCancha);
        boxSala.setDisable(!esSala);
    }

    private void sincronizarTabla() {
        turnosTabla.setAll(repositorio.listar());
    }

    @FXML
    private void onRegistrar() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            lblMensaje.setText("Debe seleccionar el tipo de turno.");
            return;
        }

        String cliente = txtCliente.getText().trim();
        if (cliente.isEmpty()) {
            lblMensaje.setText("El nombre del cliente no puede estar vacío.");
            return;
        }

        Integer horaInicio = leerEntero(txtHoraInicio, "La hora de inicio",
                Turno.HORA_INICIO_MINIMA, Turno.HORA_INICIO_MAXIMA);
        if (horaInicio == null) {
            return;
        }

        Integer horas = leerEntero(txtHoras, "La duración del turno (en horas)", Turno.HORAS_MINIMAS, Turno.HORAS_MAXIMAS);
        if (horas == null) {
            return;
        }

        Turno turno;
        if (TurnoCancha.TIPO.equals(tipo)) {
            Integer numeroCancha = leerEntero(txtNumeroCancha, "El número de cancha",
                    TurnoCancha.CANCHA_MINIMA, TurnoCancha.CANCHA_MAXIMA);
            if (numeroCancha == null) {
                return;
            }
            turno = new TurnoCancha(cliente, horaInicio, horas, numeroCancha, chkIluminacion.isSelected());
        } else {
            Integer asistentes = leerEntero(txtAsistentes, "La cantidad de asistentes",
                    TurnoSala.ASISTENTES_MINIMOS, TurnoSala.ASISTENTES_MAXIMOS);
            if (asistentes == null) {
                return;
            }
            Integer dias = leerEntero(txtDiasAnticipacion, "La anticipación de la reserva (en días)",
                    TurnoSala.DIAS_MINIMOS, TurnoSala.DIAS_MAXIMOS);
            if (dias == null) {
                return;
            }
            TurnoSala sala = new TurnoSala(cliente, horaInicio, horas, asistentes, dias);
            if (chkConfirmado.isSelected()) {
                sala.confirmar();
            }
            turno = sala;
        }

        try {
            repositorio.registrar(turno);
            sincronizarTabla();
            lblMensaje.setText(turno.getCliente() + " (" + turno.getTipo() + ") registrado correctamente.");
            limpiarFormulario();
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Lee un entero del campo; si no sirve, muestra el motivo y devuelve null. */
    private Integer leerEntero(TextField campo, String nombreCampo, int minimo, int maximo) {
        int valor;
        try {
            valor = Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            lblMensaje.setText(nombreCampo + " debe ser un número entero.");
            return null;
        }
        if (valor < minimo || valor > maximo) {
            lblMensaje.setText(nombreCampo + " debe estar entre " + minimo + " y " + maximo + ".");
            return null;
        }
        return valor;
    }

    private void limpiarFormulario() {
        cmbTipo.setValue(null);
        txtCliente.clear();
        txtHoraInicio.clear();
        txtHoras.clear();
        txtNumeroCancha.clear();
        txtAsistentes.clear();
        txtDiasAnticipacion.clear();
        chkIluminacion.setSelected(false);
        chkConfirmado.setSelected(false);
    }

    @FXML
    private void onEliminar() {
        Turno seleccionado = tblTurnos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText("Debe seleccionar un turno de la tabla para eliminarlo.");
            return;
        }

        try {
            repositorio.eliminar(seleccionado);
            sincronizarTabla();
            lblMensaje.setText(seleccionado.getCliente() + " (" + seleccionado.getTipo() + ") eliminado correctamente.");
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }
}
