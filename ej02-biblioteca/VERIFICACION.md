# VERIFICACION · ej02-biblioteca

Generado de forma automática y desatendida (sin preguntas). Fecha: 2026-11-08.

## 1. Supuestos y decisiones

**Inspiración tomada de Stevens ej02 (solo lectura: README y lista de archivos)** y cómo se abstrajo:

| Idea de Stevens ej02 | Qué hice en este ejercicio (original) |
|---|---|
| Tema biblioteca con libro prestado / fecha de devolución | El tema se mantiene como **préstamos de una biblioteca**; la idea de "prestado" pasa a ser el estado `renovado` de un préstamo de libro, gestionado por la interfaz `Renovable`. |
| CRUD con tabla, CheckBox editable de "Prestado" | Tabla de solo lectura con 5 columnas y botones Registrar/Eliminar (la forma de la Librería). No hay CheckBox editable en la tabla. |
| Ventana secundaria de detalle con doble clic, búsqueda con `FilteredList`, validación de ISBN duplicado | **Descartado** (no cabe en la rúbrica). Una sola ventana, un solo FXML, sin filtros ni ventanas secundarias. |

No se copió ninguna clase, nombre, dato de prueba, regla ni redacción de Stevens.

Decisiones de diseño (jerarquía propia, misma forma que la Librería Rincón Lector):

- `Prestamo` (abstracta): `socio` (no vacío), `diasPrestamo` (1–30), `materiales` (1–5) validados en los setters con `IllegalArgumentException` con motivo; `calcularTarifa()` y `getTipo()` abstractos.
- `PrestamoLibro` (implementa `Renovable`): `titulo`, `novedad`. Tarifa base $1.800, +25 % si es novedad. El estado `renovado` lo gestiona la interfaz (como `listaParaDevolucion` en la Librería).
- `PrestamoAudiovisual`: `formato`, `duracionMinutos` (1–600). Tarifa base $2.400, +20 % si dura más de 120 minutos.
- `Renovable`: `estaRenovado()` y `renovar()`.
- Supuesto: los atributos propios también se validan en sus setters (la Librería solo validaba los comunes), así el DAO revalida lo leído de JSON usando los setters.
- Supuesto: el valor calculado es la tarifa **por material**, independiente de días y cantidad (igual que el precio unitario de la Librería).
- Columnas de la tabla: Tipo, Socio, Días, Materiales, Tarifa (equivalentes a Tipo, Título, Páginas, Cantidad, Precio de la rúbrica). Los criterios de la rúbrica se leyeron con esa equivalencia.
- Archivos JSON: `prestamos-libros.json` y `prestamos-audiovisuales.json` en la raíz del proyecto (ignorados en `.gitignore`). No se crean `.github/workflows` ni archivos COPIAR-A-….
- `solucion/` no lleva README (igual que la solución de la veterinaria); el README técnico está en `base/`.
- Rango de validación en el controlador: días 1–30, materiales 1–5, duración 1–600 (equivalentes a páginas/cantidad/edición/antigüedad).

Datos de prueba y tarifas obtenidos **ejecutando la solución** (no a mano): Camila Rojas (Libro, novedad) $2.250; Felipe Mora (Libro, renovado) $1.800; Ignacia Vera (Audiovisual, 150 min) $2.880; Tomás Lagos (Audiovisual, 90 min) $2.400.

## 2. Casillas de la rúbrica (RUBRICA.md, adaptada a Préstamo / PrestamoLibro / PrestamoAudiovisual)

Pruebas: **T1** = `Prueba` (repository/DAO/modelo, 29 comprobaciones), **T2** = `UiPrueba` (FXML + controller reales con JavaFX bajo xvfb, 34 comprobaciones), **T3** = `javafx:run` (exit 124 = abre), **T4** = chequeo estático (script Python), **T5** = revisión de código, **T6** = `Flujo` (secuencia exacta del enunciado).

| N° | Criterio | Casillas | Evidencia |
|---|---|---|---|
| 1 | Configuración | 4/4 | Gson 2.11.0 una sola vez junto a JavaFX sin alterarlo (T4, `solucion/pom.xml`); `mvn -q compile` OK en base y solución; `Launcher` y `App` iguales a la Librería salvo paquete y título (diff); T3 abre la ventana; vista cargada desde `/cl/duoc/biblioteca/view/main-view.fxml` |
| 2 | Vista FXML | 3/4 + 1 NO VERIFICADO | FXML declarado (T5); selector, comunes, propios de ambos tipos, tabla de 5 columnas, Registrar/Eliminar y `lblMensaje` presentes y usados por T2. "Cada grupo se distingue con claridad": hay título, `Separator`, `GridPane`/`HBox`, pero **NO VERIFICADO visualmente** (no se vio captura) |
| 3 | Vista–controlador | 4/4 | `fx:controller` en el FXML; 18 `fx:id` = 18 `@FXML`, tipos coinciden, botones con `onAction` y sin `fx:id` (T4); T2 carga el FXML sin error |
| 4 | Inicio y eventos | 4/4 | T2: selector con Libro/Audiovisual, habilitación por tipo, Registrar y Eliminar funcionan, Eliminar quita solo la fila seleccionada y sin selección avisa y conserva |
| 5 | Mensajes de resultado | 4/4 | T2: mensajes de registro, eliminación, falta de selección, error de lectura (JSON corrupto al iniciar) y error de escritura aparecen en `lblMensaje`, cada uno reemplaza al anterior e indica préstamo/causa; sin `System.out/err` (T4) |
| 6 | Paquetes y capas | 4/4 | `controller`, `model`, `repository`, FXML en `resources/.../view`; `model` y `repository` sin javafx ni controller (T4); nada nuevo en el paquete raíz salvo `App`/`Launcher` |
| 7 | Controlador solo coordina | 4/4 | T4/T5: usa `calcularTarifa()` del modelo, pide todo al `PrestamoRepository`, no importa java.io/nio/gson |
| 8 | Tabla y lista observable | 4/4 | T2: misma `ObservableList` antes y después, `setAll` tras cada operación, aparece/desaparece al instante, 5 columnas, tarifas del modelo |
| 9 | Validación | 4/4 | T2: tipo, socio/título/formato en blanco, número no entero, rangos; solo se validan los campos del tipo elegido; sin registro ni excepción no controlada |
| 10 | Mensajes de error | 4/4 | T2: mensajes en el Label nombran campo y motivo ("El plazo de préstamo debe estar entre 1 y 30 días."); valores conservados tras error y formulario limpio tras éxito |
| 11 | DAO y Repository | 4/4 | `PrestamoDao`, `GsonPrestamoDao` (dos archivos), `PrestamoRepository` con dos listas, sin JavaFX, archivos solo vía DAO (T4, T5) |
| 12 | Carga y guardado | 4/4 | T1: inicio sin archivos → vacío; ambos JSON se crean; reabrir muestra lo guardado; registrar/eliminar guardan sin acción extra; escritura en carpeta → `PersistenciaException`, memoria intacta (también en T2 con la ventana); fallo al eliminar con DAO falso → deshace |
| 13 | Flujo completo | 4/4 | T6/T2: 4 datos de prueba con tarifas $2.250, $1.800, $2.880, $2.400; reabrir muestra 4 y Felipe sigue renovado (leído del JSON); eliminar Tomás y reabrir → ya no aparece. La secuencia se probó por API/controller, no escribiendo en la ventana a mano |

**Total: 52/52 por código y pruebas, con la salvedad de la casilla 2.4 (claridad visual) NO VERIFICADA visualmente → 51/52 estrictamente verificadas.**

## 3. Resultado de las pruebas (contenedor: Java 21, Maven 3.9.11, xvfb)

- `mvn -q compile` base: OK. `mvn -q compile` solución: OK.
- Base: `javafx:run` falla con `IllegalStateException: No se encontró la vista principal: /cl/duoc/biblioteca/view/main-view.fxml` (esperado).
- Solución: `timeout 25 xvfb-run -a mvn -q javafx:run` → exit 124 (la ventana abre y sigue viva).
- T1: 29/29 OK (sin archivos → vacío; 4 tarifas; 2 JSON; `listar()` copia; reabrir 4; renovado persiste; eliminar y reabrir; JSON corrupto, días 99, socio vacío y duración 0 dentro del JSON → `PersistenciaException`; JSON-carpeta → `PersistenciaException` con memoria intacta; fallo al eliminar → memoria intacta; rechazos del modelo).
- T2: 34/34 OK. Una primera ejecución dio 1 falla por un error **del propio test** (el JSON de audiovisuales de pasos anteriores seguía cargado); se corrigió el test. También se mejoró la redacción del mensaje de materiales ("…entre 1 y 5." sin repetir la unidad).
- T6: secuencia del enunciado OK.
- T4: 18 fx:id = 18 @FXML; sin fx:id en botones; model/repository sin javafx; Gson 1 vez; controller sin java.io/nio/gson/System.out; sin referencias residuales a libreria/veterinaria/stevens.

## 4. NO VERIFICADO / pendiente

- Aspecto visual de la ventana (no se tomó ni revisó captura): disposición y claridad de grupos.
- Ejecución real en IntelliJ/Windows con JDK 21 (el PC solo tiene Java 11 sin Maven; se probó en contenedor Linux).
- Escritura manual en los campos de la ventana real: la UI se probó cargando el FXML y accionando el controller por reflexión.
- `mvn verify` no se corrió (solo `compile`); no hay workflow de GitHub en esta entrega (a propósito).
- **Archivo temporal:** quedó `Semestre02_2026/tmp_ej02.tgz` en el PC (paquete de plantillas usado para traerlas al contenedor). No se puede borrar desde aquí; puede eliminarse a mano.
