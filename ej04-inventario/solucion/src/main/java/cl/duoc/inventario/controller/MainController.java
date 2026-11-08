package cl.duoc.inventario.controller;

import cl.duoc.inventario.model.Articulo;
import cl.duoc.inventario.model.Herramienta;
import cl.duoc.inventario.model.Pintura;
import cl.duoc.inventario.repository.ArticuloRepository;
import cl.duoc.inventario.repository.PersistenciaException;
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

    private static final String TIPO_PINTURA = "Pintura";
    private static final String TIPO_HERRAMIENTA = "Herramienta";

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtStock;
    @FXML private TextField txtAnaquel;

    @FXML private HBox boxPintura;
    @FXML private TextField txtLitros;
    @FXML private CheckBox chkAntioxido;

    @FXML private HBox boxHerramienta;
    @FXML private TextField txtMarca;
    @FXML private TextField txtMesesBodega;
    @FXML private CheckBox chkPrestada;

    @FXML private TableView<Articulo> tblArticulos;
    @FXML private TableColumn<Articulo, String> colTipo;
    @FXML private TableColumn<Articulo, String> colNombre;
    @FXML private TableColumn<Articulo, Integer> colStock;
    @FXML private TableColumn<Articulo, Integer> colAnaquel;
    @FXML private TableColumn<Articulo, String> colPrecio;

    @FXML private Label lblMensaje;

    private final ArticuloRepository repositorio = new ArticuloRepository();
    private final ObservableList<Articulo> articulosTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll(TIPO_PINTURA, TIPO_HERRAMIENTA);

        colTipo.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getTipo()));
        colNombre.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getNombre()));
        colStock.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getStock()));
        colAnaquel.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getAnaquel()));
        colPrecio.setCellValueFactory(fila -> new SimpleStringProperty(Articulo.pesos(fila.getValue().calcularPrecioVenta())));

        cmbTipo.valueProperty().addListener((observable, anterior, nuevo) -> actualizarCamposPorTipo());
        actualizarCamposPorTipo();

        tblArticulos.setItems(articulosTabla);

        try {
            repositorio.cargar();
            sincronizarTabla();
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Deja activos solo los datos del tipo elegido. */
    private void actualizarCamposPorTipo() {
        boolean esPintura = TIPO_PINTURA.equals(cmbTipo.getValue());
        boolean esHerramienta = TIPO_HERRAMIENTA.equals(cmbTipo.getValue());

        boxPintura.setDisable(!esPintura);
        boxHerramienta.setDisable(!esHerramienta);
    }

    private void sincronizarTabla() {
        articulosTabla.setAll(repositorio.listar());
    }

    @FXML
    private void onRegistrar() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            lblMensaje.setText("Debe seleccionar el tipo de artículo.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            lblMensaje.setText("El nombre no puede estar vacío.");
            return;
        }

        Integer stock = leerEntero(txtStock, "El stock debe", Articulo.STOCK_MINIMO, Articulo.STOCK_MAXIMO);
        if (stock == null) {
            return;
        }

        Integer anaquel = leerEntero(txtAnaquel, "El anaquel debe", Articulo.ANAQUEL_MINIMO, Articulo.ANAQUEL_MAXIMO);
        if (anaquel == null) {
            return;
        }

        Articulo articulo;
        if (TIPO_PINTURA.equals(tipo)) {
            Integer litros = leerEntero(txtLitros, "Los litros del envase deben", Pintura.LITROS_MINIMOS, Pintura.LITROS_MAXIMOS);
            if (litros == null) {
                return;
            }
            articulo = new Pintura(nombre, stock, anaquel, litros, chkAntioxido.isSelected());
        } else {
            String marca = txtMarca.getText().trim();
            if (marca.isEmpty()) {
                lblMensaje.setText("La marca no puede estar vacía.");
                return;
            }
            Integer meses = leerEntero(txtMesesBodega, "Los meses en bodega deben", 0, Herramienta.MESES_MAXIMOS);
            if (meses == null) {
                return;
            }
            Herramienta herramienta = new Herramienta(nombre, stock, anaquel, marca, meses);
            if (chkPrestada.isSelected()) {
                herramienta.marcarPrestada();
            }
            articulo = herramienta;
        }

        try {
            repositorio.registrar(articulo);
            sincronizarTabla();
            lblMensaje.setText(articulo.getNombre() + " (" + articulo.getTipo() + ") registrado correctamente.");
            limpiarFormulario();
        } catch (PersistenciaException e) {
            lblMensaje.setText("No se registró " + articulo.getNombre() + " (" + articulo.getTipo() + "). "
                    + e.getMessage());
        }
    }

    /** Lee un entero del campo; si no sirve, muestra el campo y el motivo, y devuelve null.
     * El primer parámetro es la frase inicial del mensaje, por ejemplo "El stock debe". */
    private Integer leerEntero(TextField campo, String nombreCampo, int minimo, int maximo) {
        int valor;
        try {
            valor = Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            lblMensaje.setText(nombreCampo + " ser un número entero.");
            return null;
        }
        if (valor < minimo || valor > maximo) {
            lblMensaje.setText(nombreCampo + " estar entre " + minimo + " y " + maximo + ".");
            return null;
        }
        return valor;
    }

    private void limpiarFormulario() {
        cmbTipo.setValue(null);
        txtNombre.clear();
        txtStock.clear();
        txtAnaquel.clear();
        txtLitros.clear();
        txtMarca.clear();
        txtMesesBodega.clear();
        chkAntioxido.setSelected(false);
        chkPrestada.setSelected(false);
    }

    @FXML
    private void onEliminar() {
        Articulo seleccionado = tblArticulos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText("Debe seleccionar un artículo de la tabla para eliminarlo.");
            return;
        }

        try {
            repositorio.eliminar(seleccionado);
            sincronizarTabla();
            lblMensaje.setText(seleccionado.getNombre() + " (" + seleccionado.getTipo() + ") eliminado correctamente.");
        } catch (PersistenciaException e) {
            lblMensaje.setText("No se eliminó " + seleccionado.getNombre() + " (" + seleccionado.getTipo() + "). "
                    + e.getMessage());
        }
    }
}
