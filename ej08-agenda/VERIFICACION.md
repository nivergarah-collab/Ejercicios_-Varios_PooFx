# VERIFICACION · ej08-agenda

Generado de forma automática y desatendida (sin preguntas). Fecha: 2026-11-08.

## 1. Supuestos y decisiones

**Inspiración tomada de Stevens ej08 (solo lectura: README)** y cómo se abstrajo:

| Idea de Stevens ej08 | Qué hice en este ejercicio (original) |
|---|---|
| Tema "agenda de contactos" (entidad Contacto: nombre, teléfono, email) | Se conservó solo el tema "agenda", pero la entidad es un **compromiso** de agenda de oficina (Reunión / Evento). No hay contactos, teléfonos ni emails. |
| Ventana secundaria de edición, `FilteredList` de búsqueda, regex de email, orden por columna, contador, exportar CSV, `GestorAgenda` con `HashMap` | **Descartado** (no cabe en la rúbrica: una sola ventana, un solo FXML, sin filtros ni relaciones). |
| Validar el formato de un dato con una regla explícita | Se tradujo a las validaciones con rangos exigidas por la rúbrica (duración 15–480, asistentes 1–200, anticipación 0–365). No se copió ninguna clase, nombre, regla, dato de prueba ni redacción. |

Decisiones de diseño (misma forma que la Librería Rincón Lector, reglas y cifras propias):

- `Compromiso` (abstracta): `titulo` (no vacío), `duracionMin` (15–480), `asistentes` (1–200) validados en los setters con `IllegalArgumentException` con motivo; `calcularCostoPorAsistente()` y `getTipo()` abstractos.
- `Reunion`: `organizador`, `virtual`. Costo base $7.500 por asistente, −40 % si es virtual.
- `Evento` (implementa `Confirmable`): `lugar`, `diasAnticipacion` (0–365). Costo base $9.000 por asistente, +35 % si se agenda con 7 días o menos de anticipación. El estado `confirmado` lo gestiona la interfaz.
- `Confirmable`: `estaConfirmado()` y `confirmar()` (solo la implementa `Evento`).
- Supuesto: los atributos propios de las subclases también se validan en sus setters, de modo que el DAO revalida lo leído de JSON usando los setters (Gson no los llama).
- Supuesto: el tipo se muestra con tilde ("Reunión") y no se guarda en el JSON (sale de `getTipo()`).
- Supuesto: mensajes en masculino ("registrado/eliminado correctamente") porque la entidad es "compromiso".
- Archivos JSON: `reuniones.json` y `eventos.json` en la raíz del proyecto (ignorados en `.gitignore`).
- Paquete `cl.duoc.agenda`, artifactId `agenda`, clase principal `cl.duoc.agenda.Launcher`, vista en `cl/duoc/agenda/view/main-view.fxml`.
- La estructura de repository/controller/FXML se adaptó a partir de `Ejercicios_base/ej01-veterinaria/solucion` (plantilla de forma y calidad), cambiando dominio, nombres, reglas y textos. No se crearon `.github/workflows` ni archivos COPIAR-A-….
- El `ENUNCIADO.md` no incluye imágenes y dice que la entrega se hace según las instrucciones de quien dicta el ejercicio.

Datos de prueba y costos obtenidos **ejecutando la solución** (no a mano): Revisión de presupuesto (Reunión presencial) $7.500; Coordinación con proveedores (Reunión virtual) $4.500; Cena de aniversario (Evento, 3 días) $12.150; Taller de capacitación (Evento, 30 días) $9.000.

## 2. Casillas de la rúbrica (RUBRICA.md, adaptada a Compromiso/Reunión/Evento)

Pruebas: **T1** = `Prueba` (modelo/repository/DAO, 37 comprobaciones), **T2** = `UiPrueba` (FXML + controller reales cargados con JavaFX bajo xvfb, 34 comprobaciones), **T3** = `javafx:run` (exit 124 = abre), **T4** = chequeo estático (script Python + grep), **T5** = revisión de código leyendo los archivos.

| N° | Criterio | Casillas | Evidencia |
|---|---|---|---|
| 1 | Configuración | 4/4 | Gson 2.11.0 una sola vez junto a JavaFX sin cambios (T4, `solucion/pom.xml`); compila base y solución; `Launcher`/`App` iguales a la base (solo se copiaron); `javafx:run` abre (T3); vista cargada desde `/cl/duoc/agenda/view/main-view.fxml` |
| 2 | Vista FXML | 3/4 verificadas + 1 NO VERIFICADO | FXML declarado (T5); selector, comunes, propios de ambos tipos, tabla de 5 columnas, botones y `lblMensaje` (T2 usa todos). "Cada grupo se distingue con claridad": hay títulos, `Separator`, `GridPane`/`HBox`, pero **NO VERIFICADO visualmente** (no se miró captura) |
| 3 | Vista–controlador | 4/4 | `fx:controller` presente; 18 `fx:id` = 18 `@FXML`, tipos coinciden, botones con `onAction` y sin `fx:id` (T4); T2 carga el FXML sin error |
| 4 | Inicio y eventos | 4/4 | T2: selector con Reunión/Evento; habilitación por tipo; Registrar y Eliminar funcionan; Eliminar quita solo la fila seleccionada; sin selección avisa y conserva |
| 5 | Mensajes de resultado | 4/4 | T2: éxito, eliminación, falta de selección, error de lectura ("Error al leer archivo reuniones.json") y de escritura en el Label, cada uno reemplaza al anterior; sin `System.out/err` (T4) |
| 6 | Paquetes y capas | 4/4 | `controller`, `model`, `repository`, FXML en `resources/.../view`; `model` y `repository` sin javafx ni controller (T4); raíz solo `App`/`Launcher` entregados |
| 7 | Controlador solo coordina | 4/4 | T4/T5: usa `calcularCostoPorAsistente()` del modelo, rangos tomados de constantes del modelo, pide todo al `CompromisoRepository`, sin `java.io/nio/gson` |
| 8 | Tabla y lista observable | 4/4 | T2: misma `ObservableList` antes y después, `setAll` tras cada operación, aparece/desaparece al instante, columna costo `[$7.500,$4.500,$12.150,$9.000]` proveniente del modelo |
| 9 | Validación | 4/4 | T2: tipo, título en blanco, no entero, rangos (14/481/0/201/366), solo campos del tipo elegido (un Evento se registra con el campo organizador vacío), sin excepciones no controladas |
| 10 | Mensajes de error | 4/4 | T2: mensajes en el Label con campo y motivo (p. ej. "La duración (minutos) debe ser un número entero."); valores conservados tras error; formulario vacío tras éxito |
| 11 | DAO y Repository | 4/4 | `CompromisoDao`, `GsonCompromisoDao` (reuniones.json / eventos.json), `CompromisoRepository` con dos listas y un DAO por lista, sin JavaFX, archivos solo vía DAO (T4, T5) |
| 12 | Carga y guardado | 4/4 | T1/T2: inicio sin archivos vacío y sin error; ambos JSON se crean; reabrir muestra lo guardado; registrar/eliminar guardan solos; escritura con carpeta → `PersistenciaException`, memoria intacta, sin éxito informado; fallo al eliminar con DAO falso → deshace |
| 13 | Flujo completo | 4/4 | T1/T2: 4 datos de prueba con costos $7.500, $4.500, $12.150, $9.000; eliminar "Coordinación" y reabrir → ya no aparece; la Cena sigue confirmada tras reabrir. La secuencia se ejecutó por controller/API, no tecleando en la ventana |

**Total: 52/52 por código y pruebas, con la salvedad de la casilla 2.4 (claridad visual) NO VERIFICADA visualmente → 51/52 estrictamente verificadas.**

## 3. Resultado de las pruebas (contenedor: Java 21, Maven 3.9.11, xvfb)

- `mvn -q compile` base: OK. `mvn -q compile` solución: OK.
- Base: `javafx:run` falla con `IllegalStateException: No se encontró la vista principal: /cl/duoc/agenda/view/main-view.fxml` (esperado).
- Solución: `timeout 25 xvfb-run -a mvn -q javafx:run` → exit 124 (la ventana abre y sigue viva).
- T1: 37/37 OK (inicio sin archivos; costos esperados y límite de 7 días; `listar()` copia; JSON creados; reabrir; confirmación y virtual persistidos; eliminar y reabrir; JSON corrupto, duración inválida, organizador faltante y anticipación inválida en JSON → `PersistenciaException`; JSON-carpeta → `PersistenciaException` con memoria intacta; fallo al eliminar con DAO falso → memoria intacta).
- T2: 34/34 OK.
- T4: 18 `fx:id` = 18 `@FXML`; tipos coinciden; sin `fx:id` en botones; modelo/repository sin javafx ni controller; Gson 1 vez; sin salida por consola.

## 4. NO VERIFICADO / pendiente

- Aspecto visual de la ventana (casilla 2.4): no se miró ninguna captura.
- No se probó tecleando en la ventana real con ratón/teclado; la UI se probó cargando el FXML y el controller reales e invocando sus manejadores.
- No se probó en Windows/IntelliJ del usuario (solo contenedor Linux con Java 21).
- Las pruebas T1/T2 no se incluyen en la entrega (viven en el contenedor).
- Quedó un temporal en el PC que no pude borrar: `02_Hackaton_PooFx/tmp_ej08.tgz`.
