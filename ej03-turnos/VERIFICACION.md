# VERIFICACION · ej03-turnos

Generado de forma automática y desatendida (sin preguntas). Fecha: 2026-11-08.

## 1. Supuestos y decisiones

**Inspiración tomada de Stevens ej03 (solo lectura: README)** y cómo se abstrajo:

| Idea de Stevens ej03 | Qué hice en este ejercicio (original) |
|---|---|
| Tema: agendar turnos con fecha y hora, evitando choques de horario | Se conserva el tema "turnos horarios", pero como arriendo de canchas y salas de un centro deportivo. No hay detección de choques (no cabe en la rúbrica). |
| Entidades Doctor, Paciente y Turno relacionadas; ComboBox de relación; ventana modal; DatePicker; celdas coloreadas | **Descartado** (no cabe en la rúbrica). Una sola entidad, una sola ventana, sin relaciones. |
| Gestor en memoria con HashMap por clave compuesta | Sustituido por el patrón DAO + Repository con una lista por subtipo (plantilla de la solución). |

No se copió ninguna clase, nombre, atributo, dato ni redacción de Stevens, de la Librería ni de la veterinaria (estas dos se usaron solo como plantilla de forma).

Decisiones de diseño (misma forma que la Librería Rincón Lector):

- `Turno` (abstracta): `cliente` (no vacío), `horaInicio` (8–21), `horas` (1–6) validados en los setters con `IllegalArgumentException` con motivo; `calcularTotal()` y `getTipo()` abstractos.
- `TurnoCancha`: `numeroCancha` (1–8), `conIluminacion`. Tarifa $12.000 por hora, +25 % sobre el total si hay iluminación.
- `TurnoSala` (implementa `Confirmable`): `asistentes` (1–40), `diasAnticipacion` (0–60). Tarifa $8.000 por hora, −15 % sobre el total si la anticipación es de 7 días o más. El estado `confirmado` lo gestiona la interfaz (como `listaParaDevolucion` en la Librería).
- `Confirmable`: `estaConfirmado()` y `confirmar()`.
- Supuesto: los atributos propios también se validan en sus setters, para que el DAO pueda revalidar lo leído del JSON (Gson no llama a los setters).
- Supuesto: el valor calculado es el total del turno completo (tarifa por hora × horas ± porcentaje), en pesos enteros; todos los datos de prueba dan enteros limpios.
- Archivos JSON: `canchas.json` y `salas.json` en la raíz del proyecto (ignorados en `.gitignore`).
- Paquete `cl.duoc.turnos`, artifactId `turnos`, clase principal `cl.duoc.turnos.Launcher`, vista en `cl/duoc/turnos/view/main-view.fxml`.
- Los mensajes de error de campo del controlador usan nombres en singular ("La duración del turno (en horas) debe estar entre 1 y 6.") para que la concordancia sea correcta. Los rangos salen de las constantes del modelo.
- El `ENUNCIADO.md` no incluye imágenes ni menciona `ENTREGA.md`; dice que la entrega es según las instrucciones de quien dicta el ejercicio. No se crearon `.github/workflows` ni archivos COPIAR-A-….
- Los valores esperados del enunciado salen de ejecutar la solución (Prueba y UiPrueba), no de cálculos a mano.

## 2. Casillas de la rúbrica (RUBRICA.md, adaptada a Turno/TurnoCancha/TurnoSala)

Pruebas: **T1** = `Prueba` (repository/DAO, 27 comprobaciones), **T2** = `UiPrueba` (FXML + controller reales cargados con JavaFX bajo xvfb, 34 comprobaciones), **T3** = `javafx:run` (código 124 = abre), **T4** = chequeo estático (script Python), **T5** = revisión de código leyendo los archivos.

| N° | Criterio | Casillas | Evidencia |
|---|---|---|---|
| 1 | Configuración | 4/4 | Gson 2.11.0 una sola vez junto a JavaFX sin cambios (T4, `solucion/pom.xml`); `mvn compile` OK en base y solución; `Launcher` y `App` de la solución idénticos a los de la base (diff); `javafx:run` abre (T3); vista cargada desde `/cl/duoc/turnos/view/main-view.fxml` sin alterar `App` |
| 2 | Vista FXML | 3/4, 1 NO VERIFICADO | Ventana declarada en `main-view.fxml` (T5); selector, comunes y propios de ambos tipos, tabla de 5 columnas, botones Registrar/Eliminar y `lblMensaje` presentes (T2 los usa todos). Casilla "cada grupo se distingue con claridad": hay títulos, `Separator` y agrupación en `GridPane`/`HBox`, pero **NO VERIFICADO visualmente** (no se miró captura) |
| 3 | Vista–controlador | 4/4 | `fx:controller` en el FXML; 18 `fx:id` = 18 `@FXML`, nombres y tipos coinciden uno a uno; botones con `onAction` y sin `fx:id` (T4); T2 carga el FXML sin error |
| 4 | Inicio y eventos | 4/4 | T2: selector con Cancha/Sala, habilitación por tipo, Registrar y Eliminar funcionan; Eliminar quita solo la fila seleccionada; sin selección avisa y conserva |
| 5 | Mensajes de resultado | 4/4 | T2: registro, eliminación y falta de selección en el Label, cada uno reemplaza al anterior e identifica turno o causa; error de lectura (JSON corrupto al iniciar) y de escritura (JSON es carpeta) aparecen en el Label (T2); sin `System.out/err` (T4) |
| 6 | Paquetes y capas | 4/4 | `controller`, `model`, `repository`, FXML en `resources/.../view`; `model` y `repository` sin imports de javafx ni del controller (T4); en la raíz solo `App` y `Launcher` entregados |
| 7 | Controlador solo coordina | 4/4 | T4/T5: usa `calcularTotal()` del modelo (sin recalcular), pide todo al `TurnoRepository`, no importa java.io/nio/gson |
| 8 | Tabla y lista observable | 4/4 | T2: misma `ObservableList` antes y después (identidad), `setAll` tras cada operación, aparece y desaparece al instante, sin duplicados, 5 columnas, columna total igual a `calcularTotal()` para los 4 turnos |
| 9 | Validación | 4/4 | T2: tipo, cliente en blanco, número no entero, rangos de hora, horas, cancha, asistentes y días; sala se registra aunque el campo de cancha tenga basura (solo se validan los campos del tipo); sin registro ni excepción no controlada |
| 10 | Mensajes de error | 4/4 | T2: mensajes en el Label nombran campo y motivo (p. ej. "La hora de inicio debe ser un número entero."); valores conservados tras error; formulario limpio tras registro exitoso |
| 11 | DAO y Repository | 4/4 | `TurnoDao`, `GsonTurnoDao` (canchas.json y salas.json), `TurnoRepository` con dos listas y sin JavaFX; archivos solo vía DAO (T4, T5) |
| 12 | Carga y guardado | 4/4 | T1: inicio sin archivos con lista vacía; ambos JSON se crean; reabrir muestra lo guardado; registrar y eliminar guardan sin acción extra; escritura en carpeta lanza `PersistenciaException` con memoria intacta; fallo al eliminar con DAO falso deshace |
| 13 | Flujo completo | 4/4 | T1/T2: los 4 datos de prueba con totales $30.000, $12.000, $20.400 y $8.000; eliminar Hugo Pardo y reabrir → ya no aparece; Camila Fuentes sigue confirmada al reabrir. La secuencia se probó por controller/API, no escribiendo a mano en la ventana |

**Total: 52/52 por código y pruebas, con la salvedad de la casilla 2.4 (claridad visual) NO VERIFICADA → 51/52 estrictamente verificadas.**

## 3. Resultado de las pruebas (contenedor: Java 21, Maven 3.9.11, xvfb)

- `mvn -q compile` base: OK. `mvn -q compile` solución: OK (también tras el último cambio de mensajes).
- Base con `xvfb-run mvn javafx:run`: falla con `IllegalStateException: No se encontró la vista principal: /cl/duoc/turnos/view/main-view.fxml` (esperado). Sin xvfb falla antes con "Unable to open DISPLAY" (solo por falta de pantalla).
- Solución: `timeout 25 xvfb-run -a mvn -q javafx:run` → código 124 (la ventana abre y sigue viva).
- T1 `Prueba`: 27/27 OK (inicio vacío, 4 totales, 2 JSON creados, `listar()` copia, reabrir 4, confirmación persistida, eliminar y reabrir, validaciones del modelo, JSON corrupto, horas 0 / cliente vacío / cancha 99 / asistentes 0 dentro del JSON, JSON-carpeta, fallo al eliminar con DAO falso).
- T2 `UiPrueba`: 34/34 OK. En una primera corrida un chequeo falló por un índice mal escrito en la propia prueba (corregido) y se ajustó la redacción de tres mensajes de campo para que la concordancia fuera correcta.
- T4: 18 fx:id = 18 @FXML con tipos coincidentes; modelo y repository sin javafx/controller; Gson una vez; controlador sin java.io/nio/gson ni System.out.
- Las pruebas usan las clases compiladas y el classpath de Maven en un directorio aparte; no dejaron JSON en los proyectos entregados.

## 4. NO VERIFICADO / pendiente

- Aspecto visual de la ventana (no se tomó ni revisó captura): disposición, tamaños y claridad de los grupos.
- Ejecución real en IntelliJ/Windows con JDK 21 (el PC solo tiene Java 11 sin Maven; se probó en contenedor Linux).
- Escritura manual en los campos de la ventana real: la UI se probó cargando el FXML y accionando el controller por reflexión.
- `mvn verify` no se ejecutó (sí `compile` y `javafx:run`).
- No se creó ningún temporal `tmp_ej03.tgz` en el PC (las plantillas se leyeron con `cat`).
