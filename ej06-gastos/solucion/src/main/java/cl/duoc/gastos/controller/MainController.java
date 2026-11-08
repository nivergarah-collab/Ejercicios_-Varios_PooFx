package cl.duoc.gastos.controller;

import cl.duoc.gastos.model.Gasto;
import cl.duoc.gastos.model.GastoSalida;
import cl.duoc.gastos.model.GastoServicio;
import cl.duoc.gastos.repository.GastoRepository;
import cl.duoc.gastos.repository.PersistenciaException;
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
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtMonto;
    @FXML private TextField txtParticipantes;

    @FXML private HBox boxSalida;
    @FXML private TextField txtLugar;
    @FXML private CheckBox chkPropina;

    @FXML private HBox boxServicio;
    @FXML private TextField txtProveedor;
    @FXML private TextField txtAnticipo;
    @FXML private CheckBox chkReembolsado;

    @FXML private TableView<Gasto> tblGastos;
    @FXML private TableColumn<Gasto, String> colTipo;
    @FXML private TableColumn<Gasto, String> colDescripcion;
    @FXML private TableColumn<Gasto, String> colMonto;
    @FXML private TableColumn<Gasto, Integer> colParticipantes;
    @FXML private TableColumn<Gasto, String> colAporte;

    @FXML private Label lblMensaje;

    private final GastoRepository repositorio = new GastoRepository();
    private final ObservableList<Gasto> gastosTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll(GastoSalida.TIPO, GastoServicio.TIPO);

        colTipo.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getTipo()));
        colDescripcion.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getDescripcion()));
        colMonto.setCellValueFactory(fila -> new SimpleStringProperty(Gasto.pesos(fila.getValue().getMontoTotal())));
        colParticipantes.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getParticipantes()));
        colAporte.setCellValueFactory(fila -> new SimpleStringProperty(Gasto.pesos(fila.getValue().calcularAporte())));

        cmbTipo.valueProperty().addListener((observable, anterior, nuevo) -> actualizarCamposPorTipo());
        actualizarCamposPorTipo();

        tblGastos.setItems(gastosTabla);

        try {
            repositorio.cargar();
            sincronizarTabla();
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Deja activos solo los datos del tipo elegido. */
    private void actualizarCamposPorTipo() {
        boolean esSalida = GastoSalida.TIPO.equals(cmbTipo.getValue());
        boolean esServicio = GastoServicio.TIPO.equals(cmbTipo.getValue());

        boxSalida.setDisable(!esSalida);
        boxServicio.setDisable(!esServicio);
    }

    private void sincronizarTabla() {
        gastosTabla.setAll(repositorio.listar());
    }

    @FXML
    private void onRegistrar() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            lblMensaje.setText("Debe seleccionar el tipo de gasto.");
            return;
        }

        String descripcion = txtDescripcion.getText().trim();
        if (descripcion.isEmpty()) {
            lblMensaje.setText("La descripción del gasto no puede estar vacía.");
            return;
        }

        Integer monto = leerEntero(txtMonto, "El monto total", Gasto.MONTO_MINIMO, Gasto.MONTO_MAXIMO);
        if (monto == null) {
            return;
        }

        Integer participantes = leerEntero(txtParticipantes, "El número de participantes",
                Gasto.PARTICIPANTES_MINIMOS, Gasto.PARTICIPANTES_MAXIMOS);
        if (participantes == null) {
            return;
        }

        Gasto gasto;
        if (GastoSalida.TIPO.equals(tipo)) {
            String lugar = txtLugar.getText().trim();
            if (lugar.isEmpty()) {
                lblMensaje.setText("El lugar de la salida no puede estar vacío.");
                return;
            }
            gasto = new GastoSalida(descripcion, monto, participantes, lugar, chkPropina.isSelected());
        } else {
            String proveedor = txtProveedor.getText().trim();
            if (proveedor.isEmpty()) {
                lblMensaje.setText("El proveedor del servicio no puede estar vacío.");
                return;
            }
            Integer anticipo = leerEntero(txtAnticipo, "El anticipo de pago (días)",
                    GastoServicio.DIAS_MINIMOS, GastoServicio.DIAS_MAXIMOS);
            if (anticipo == null) {
                return;
            }
            GastoServicio servicio = new GastoServicio(descripcion, monto, participantes, proveedor, anticipo);
            if (chkReembolsado.isSelected()) {
                servicio.marcarReembolsado();
            }
            gasto = servicio;
        }

        try {
            repositorio.registrar(gasto);
            sincronizarTabla();
            lblMensaje.setText(gasto.getDescripcion() + " (" + gasto.getTipo() + ") registrado correctamente.");
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
        txtDescripcion.clear();
        txtMonto.clear();
        txtParticipantes.clear();
        txtLugar.clear();
        txtProveedor.clear();
        txtAnticipo.clear();
        chkPropina.setSelected(false);
        chkReembolsado.setSelected(false);
    }

    @FXML
    private void onEliminar() {
        Gasto seleccionado = tblGastos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText("Debe seleccionar un gasto de la tabla para eliminarlo.");
            return;
        }

        try {
            repositorio.eliminar(seleccionado);
            sincronizarTabla();
            lblMensaje.setText(seleccionado.getDescripcion() + " (" + seleccionado.getTipo() + ") eliminado correctamente.");
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }
}
