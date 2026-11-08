package cl.duoc.playlist.controller;

import cl.duoc.playlist.model.Cancion;
import cl.duoc.playlist.model.Pista;
import cl.duoc.playlist.model.Podcast;
import cl.duoc.playlist.repository.PersistenciaException;
import cl.duoc.playlist.repository.PistaRepository;
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
    @FXML private TextField txtArtista;
    @FXML private TextField txtDuracion;

    @FXML private HBox boxCancion;
    @FXML private TextField txtAlbum;
    @FXML private CheckBox chkEstreno;

    @FXML private HBox boxPodcast;
    @FXML private TextField txtEpisodio;
    @FXML private CheckBox chkSincronizado;

    @FXML private TableView<Pista> tblPistas;
    @FXML private TableColumn<Pista, String> colTipo;
    @FXML private TableColumn<Pista, String> colTitulo;
    @FXML private TableColumn<Pista, String> colArtista;
    @FXML private TableColumn<Pista, Integer> colDuracion;
    @FXML private TableColumn<Pista, String> colLicencia;

    @FXML private Label lblMensaje;

    private final PistaRepository repositorio = new PistaRepository();
    private final ObservableList<Pista> pistasTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll(Cancion.TIPO, Podcast.TIPO);

        colTipo.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getTipo()));
        colTitulo.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getTitulo()));
        colArtista.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().getArtista()));
        colDuracion.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue().getDuracionMin()));
        colLicencia.setCellValueFactory(fila -> new SimpleStringProperty(Pista.pesos(fila.getValue().calcularCostoLicencia())));

        cmbTipo.valueProperty().addListener((observable, anterior, nuevo) -> actualizarCamposPorTipo());
        actualizarCamposPorTipo();

        tblPistas.setItems(pistasTabla);

        try {
            repositorio.cargar();
            sincronizarTabla();
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    /** Deja activos solo los datos del tipo elegido. */
    private void actualizarCamposPorTipo() {
        boolean esCancion = Cancion.TIPO.equals(cmbTipo.getValue());
        boolean esPodcast = Podcast.TIPO.equals(cmbTipo.getValue());

        boxCancion.setDisable(!esCancion);
        boxPodcast.setDisable(!esPodcast);
    }

    private void sincronizarTabla() {
        pistasTabla.setAll(repositorio.listar());
    }

    @FXML
    private void onRegistrar() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            lblMensaje.setText("Debe seleccionar el tipo de pista.");
            return;
        }

        String titulo = txtTitulo.getText().trim();
        if (titulo.isEmpty()) {
            lblMensaje.setText("El título de la pista no puede estar vacío.");
            return;
        }

        String artista = txtArtista.getText().trim();
        if (artista.isEmpty()) {
            lblMensaje.setText("El nombre del artista no puede estar vacío.");
            return;
        }

        Integer duracion = leerEntero(txtDuracion, "La duración (minutos)", 1, 240);
        if (duracion == null) {
            return;
        }

        Pista pista;
        if (Cancion.TIPO.equals(tipo)) {
            String album = txtAlbum.getText().trim();
            if (album.isEmpty()) {
                lblMensaje.setText("El nombre del álbum no puede estar vacío.");
                return;
            }
            pista = new Cancion(titulo, artista, duracion, album, chkEstreno.isSelected());
        } else {
            Integer episodio = leerEntero(txtEpisodio, "El número de episodio", 1, 500);
            if (episodio == null) {
                return;
            }
            Podcast podcast = new Podcast(titulo, artista, duracion, episodio);
            if (chkSincronizado.isSelected()) {
                podcast.marcarSincronizado();
            }
            pista = podcast;
        }

        try {
            repositorio.registrar(pista);
            sincronizarTabla();
            lblMensaje.setText(pista.getTitulo() + " (" + pista.getTipo() + ") registrada correctamente.");
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
        txtArtista.clear();
        txtDuracion.clear();
        txtAlbum.clear();
        txtEpisodio.clear();
        chkEstreno.setSelected(false);
        chkSincronizado.setSelected(false);
    }

    @FXML
    private void onEliminar() {
        Pista seleccionada = tblPistas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            lblMensaje.setText("Debe seleccionar una pista de la tabla para eliminarla.");
            return;
        }

        try {
            repositorio.eliminar(seleccionada);
            sincronizarTabla();
            lblMensaje.setText(seleccionada.getTitulo() + " (" + seleccionada.getTipo() + ") eliminada correctamente.");
        } catch (PersistenciaException e) {
            lblMensaje.setText(e.getMessage());
        }
    }
}
