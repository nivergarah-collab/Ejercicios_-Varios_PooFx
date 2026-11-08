# VERIFICACION · ej07-playlist

Generado de forma automática y desatendida (sin preguntas). Fecha: 2026-11-08.

## 1. Supuestos y decisiones

**Inspiración tomada de Stevens ej07 (solo lectura del README) y cómo se abstrajo:**

| Idea de Stevens ej07 | Qué hice en este ejercicio (original) |
|---|---|
| Tema playlist con dos subtipos de contenido (canción y podcast), cada uno con su regla de cálculo | Tema conservado (pistas de una playlist), pero el valor calculado es el **costo de licencia en pesos** (`calcularCostoLicencia()`), no un peso en MB. Reglas y cifras propias. |
| Duración formateada `mm:ss`, peso estimado por calidad, columna de datos específicos, Label con duración total | **Descartado** (no cabe en la rúbrica): la duración es un entero en minutos y no hay Label de total ni columna de datos específicos. |
| Interfaz de negocio que implementa solo uno de los subtipos | Misma forma, con otro nombre y otro propósito: `Sincronizable` (`estaSincronizado()` / `marcarSincronizado()`), implementada solo por `Podcast`. |
| Datos de prueba, atributos (álbum, plataforma, género), clases y paquetes | No se copió ninguno: atributos, nombres y datos son propios. Los nombres de los dos JSON (`canciones.json`, `podcasts.json`) coinciden con los de Stevens por ser los nombres naturales de cada subtipo; se mantienen. |

Decisiones de diseño (misma forma que la Librería Rincón Lector, dominio distinto):

- `Pista` (abstracta): `titulo`, `artista` (no vacíos), `duracionMin` (1–240), validados en los setters con `IllegalArgumentException` con motivo; `calcularCostoLicencia()` y `getTipo()` abstractos.
- `Cancion`: `album`, `estreno`. Licencia $700 por minuto, +30 % si es estreno.
- `Podcast` (implementa `Sincronizable`): `episodio` (1–500) y `sincronizado` (estado, sin setter). Licencia $400 por minuto, −25 % si el episodio es de los primeros 10.
- Supuesto: los atributos propios también se validan en los setters (álbum no vacío, episodio 1–500) para que el DAO pueda revalidar lo leído del JSON.
- Supuesto: el tipo no se guarda en el JSON; sale de `getTipo()`.
- `PistaRepository` mantiene una lista por subtipo y un DAO por lista; si guardar falla, deshace el cambio en memoria y relanza `PersistenciaException`.
- Paquete `cl.duoc.playlist`, artifactId `playlist`, clase principal `cl.duoc.playlist.Launcher`.
- `ENUNCIADO.md` sin imágenes; en "Condiciones" dice que la entrega se hace según las instrucciones de quien dicta el ejercicio.
- La base no incluye `.github/workflows` ni archivos COPIAR-A-… (pedido expreso).
- No se creó ningún `tmp_ej07.tgz` en el PC (las plantillas se leyeron con `cat`).

Datos de prueba y licencias obtenidos **ejecutando la solución** (no a mano): Rutas del Sur (Canción, 4 min, estreno) $3.640; Marea Baja (Canción, 3 min) $2.100; Código y Café (Podcast, 45 min, episodio 8) $13.500; Historias del Puerto (Podcast, 30 min, episodio 25) $12.000.

## 2. Casillas de la rúbrica (RUBRICA.md, adaptada a Pista/Canción/Podcast)

Pruebas referenciadas: **T1** = `Prueba` (repository/DAO, 29 comprobaciones), **T2** = `UiPrueba` (FXML + controller reales cargados con JavaFX bajo xvfb, 27 comprobaciones), **T3** = `javafx:run` (exit 124 = abre), **T4** = chequeo estático (script Python), **T5** = revisión de código leyendo los archivos.

| N° | Criterio | Casillas | Evidencia |
|---|---|---|---|
| 1 | Configuración | 4/4 | Gson 2.11.0 una sola vez junto a JavaFX sin cambios (T4, `solucion/pom.xml`); `mvn compile` OK en base y solución; `Launcher`/`App` idénticos a la base (diff), `javafx:run` abre (T3); vista cargada desde `/cl/duoc/playlist/view/main-view.fxml` sin alterar `App` |
| 2 | Vista FXML | 3/4 + 1 NO VERIFICADO | FXML declarado (T5); selector, comunes, propios de ambos tipos, tabla de 5 columnas, Registrar/Eliminar y `lblMensaje` presentes (T2 los usa). La casilla "cada grupo se distingue con claridad": hay títulos, `Separator` y agrupación en `GridPane`/`HBox`, pero **NO VERIFICADO visualmente** (no se miró captura) |
| 3 | Vista–controlador | 4/4 | `fx:controller` en el FXML; 17 `fx:id` = 17 `@FXML` uno a uno, tipos coinciden, botones con `onAction` y sin `fx:id` (T4); T2 carga el FXML sin error |
| 4 | Inicio y eventos | 4/4 | T2: selector con Canción/Podcast, habilitación por tipo, Registrar y Eliminar funcionan, Eliminar quita solo la fila seleccionada y sin selección avisa y conserva |
| 5 | Mensajes de resultado | 4/4 | T2: mensajes de registro, eliminación, falta de selección y error de lectura (JSON corrupto) en el Label, cada uno reemplaza al anterior e indica pista/causa; sin `System.out/err` (T4). El error de escritura se prueba en T1 (mensaje de la excepción con el archivo) y el controller lo muestra con `lblMensaje.setText(e.getMessage())` (T5); no se probó con la ventana real |
| 6 | Paquetes y capas | 4/4 | `controller`, `model`, `repository`, FXML en `resources/.../view`; `model` y `repository` sin imports de javafx ni controller (T4); nada en el paquete raíz salvo `App`/`Launcher` entregados |
| 7 | Controlador solo coordina | 4/4 | T4/T5: usa `calcularCostoLicencia()` del modelo, no hay constantes de cálculo en el controller, pide todo al `PistaRepository`, no importa java.io/nio/gson |
| 8 | Tabla y lista observable | 4/4 | T2: misma `ObservableList` (`getItems()` idéntico), `setAll` tras cada operación, aparece/desaparece al instante, 5 columnas, licencias $3.640/$2.100/$13.500/$12.000 provienen del modelo, sin duplicados |
| 9 | Validación | 4/4 | T2: tipo, título vacío y en blanco, duración `abc`/241, episodio 0; solo se validan campos del tipo elegido (un Podcast se registra con el álbum vacío); sin registro ni excepción no controlada |
| 10 | Mensajes de error | 4/4 | T2: mensajes en el Label que nombran campo y motivo; valores conservados tras error y formulario limpio tras registro exitoso |
| 11 | DAO y Repository | 4/4 | `PistaDao`, `GsonPistaDao` (canciones.json/podcasts.json), `PistaRepository` con dos listas, sin JavaFX, archivos solo vía DAO (T4, T5) |
| 12 | Carga y guardado | 4/4 | T1: inicio sin archivos → lista vacía; ambos JSON se crean; reabrir muestra lo guardado; escritura en carpeta → `PersistenciaException` con memoria intacta; fallo al eliminar (DAO falso) → deshace |
| 13 | Flujo completo | 4/4 | T1/T2: 4 datos de prueba con licencias esperadas; eliminar Marea Baja y reabrir → ya no aparece; Código y Café sigue sincronizado al reabrir. La secuencia se probó por API/controller, no escribiendo en la ventana a mano |

**Total: 52/52 por código y pruebas, con la salvedad de la casilla 2.4 (claridad visual) NO VERIFICADA visualmente → 51/52 estrictamente verificadas.**

## 3. Resultado de las pruebas (contenedor: Java 21, Maven 3.9.11, xvfb)

- `mvn -q compile` base: OK. `mvn -q compile` solución: OK.
- Base: `javafx:run` falla con `IllegalStateException: No se encontró la vista principal: /cl/duoc/playlist/view/main-view.fxml` (esperado).
- Solución: `timeout 25 xvfb-run -a mvn -q javafx:run` → exit 124 (la ventana abre y sigue viva).
- T1 (`Prueba`): 29 OK, 0 fallas — inicio sin archivos vacío; 4 licencias esperadas; 2 JSON creados; `listar()` devuelve copia; reabrir 4; sincronizado persistido; eliminar y reabrir; JSON corrupto, duración 0, álbum vacío y registro nulo en JSON → `PersistenciaException`; JSON-carpeta → `PersistenciaException` con memoria intacta; fallo al eliminar con DAO falso → memoria intacta.
- T2 (`UiPrueba`): 27 OK, 0 fallas.
- T4: 17 `fx:id` = 17 `@FXML`; botones sin `fx:id`; `model/` y `repository/` sin imports de javafx ni controller; controller sin imports de archivos/Gson; sin `System.out/err`; Gson 1 vez en el pom de la solución.

## 4. NO VERIFICADO / pendiente

- Claridad visual de los grupos de la ventana (casilla 2.4): no se miró ninguna captura.
- Registro manual con teclado y ratón sobre la ventana real: se probó cargando el FXML y el controller reales y disparando los botones por código.
- Error de escritura mostrado en la ventana: probado en el repositorio y por lectura de código, no en la ventana.
- No se probó en IntelliJ ni con Scene Builder, ni en Windows.
- Las pruebas (`Prueba`, `UiPrueba`) viven en el contenedor y no se entregan.
