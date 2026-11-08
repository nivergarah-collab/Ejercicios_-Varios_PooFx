# Ejercicio base · Playlist Ritmo Sur

Ejercicio de práctica de **Programación Orientada a Objetos**: construir una aplicación de escritorio con **JavaFX** y archivos **JSON** sobre un modelo de clases ya entregado. El caso completo está en [`ENUNCIADO.md`](ENUNCIADO.md) (en la carpeta del ejercicio, junto a esta base).

> **Aviso importante**
>
> Este material es de práctica, creado con ayuda de inteligencia artificial. No reemplaza ninguna pauta oficial, y los resultados que obtengas aquí no aseguran el mismo resultado en una evaluación real.

## Cómo abrir el proyecto

1. Descomprime o copia la carpeta del proyecto en tu carpeta de trabajo.
2. Abre IntelliJ IDEA y elige **File → Open...**, luego selecciona la carpeta del proyecto.
3. Si IntelliJ lo pregunta, elige **Trust Project** y ábrelo como proyecto Maven.
4. Verifica que el JDK del proyecto sea **21** (**File → Project Structure → Project**).

## Referencia del proyecto base

### Contenido

```
pom.xml                                     Proyecto Maven para Java 21, con JavaFX incluido.
src/main/java/cl/duoc/playlist/
    Launcher.java                           Punto de entrada: ejecuta esta clase para iniciar la aplicación.
    App.java                                Aplicación JavaFX: carga la vista principal y abre la ventana.
src/main/java/cl/duoc/playlist/model/       Modelo del sistema (ya implementado y probado):
    Sincronizable.java
    Pista.java
    Cancion.java
    Podcast.java
src/main/resources/cl/duoc/playlist/view/   Carpeta para las vistas FXML.
```

### Cómo ejecutar la aplicación

- Ejecuta la clase `Launcher` (botón Run junto al método `main`), o desde una terminal: `mvn javafx:run`.
- `App` carga la vista principal desde `src/main/resources/cl/duoc/playlist/view/main-view.fxml`. Debes crear ese archivo con Scene Builder. Mientras no exista, la aplicación se detiene con el mensaje "No se encontró la vista principal".

### Lo que debes agregar al `pom.xml`

La dependencia de la librería **Gson 2.11.0** (`com.google.code.gson:gson`), en el lugar indicado por el comentario, una sola vez. Luego recarga el proyecto Maven (ícono **Load Maven Changes**, o clic derecho en `pom.xml` → **Maven → Reload Project**).

### Importante

- No modifiques la lógica de las clases del modelo.
- Puedes agregar tus propios paquetes, clases y recursos.

### Operaciones disponibles en el modelo

**`Pista`** (clase abstracta)

- `getTitulo()` / `setTitulo(String)`
- `getArtista()` / `setArtista(String)`
- `getDuracionMin()` / `setDuracionMin(int)`
- `calcularCostoLicencia()`: costo de la licencia de la pista, en pesos, según el tipo.
- `getTipo()`: devuelve `"Canción"` o `"Podcast"`.
- `pesos(int)`: da formato de pesos chilenos (por ejemplo `$3.640`).
- `toString()`: tipo, título, artista, duración y costo de licencia.

Los métodos `set` rechazan datos inválidos con un mensaje que explica el motivo: el título y el artista no pueden ser nulos ni vacíos, y la duración va entre 1 y 240 minutos.

**`Cancion`** (extiende `Pista`)

- `Cancion(titulo, artista, duracionMin, album, estreno)`
- `getAlbum()` / `setAlbum(String)`
- `isEstreno()` / `setEstreno(boolean)`

**`Podcast`** (extiende `Pista`, implementa `Sincronizable`)

- `Podcast(titulo, artista, duracionMin, episodio)`
- `getEpisodio()` / `setEpisodio(int)`
- `estaSincronizado()`
- `marcarSincronizado()`
