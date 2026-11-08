package cl.duoc.agenda.controller;

import cl.duoc.agenda.model.Compromiso;
import cl.duoc.agenda.model.Evento;
import cl.duoc.agenda.model.Reunion;
import cl.duoc.agenda.repository.CompromisoRepository;
import cl.duoc.agenda.repository.PersistenciaException;
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
    @FXML private TextField txtTitulo;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtAsistentes;

    @FXML private HBox boxReunion;
    @FXML private TextField txtOrganizador;
    @FXML private CheckBox chkVirtual;

    @FXML private HBox boxEvento;
    @FXML private TextField txtLugar;
    @FXML private TextField txtAnticipacion;
    @FXML private CheckBox chkConfirmado;

    @FXML private TableView<Compromiso> tblCompromisos;
    @FXML private TableColumn<Compromiso, String> colTipo;
    @FXML private TableColumn<Compromiso, String> colTitulo;
    @FXML private TableColumn<Compromiso, Integer> colDuracion;
    @FXML private TableColumn<Compromiso, Integer> colAsistentes;
    @FXML private TableColumn<Compromiso, String> colCosto;

    @FXML private Label lblMensaje;

    private final CompromisoRepository repositorio = new CompromisoRepository();
    private final ObservableList<Compromiso> compromisosTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll(Reunion.TIPO, Evento.TIPO);

        colTipo.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getTipo()));
        colTitulo.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getTitulo()));
        colDuracion.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getDuracionMin()));
        colAsistentes.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getAsistentes()));
        colCosto.setCellValueFactory(fila -> new SimpleStringProperty(
                Compromiso.pesos(fila.getValue().calcularCostoPorAsistente())));

        cmbTipo.valueProperty().addListener((observable, anterior, nuevo) -> actualizarCamposPorTipo());
        actualizarCamposPorTipo();

        tblCompromisos.setItems(compromisosTabla);

        try {
            repositorio.cargar();
            sincronizarTabla();
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Deja activos solo los datos del tipo elegido. */
    private void actualizarCamposPorTipo() {
        boolean esReunion = Reunion.TIPO.equals(cmbTipo.getValue());
        boolean esEvento = Evento.TIPO.equals(cmbTipo.getValue());

        boxReunion.setDisable(!esReunion);
        boxEvento.setDisable(!esEvento);
    }

    private void sincronizarTabla() {
        compromisosTabla.setAll(repositorio.listar());
    }

    @FXML
    private void onRegistrar() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            lblMensaje.setText("Debe seleccionar el tipo de compromiso.");
            return;
        }

        String titulo = txtTitulo.getText().trim();
        if (titulo.isEmpty()) {
            lblMensaje.setText("El título no puede estar vacío.");
            return;
        }

        Integer duracion = leerEntero(txtDuracion, "La duración (minutos)",
                Compromiso.DURACION_MINIMA, Compromiso.DURACION_MAXIMA);
        if (duracion == null) {
            return;
        }

        Integer asistentes = leerEntero(txtAsistentes, "El número de asistentes",
                Compromiso.ASISTENTES_MINIMOS, Compromiso.ASISTENTES_MAXIMOS);
        if (asistentes == null) {
            return;
        }

        Compromiso compromiso;
        if (Reunion.TIPO.equals(tipo)) {
            String organizador = txtOrganizador.getText().trim();
            if (organizador.isEmpty()) {
                lblMensaje.setText("El nombre del organizador no puede estar vacío.");
                return;
            }
            compromiso = new Reunion(titulo, duracion, asistentes, organizador, chkVirtual.isSelected());
        } else {
            String lugar = txtLugar.getText().trim();
            if (lugar.isEmpty()) {
                lblMensaje.setText("El lugar no puede estar vacío.");
                return;
            }
            Integer anticipacion = leerEntero(txtAnticipacion, "La anticipación (días)",
                    Evento.ANTICIPACION_MINIMA, Evento.ANTICIPACION_MAXIMA);
            if (anticipacion == null) {
                return;
            }
            Evento evento = new Evento(titulo, duracion, asistentes, lugar, anticipacion);
            if (chkConfirmado.isSelected()) {
                evento.confirmar();
            }
            compromiso = evento;
        }

        try {
            repositorio.registrar(compromiso);
            sincronizarTabla();
            lblMensaje.setText(compromiso.getTitulo() + " (" + compromiso.getTipo() + ") registrado correctamente.");
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
        txtTitulo.clear();
        txtDuracion.clear();
        txtAsistentes.clear();
        txtOrganizador.clear();
        txtLugar.clear();
        txtAnticipacion.clear();
        chkVirtual.setSelected(false);
        chkConfirmado.setSelected(false);
    }

    @FXML
    private void onEliminar() {
        Compromiso seleccionado = tblCompromisos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText("Debe seleccionar un compromiso de la tabla para eliminarlo.");
            return;
        }

        try {
            repositorio.eliminar(seleccionado);
            sincronizarTabla();
            lblMensaje.setText(seleccionado.getTitulo() + " (" + seleccionado.getTipo() + ") eliminado correctamente.");
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }
}
