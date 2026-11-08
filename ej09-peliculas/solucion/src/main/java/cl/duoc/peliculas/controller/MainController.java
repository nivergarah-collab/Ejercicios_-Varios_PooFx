package cl.duoc.peliculas.controller;

import cl.duoc.peliculas.model.Funcion;
import cl.duoc.peliculas.model.FuncionPopular;
import cl.duoc.peliculas.model.FuncionRegular;
import cl.duoc.peliculas.repository.FuncionRepository;
import cl.duoc.peliculas.repository.PersistenciaException;
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
    @FXML private TextField txtPelicula;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtEntradas;

    @FXML private HBox boxRegular;
    @FXML private TextField txtSala;
    @FXML private CheckBox chk3D;

    @FXML private HBox boxPopular;
    @FXML private TextField txtDistribuidora;
    @FXML private TextField txtDias;
    @FXML private CheckBox chkReembolsada;

    @FXML private TableView<Funcion> tblFunciones;
    @FXML private TableColumn<Funcion, String> colTipo;
    @FXML private TableColumn<Funcion, String> colPelicula;
    @FXML private TableColumn<Funcion, Integer> colDuracion;
    @FXML private TableColumn<Funcion, Integer> colEntradas;
    @FXML private TableColumn<Funcion, String> colPrecio;

    @FXML private Label lblMensaje;

    private final FuncionRepository repositorio = new FuncionRepository();
    private final ObservableList<Funcion> funcionesTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll(FuncionRegular.TIPO, FuncionPopular.TIPO);

        colTipo.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getTipo()));
        colPelicula.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getPelicula()));
        colDuracion.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getDuracionMin()));
        colEntradas.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getEntradas()));
        colPrecio.setCellValueFactory(fila -> new SimpleStringProperty(Funcion.pesos(fila.getValue().calcularPrecioEntrada())));

        cmbTipo.valueProperty().addListener((observable, anterior, nuevo) -> actualizarCamposPorTipo());
        actualizarCamposPorTipo();

        tblFunciones.setItems(funcionesTabla);

        try {
            repositorio.cargar();
            sincronizarTabla();
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Deja activos solo los datos del tipo elegido. */
    private void actualizarCamposPorTipo() {
        boolean esRegular = FuncionRegular.TIPO.equals(cmbTipo.getValue());
        boolean esPopular = FuncionPopular.TIPO.equals(cmbTipo.getValue());

        boxRegular.setDisable(!esRegular);
        boxPopular.setDisable(!esPopular);
    }

    private void sincronizarTabla() {
        funcionesTabla.setAll(repositorio.listar());
    }

    @FXML
    private void onRegistrar() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            lblMensaje.setText("Debe seleccionar el tipo de función.");
            return;
        }

        String pelicula = txtPelicula.getText().trim();
        if (pelicula.isEmpty()) {
            lblMensaje.setText("El nombre de la película no puede estar vacío.");
            return;
        }

        Integer duracion = leerEntero(txtDuracion, "La duración (minutos)", 60, 240);
        if (duracion == null) {
            return;
        }

        Integer entradas = leerEntero(txtEntradas, "La cantidad de entradas vendidas", 1, 300);
        if (entradas == null) {
            return;
        }

        Funcion funcion;
        if (FuncionRegular.TIPO.equals(tipo)) {
            String sala = txtSala.getText().trim();
            if (sala.isEmpty()) {
                lblMensaje.setText("El nombre de la sala no puede estar vacío.");
                return;
            }
            funcion = new FuncionRegular(pelicula, duracion, entradas, sala, chk3D.isSelected());
        } else {
            String distribuidora = txtDistribuidora.getText().trim();
            if (distribuidora.isEmpty()) {
                lblMensaje.setText("El nombre de la distribuidora no puede estar vacío.");
                return;
            }
            Integer dias = leerEntero(txtDias, "La cantidad de días en cartelera", 0, 365);
            if (dias == null) {
                return;
            }
            FuncionPopular popular = new FuncionPopular(pelicula, duracion, entradas, distribuidora, dias);
            if (chkReembolsada.isSelected()) {
                popular.marcarReembolsada();
            }
            funcion = popular;
        }

        try {
            repositorio.registrar(funcion);
            sincronizarTabla();
            lblMensaje.setText(funcion.getPelicula() + " (" + funcion.getTipo() + ") registrada correctamente.");
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
        txtPelicula.clear();
        txtDuracion.clear();
        txtEntradas.clear();
        txtSala.clear();
        txtDistribuidora.clear();
        txtDias.clear();
        chk3D.setSelected(false);
        chkReembolsada.setSelected(false);
    }

    @FXML
    private void onEliminar() {
        Funcion seleccionada = tblFunciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            lblMensaje.setText("Debe seleccionar una función de la tabla para eliminarla.");
            return;
        }

        try {
            repositorio.eliminar(seleccionada);
            sincronizarTabla();
            lblMensaje.setText(seleccionada.getPelicula() + " (" + seleccionada.getTipo() + ") eliminada correctamente.");
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }
}
