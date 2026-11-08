# Ejercicio base · Centro Deportivo Los Aromos

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
pom.xml                                       Proyecto Maven para Java 21, con JavaFX incluido.
src/main/java/cl/duoc/turnos/
    Launcher.java                             Punto de entrada: ejecuta esta clase para iniciar la aplicación.
    App.java                                  Aplicación JavaFX: carga la vista principal y abre la ventana.
src/main/java/cl/duoc/turnos/model/           Modelo del sistema (ya implementado y probado):
    Confirmable.java
    Turno.java
    TurnoCancha.java
    TurnoSala.java
src/main/resources/cl/duoc/turnos/view/       Carpeta para las vistas FXML.
```

### Cómo ejecutar la aplicación

- Ejecuta la clase `Launcher` (botón Run junto al método `main`), o desde una terminal: `mvn javafx:run`.
- `App` carga la vista principal desde `src/main/resources/cl/duoc/turnos/view/main-view.fxml`. Debes crear ese archivo con Scene Builder. Mientras no exista, la aplicación se detiene con el mensaje "No se encontró la vista principal".

### Lo que debes agregar al `pom.xml`

La dependencia de la librería **Gson 2.11.0** (`com.google.code.gson:gson`), en el lugar indicado por el comentario, una sola vez. Luego recarga el proyecto Maven (ícono **Load Maven Changes**, o clic derecho en `pom.xml` → **Maven → Reload Project**).

### Importante

- No modifiques la lógica de las clases del modelo.
- Puedes agregar tus propios paquetes, clases y recursos.

### Operaciones disponibles en el modelo

**`Turno`** (clase abstracta)

- `getCliente()` / `setCliente(String)`
- `getHoraInicio()` / `setHoraInicio(int)`
- `getHoras()` / `setHoras(int)`
- `calcularTotal()`: total a pagar por el turno completo, según el tipo.
- `getTipo()`: devuelve `"Cancha"` o `"Sala"`.
- `pesos(int)`: da formato de pesos chilenos (por ejemplo `$30.000`).
- `toString()`: tipo, cliente, hora de inicio, horas y total.

Los métodos `set` rechazan datos inválidos con un mensaje que explica el motivo: el cliente no puede ser nulo ni vacío, la hora de inicio va entre las 8 y las 21 horas y las horas del turno entre 1 y 6.

**`TurnoCancha`** (extiende `Turno`)

- `TurnoCancha(cliente, horaInicio, horas, numeroCancha, conIluminacion)`
- `getNumeroCancha()` / `setNumeroCancha(int)`
- `isConIluminacion()` / `setConIluminacion(boolean)`

**`TurnoSala`** (extiende `Turno`, implementa `Confirmable`)

- `TurnoSala(cliente, horaInicio, horas, asistentes, diasAnticipacion)`
- `getAsistentes()` / `setAsistentes(int)`
- `getDiasAnticipacion()` / `setDiasAnticipacion(int)`
- `estaConfirmado()`
- `confirmar()`
