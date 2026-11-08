package cl.duoc.biblioteca.controller;

import cl.duoc.biblioteca.model.Prestamo;
import cl.duoc.biblioteca.model.PrestamoAudiovisual;
import cl.duoc.biblioteca.model.PrestamoLibro;
import cl.duoc.biblioteca.repository.PersistenciaException;
import cl.duoc.biblioteca.repository.PrestamoRepository;
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

    private static final String TIPO_LIBRO = "Libro";
    private static final String TIPO_AUDIOVISUAL = "Audiovisual";

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtSocio;
    @FXML private TextField txtDias;
    @FXML private TextField txtMateriales;

    @FXML private HBox boxLibro;
    @FXML private TextField txtTitulo;
    @FXML private CheckBox chkNovedad;
    @FXML private CheckBox chkRenovado;

    @FXML private HBox boxAudiovisual;
    @FXML private TextField txtFormato;
    @FXML private TextField txtDuracion;

    @FXML private TableView<Prestamo> tblPrestamos;
    @FXML private TableColumn<Prestamo, String> colTipo;
    @FXML private TableColumn<Prestamo, String> colSocio;
    @FXML private TableColumn<Prestamo, Integer> colDias;
    @FXML private TableColumn<Prestamo, Integer> colMateriales;
    @FXML private TableColumn<Prestamo, String> colTarifa;

    @FXML private Label lblMensaje;

    private final PrestamoRepository repositorio = new PrestamoRepository();
    private final ObservableList<Prestamo> prestamosTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll(TIPO_LIBRO, TIPO_AUDIOVISUAL);

        colTipo.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getTipo()));
        colSocio.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getSocio()));
        colDias.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getDiasPrestamo()));
        colMateriales.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getMateriales()));
        colTarifa.setCellValueFactory(fila -> new SimpleStringProperty(Prestamo.pesos(fila.getValue().calcularTarifa())));

        cmbTipo.valueProperty().addListener((observable, anterior, nuevo) -> actualizarCamposPorTipo());
        actualizarCamposPorTipo();

        tblPrestamos.setItems(prestamosTabla);

        try {
            repositorio.cargar();
            sincronizarTabla();
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Deja activos solo los datos del tipo elegido. */
    private void actualizarCamposPorTipo() {
        boolean esLibro = TIPO_LIBRO.equals(cmbTipo.getValue());
        boolean esAudiovisual = TIPO_AUDIOVISUAL.equals(cmbTipo.getValue());

        boxLibro.setDisable(!esLibro);
        boxAudiovisual.setDisable(!esAudiovisual);
    }

    private void sincronizarTabla() {
        prestamosTabla.setAll(repositorio.listar());
    }

    @FXML
    private void onRegistrar() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            lblMensaje.setText("Debe seleccionar el tipo de préstamo.");
            return;
        }

        String socio = txtSocio.getText().trim();
        if (socio.isEmpty()) {
            lblMensaje.setText("El nombre del socio no puede estar vacío.");
            return;
        }

        Integer dias = leerEntero(txtDias, "El plazo de préstamo", "días", 1, 30);
        if (dias == null) {
            return;
        }

        Integer materiales = leerEntero(txtMateriales, "La cantidad de materiales", "", 1, 5);
        if (materiales == null) {
            return;
        }

        Prestamo prestamo;
        if (TIPO_LIBRO.equals(tipo)) {
            String titulo = txtTitulo.getText().trim();
            if (titulo.isEmpty()) {
                lblMensaje.setText("El título del libro no puede estar vacío.");
                return;
            }
            PrestamoLibro libro = new PrestamoLibro(socio, dias, materiales, titulo, chkNovedad.isSelected());
            if (chkRenovado.isSelected()) {
                libro.renovar();
            }
            prestamo = libro;
        } else {
            String formato = txtFormato.getText().trim();
            if (formato.isEmpty()) {
                lblMensaje.setText("El formato no puede estar vacío.");
                return;
            }
            Integer duracion = leerEntero(txtDuracion, "La duración", "minutos", 1, 600);
            if (duracion == null) {
                return;
            }
            prestamo = new PrestamoAudiovisual(socio, dias, materiales, formato, duracion);
        }

        try {
            repositorio.registrar(prestamo);
            sincronizarTabla();
            lblMensaje.setText("Préstamo de " + prestamo.getSocio() + " (" + prestamo.getTipo()
                    + ") registrado correctamente.");
            limpiarFormulario();
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Lee un entero del campo; si no sirve, muestra el campo y el motivo, y devuelve null. */
    private Integer leerEntero(TextField campo, String nombreCampo, String unidad, int minimo, int maximo) {
        int valor;
        try {
            valor = Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            lblMensaje.setText(nombreCampo + " debe ser un número entero.");
            return null;
        }
        if (valor < minimo || valor > maximo) {
            String sufijo = unidad.isEmpty() ? "" : " " + unidad;
            lblMensaje.setText(nombreCampo + " debe estar entre " + minimo + " y " + maximo + sufijo + ".");
            return null;
        }
        return valor;
    }

    private void limpiarFormulario() {
        cmbTipo.setValue(null);
        txtSocio.clear();
        txtDias.clear();
        txtMateriales.clear();
        txtTitulo.clear();
        txtFormato.clear();
        txtDuracion.clear();
        chkNovedad.setSelected(false);
        chkRenovado.setSelected(false);
    }

    @FXML
    private void onEliminar() {
        Prestamo seleccionado = tblPrestamos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText("Debe seleccionar un préstamo de la tabla para eliminarlo.");
            return;
        }

        try {
            repositorio.eliminar(seleccionado);
            sincronizarTabla();
            lblMensaje.setText("Préstamo de " + seleccionado.getSocio() + " (" + seleccionado.getTipo()
                    + ") eliminado correctamente.");
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }
}
