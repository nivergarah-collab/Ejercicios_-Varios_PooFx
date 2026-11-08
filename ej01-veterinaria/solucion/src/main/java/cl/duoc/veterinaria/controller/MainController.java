package cl.duoc.veterinaria.controller;

import cl.duoc.veterinaria.model.Atencion;
import cl.duoc.veterinaria.model.Consulta;
import cl.duoc.veterinaria.model.Vacunacion;
import cl.duoc.veterinaria.repository.AtencionRepository;
import cl.duoc.veterinaria.repository.PersistenciaException;
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
    @FXML private TextField txtPaciente;
    @FXML private TextField txtPeso;
    @FXML private TextField txtSesiones;

    @FXML private HBox boxConsulta;
    @FXML private TextField txtVeterinario;
    @FXML private CheckBox chkUrgente;

    @FXML private HBox boxVacunacion;
    @FXML private TextField txtDosis;
    @FXML private TextField txtMeses;
    @FXML private CheckBox chkRecordatorio;

    @FXML private TableView<Atencion> tblAtenciones;
    @FXML private TableColumn<Atencion, String> colTipo;
    @FXML private TableColumn<Atencion, String> colPaciente;
    @FXML private TableColumn<Atencion, Integer> colPeso;
    @FXML private TableColumn<Atencion, Integer> colSesiones;
    @FXML private TableColumn<Atencion, String> colCosto;

    @FXML private Label lblMensaje;

    private final AtencionRepository repositorio = new AtencionRepository();
    private final ObservableList<Atencion> atencionesTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll(Consulta.TIPO, Vacunacion.TIPO);

        colTipo.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getTipo()));
        colPaciente.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getPaciente()));
        colPeso.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getPesoKg()));
        colSesiones.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getSesiones()));
        colCosto.setCellValueFactory(fila -> new SimpleStringProperty(Atencion.pesos(fila.getValue().calcularCosto())));

        cmbTipo.valueProperty().addListener((observable, anterior, nuevo) -> actualizarCamposPorTipo());
        actualizarCamposPorTipo();

        tblAtenciones.setItems(atencionesTabla);

        try {
            repositorio.cargar();
            sincronizarTabla();
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Deja activos solo los datos del tipo elegido. */
    private void actualizarCamposPorTipo() {
        boolean esConsulta = Consulta.TIPO.equals(cmbTipo.getValue());
        boolean esVacunacion = Vacunacion.TIPO.equals(cmbTipo.getValue());

        boxConsulta.setDisable(!esConsulta);
        boxVacunacion.setDisable(!esVacunacion);
    }

    private void sincronizarTabla() {
        atencionesTabla.setAll(repositorio.listar());
    }

    @FXML
    private void onRegistrar() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            lblMensaje.setText("Debe seleccionar el tipo de atención.");
            return;
        }

        String paciente = txtPaciente.getText().trim();
        if (paciente.isEmpty()) {
            lblMensaje.setText("El nombre del paciente no puede estar vacío.");
            return;
        }

        Integer peso = leerEntero(txtPeso, "El peso", 1, 150);
        if (peso == null) {
            return;
        }

        Integer sesiones = leerEntero(txtSesiones, "El número de sesiones", 1, 20);
        if (sesiones == null) {
            return;
        }

        Atencion atencion;
        if (Consulta.TIPO.equals(tipo)) {
            String veterinario = txtVeterinario.getText().trim();
            if (veterinario.isEmpty()) {
                lblMensaje.setText("El nombre del veterinario no puede estar vacío.");
                return;
            }
            atencion = new Consulta(paciente, peso, sesiones, veterinario, chkUrgente.isSelected());
        } else {
            Integer dosis = leerEntero(txtDosis, "El número de dosis", 1, 12);
            if (dosis == null) {
                return;
            }
            Integer meses = leerEntero(txtMeses, "El tiempo desde la última dosis (meses)", 0, 120);
            if (meses == null) {
                return;
            }
            Vacunacion vacunacion = new Vacunacion(paciente, peso, sesiones, dosis, meses);
            if (chkRecordatorio.isSelected()) {
                vacunacion.marcarRecordatorioEnviado();
            }
            atencion = vacunacion;
        }

        try {
            repositorio.registrar(atencion);
            sincronizarTabla();
            lblMensaje.setText(atencion.getPaciente() + " (" + atencion.getTipo() + ") registrada correctamente.");
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
        txtPaciente.clear();
        txtPeso.clear();
        txtSesiones.clear();
        txtVeterinario.clear();
        txtDosis.clear();
        txtMeses.clear();
        chkUrgente.setSelected(false);
        chkRecordatorio.setSelected(false);
    }

    @FXML
    private void onEliminar() {
        Atencion seleccionada = tblAtenciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            lblMensaje.setText("Debe seleccionar una atención de la tabla para eliminarla.");
            return;
        }

        try {
            repositorio.eliminar(seleccionada);
            sincronizarTabla();
            lblMensaje.setText(seleccionada.getPaciente() + " (" + seleccionada.getTipo() + ") eliminada correctamente.");
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }
}
