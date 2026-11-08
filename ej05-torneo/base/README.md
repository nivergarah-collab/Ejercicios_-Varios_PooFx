# Torneo Copa Austral · Proyecto base

Proyecto base del ejercicio de práctica de **Programación Orientada a Objetos (DSY1102)**: construir una aplicación de escritorio con **JavaFX** y archivos **JSON** sobre un modelo de clases ya entregado. El caso completo está en [`ENUNCIADO.md`](../ENUNCIADO.md).

## Cómo abrirlo

1. Abre IntelliJ IDEA y elige **File → Open...**, luego selecciona esta carpeta (`base`).
2. Si IntelliJ lo pregunta, elige **Trust Project** y ábrelo como proyecto Maven.
3. Verifica que el JDK del proyecto sea **21** (**File → Project Structure → Project**).

## Contenido

```
pom.xml                                     Proyecto Maven para Java 21, con JavaFX incluido.
src/main/java/cl/duoc/torneo/
    Launcher.java                           Punto de entrada: ejecuta esta clase para iniciar la aplicación.
    App.java                                Aplicación JavaFX: carga la vista principal y abre la ventana.
src/main/java/cl/duoc/torneo/model/         Modelo del sistema (ya implementado y probado):
    Becable.java
    Inscripcion.java
    InscripcionEquipo.java
    InscripcionIndividual.java
src/main/resources/cl/duoc/torneo/view/     Carpeta para las vistas FXML.
```

## Cómo ejecutar la aplicación

- Ejecuta la clase `Launcher` (botón Run junto al método `main`), o bien `mvn javafx:run` en una terminal con Maven instalado.
- `App` carga la vista principal desde `src/main/resources/cl/duoc/torneo/view/main-view.fxml`. Debes crear ese archivo con Scene Builder. Mientras no exista, la aplicación se detiene con el mensaje "No se encontró la vista principal".

## Lo que debes agregar al `pom.xml`

La dependencia de la librería **Gson**, en el lugar indicado por el comentario. Luego recarga el proyecto Maven (ícono **Load Maven Changes**, o clic derecho en `pom.xml` → **Maven → Reload Project**).

## Importante

- No modifiques la lógica de las clases del modelo.
- Puedes agregar tus propios paquetes, clases y recursos.

## Operaciones disponibles en el modelo

**`Inscripcion`** (clase abstracta)

- `getNombre()` / `setNombre(String)`
- `getPartidosJugados()` / `setPartidosJugados(int)`
- `getRondas()` / `setRondas(int)`
- `calcularCuota()`: cuota de inscripción, en pesos, según el tipo de inscripción.
- `getTipo()`: devuelve `"Equipo"` o `"Individual"`.
- `pesos(int)`: da formato de pesos chilenos (por ejemplo, `$60.000`).
- `toString()`: tipo, nombre, partidos, rondas y cuota.

Los métodos `set` rechazan datos inválidos con un mensaje que explica el motivo: el nombre no puede ser nulo ni vacío, los partidos jugados van entre 0 y 200 y las rondas entre 1 y 12.

**`InscripcionEquipo`** (extiende `Inscripcion`)

- `InscripcionEquipo(nombre, partidosJugados, rondas, capitan, canchaTechada)`
- `getCapitan()` / `setCapitan(String)`
- `isCanchaTechada()` / `setCanchaTechada(boolean)`

**`InscripcionIndividual`** (extiende `Inscripcion`, implementa `Becable`)

- `InscripcionIndividual(nombre, partidosJugados, rondas, ranking, aniosFederado)`
- `getRanking()` / `setRanking(int)`
- `getAniosFederado()` / `setAniosFederado(int)`
- `estaBecado()`
- `marcarBecado()`
