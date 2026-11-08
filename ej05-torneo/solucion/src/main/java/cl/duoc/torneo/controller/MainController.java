package cl.duoc.torneo.controller;

import cl.duoc.torneo.model.Inscripcion;
import cl.duoc.torneo.model.InscripcionEquipo;
import cl.duoc.torneo.model.InscripcionIndividual;
import cl.duoc.torneo.repository.InscripcionRepository;
import cl.duoc.torneo.repository.PersistenciaException;
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

    private static final String TIPO_EQUIPO = "Equipo";
    private static final String TIPO_INDIVIDUAL = "Individual";

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPartidos;
    @FXML private TextField txtRondas;

    @FXML private HBox boxEquipo;
    @FXML private TextField txtCapitan;
    @FXML private CheckBox chkCanchaTechada;

    @FXML private HBox boxIndividual;
    @FXML private TextField txtRanking;
    @FXML private TextField txtAniosFederado;
    @FXML private CheckBox chkBecado;

    @FXML private TableView<Inscripcion> tblInscripciones;
    @FXML private TableColumn<Inscripcion, String> colTipo;
    @FXML private TableColumn<Inscripcion, String> colNombre;
    @FXML private TableColumn<Inscripcion, Integer> colPartidos;
    @FXML private TableColumn<Inscripcion, Integer> colRondas;
    @FXML private TableColumn<Inscripcion, String> colCuota;

    @FXML private Label lblMensaje;

    private final InscripcionRepository repositorio = new InscripcionRepository();
    private final ObservableList<Inscripcion> inscripcionesTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll(TIPO_EQUIPO, TIPO_INDIVIDUAL);

        colTipo.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getTipo()));
        colNombre.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getNombre()));
        colPartidos.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getPartidosJugados()));
        colRondas.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getRondas()));
        colCuota.setCellValueFactory(fila -> new SimpleStringProperty(Inscripcion.pesos(fila.getValue().calcularCuota())));

        cmbTipo.valueProperty().addListener((observable, anterior, nuevo) -> actualizarCamposPorTipo());
        actualizarCamposPorTipo();

        tblInscripciones.setItems(inscripcionesTabla);

        try {
            repositorio.cargar();
            sincronizarTabla();
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Deja activos solo los datos del tipo elegido. */
    private void actualizarCamposPorTipo() {
        boolean esEquipo = TIPO_EQUIPO.equals(cmbTipo.getValue());
        boolean esIndividual = TIPO_INDIVIDUAL.equals(cmbTipo.getValue());

        boxEquipo.setDisable(!esEquipo);
        boxIndividual.setDisable(!esIndividual);
    }

    private void sincronizarTabla() {
        inscripcionesTabla.setAll(repositorio.listar());
    }

    @FXML
    private void onRegistrar() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            lblMensaje.setText("Debe seleccionar el tipo de inscripción.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            lblMensaje.setText("El campo Nombre no puede estar vacío.");
            return;
        }

        Integer partidos = leerEntero(txtPartidos, "Partidos jugados",
                Inscripcion.PARTIDOS_MINIMOS, Inscripcion.PARTIDOS_MAXIMOS);
        if (partidos == null) {
            return;
        }

        Integer rondas = leerEntero(txtRondas, "Rondas",
                Inscripcion.RONDAS_MINIMAS, Inscripcion.RONDAS_MAXIMAS);
        if (rondas == null) {
            return;
        }

        Inscripcion inscripcion;
        if (TIPO_EQUIPO.equals(tipo)) {
            String capitan = txtCapitan.getText().trim();
            if (capitan.isEmpty()) {
                lblMensaje.setText("El campo Capitán no puede estar vacío.");
                return;
            }
            inscripcion = new InscripcionEquipo(nombre, partidos, rondas, capitan, chkCanchaTechada.isSelected());
        } else {
            Integer ranking = leerEntero(txtRanking, "Ranking",
                    InscripcionIndividual.RANKING_MINIMO, InscripcionIndividual.RANKING_MAXIMO);
            if (ranking == null) {
                return;
            }
            Integer anios = leerEntero(txtAniosFederado, "Años federado",
                    InscripcionIndividual.ANIOS_MINIMOS, InscripcionIndividual.ANIOS_MAXIMOS);
            if (anios == null) {
                return;
            }
            InscripcionIndividual individual = new InscripcionIndividual(nombre, partidos, rondas, ranking, anios);
            if (chkBecado.isSelected()) {
                individual.marcarBecado();
            }
            inscripcion = individual;
        }

        try {
            repositorio.registrar(inscripcion);
            sincronizarTabla();
            lblMensaje.setText("Inscripción de " + inscripcion.getNombre() + " (" + inscripcion.getTipo()
                    + ") registrada correctamente.");
            limpiarFormulario();
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Lee un entero del campo; si no sirve, muestra el campo y el motivo, y devuelve null. */
    private Integer leerEntero(TextField campo, String nombreCampo, int minimo, int maximo) {
        int valor;
        try {
            valor = Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            lblMensaje.setText("El campo " + nombreCampo + " debe ser un número entero.");
            return null;
        }
        if (valor < minimo || valor > maximo) {
            lblMensaje.setText("El campo " + nombreCampo + " debe estar entre " + minimo + " y " + maximo + ".");
            return null;
        }
        return valor;
    }

    private void limpiarFormulario() {
        cmbTipo.setValue(null);
        txtNombre.clear();
        txtPartidos.clear();
        txtRondas.clear();
        txtCapitan.clear();
        txtRanking.clear();
        txtAniosFederado.clear();
        chkCanchaTechada.setSelected(false);
        chkBecado.setSelected(false);
    }

    @FXML
    private void onEliminar() {
        Inscripcion seleccionada = tblInscripciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            lblMensaje.setText("Debe seleccionar una inscripción de la tabla para eliminarla.");
            return;
        }

        try {
            repositorio.eliminar(seleccionada);
            sincronizarTabla();
            lblMensaje.setText("Inscripción de " + seleccionada.getNombre() + " (" + seleccionada.getTipo()
                    + ") eliminada correctamente.");
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }
}
