# VERIFICACION · ej04-inventario

Generado de forma automática y desatendida (sin preguntas). Fecha: 2026-11-08.

## 1. Supuestos y decisiones

**Inspiración tomada de Stevens ej04 (solo lectura: README y lista de archivos)** y cómo se abstrajo:

| Idea de Stevens ej04 | Qué hice en este ejercicio (original) |
|---|---|
| Inventario de supermercado con productos de dos subtipos y un precio final con descuento/recargo | Tema de inventario conservado, pero en otro dominio: **bodega de una ferretería** con `Pintura` y `Herramienta`. Reglas, cifras, nombres y datos propios (nada copiado de clases, atributos, datos o redacción). |
| Una interfaz de negocio implementada solo por un subtipo | `Prestable` (`estaPrestada()` / `marcarPrestada()`), implementada solo por `Herramienta`. |
| Stock, valor total del inventario, alerta de stock bajo, archivos JSON por tipo | **Descartados** el valor total, la alerta en rojo y el modelo de "valor inventario" (no caben en la rúbrica de la Librería). Se mantiene `stock` como dato común y dos JSON por subtipo. |

Decisiones de diseño (misma forma que la Librería Rincón Lector, dominio propio):

- `Articulo` (abstracta): `nombre` (no vacío), `stock` (1–500), `anaquel` (1–40) validados en los setters con `IllegalArgumentException` con motivo; `calcularPrecioVenta()` y `getTipo()` abstractos.
- `Pintura`: `litrosEnvase` (1–20), `antioxido`. Precio = litros × $3.200, +15 % si es anti-óxido.
- `Herramienta` (implementa `Prestable`): `marca` (no vacía), `mesesEnBodega` (0–60); `prestada` se gestiona con la interfaz. Precio base $22.000, −25 % si lleva más de 12 meses en bodega.
- Los resultados con los datos de prueba son enteros exactos (cálculo con enteros: `base * porcentaje / 100`).
- Supuesto: los atributos propios de las subclases también se validan en sus setters (la Librería solo validaba los comunes), así el DAO revalida lo leído de JSON con esos mismos setters.
- Archivos JSON: `pinturas.json` y `herramientas.json` en la raíz del proyecto (ignorados en `.gitignore`).
- Mensaje ante fallo de guardado: "No se registró X (Tipo). Error al escribir archivo ….json." (nombra el artículo y la causa).
- No se crearon `.github/workflows` ni archivos COPIAR-A-… (el usuario no los necesita). Sin `module-info.java`.
- El `ENUNCIADO.md` no incluye imágenes y dice "entrega según las instrucciones de quien dicta el ejercicio".
- Los valores esperados del enunciado se obtuvieron **ejecutando la solución** (prueba T1 imprime `VALOR esmalte=14720 latex=32000 taladro=22000 sierra=16500`).
- Limitación conocida (heredada del diseño de la Librería): si un JSON está corrupto al iniciar, la tabla queda vacía y el mensaje lo informa; un registro posterior sobrescribiría ese archivo.

## 2. Casillas de la rúbrica (RUBRICA.md, adaptada a Articulo/Pintura/Herramienta)

Pruebas: **T1** = `Prueba` (repository/DAO/modelo, 27 comprobaciones), **T2** = `UiPrueba` (FXML + controller reales cargados con JavaFX bajo xvfb, 29 comprobaciones), **T3** = `javafx:run` (exit 124 = abre), **T4** = chequeo estático (script Python), **T5** = revisión de código leyendo los archivos.

| N° | Criterio | Casillas | Evidencia |
|---|---|---|---|
| 1 | Configuración | 4/4 | Gson 2.11.0 una sola vez junto a JavaFX, que quedó sin cambios (T4, `diff` base/solución solo agrega Gson); `mvn compile` OK en ambos; `Launcher`/`App` idénticos a la base (diff); `javafx:run` abre (T3); vista en `/cl/duoc/inventario/view/main-view.fxml` |
| 2 | Vista FXML | 3/4 + 1 NO VERIFICADO | Ventana en FXML (T5); selector, comunes, propios de ambos tipos, tabla de 5 columnas, Registrar/Eliminar y `lblMensaje` presentes (T2 los usa). "Cada grupo se distingue con claridad": hay títulos, `Separator` y `GridPane`/`HBox`, pero **NO VERIFICADO visualmente** (no se miró captura) |
| 3 | Vista–controlador | 4/4 | `fx:controller` presente; 18 `fx:id` = 18 `@FXML` uno a uno, tipos coinciden, botones con `onAction` sin `fx:id` (T4); T2 carga el FXML sin error |
| 4 | Inicio y eventos | 4/4 | T2: selector con Pintura/Herramienta, habilitación por tipo, Registrar y Eliminar funcionan, Eliminar quita solo la fila seleccionada y sin selección avisa y conserva |
| 5 | Mensajes de resultado | 4/4 | T2: mensajes de registro, eliminación y falta de selección en el Label, cada uno reemplaza al anterior y nombra artículo/causa; sin `System.out/err` (T4). Errores de lectura/guardado: T1 prueba el texto de `PersistenciaException` (incluye archivo) y T5 confirma que el controller lo muestra en `lblMensaje`; **no se provocó en la ventana real** |
| 6 | Paquetes y capas | 4/4 | `controller`, `model`, `repository`, FXML en `resources/.../view`; `model` y `repository` sin imports de javafx ni controller (T4); en el paquete raíz solo `App`/`Launcher` entregados |
| 7 | Controlador solo coordina | 4/4 | T4/T5: usa `calcularPrecioVenta()` del modelo, pide todo a `ArticuloRepository`, no importa java.io/nio/gson |
| 8 | Tabla y lista observable | 4/4 | T2: misma `ObservableList` (`getItems()` idéntico), `setAll` tras cada operación, aparece/desaparece al instante, 5 columnas con precios `$14.720 $32.000 $22.000 $16.500` leídos de la columna |
| 9 | Validación | 4/4 | T2: tipo, nombre y marca en blanco, no entero, rangos (stock 0, anaquel 41, litros 25, meses 61); solo se validan campos del tipo elegido (T5); sin registro ni excepción |
| 10 | Mensajes de error | 4/4 | T2: mensajes en el Label nombran campo y motivo ("El stock debe estar entre 1 y 500."); valores conservados tras error y formulario limpio tras éxito |
| 11 | DAO y Repository | 4/4 | `ArticuloDao`, `GsonArticuloDao` (pinturas.json/herramientas.json), `ArticuloRepository` con dos listas, sin JavaFX, archivos solo vía DAO (T4, T5) |
| 12 | Carga y guardado | 4/4 | T1: inicio sin archivos → vacío; ambos JSON se crean; reabrir muestra lo guardado; escritura en carpeta → `PersistenciaException` y memoria intacta; fallo al eliminar con DAO falso → deshace |
| 13 | Flujo completo | 4/4 | T1/T2: 4 datos con precios $14.720, $32.000, $22.000, $16.500; eliminar Látex y reabrir → ya no aparece; Sierra sigue prestada tras reabrir. La secuencia se probó por API/controller, no escribiendo a mano en la ventana |

**Total: 52/52 por código y pruebas; estrictamente verificadas 51/52** (casilla 2.4, claridad visual, NO VERIFICADA).

## 3. Resultado de las pruebas (contenedor: Java 21, Maven 3.9.11, xvfb)

- `mvn -q compile` base: OK. `mvn -q compile` solución: OK.
- Base: `javafx:run` falla con `IllegalStateException: No se encontró la vista principal: /cl/duoc/inventario/view/main-view.fxml` (esperado).
- Solución: `timeout 25 xvfb-run -a mvn -q javafx:run` → exit 124 (la ventana abre y sigue viva).
- T1 `Prueba`: 27/27 OK (inicio vacío, 4 precios, 2 JSON, `listar()` copia, reabrir 4, préstamo persistido, eliminar y reabrir, JSON corrupto, stock 0 / litros 99 / marca vacía dentro del JSON → `PersistenciaException`, JSON-carpeta → excepción y memoria intacta, fallo al eliminar con DAO falso, validaciones del modelo, límite 12/13 meses).
- T2 `UiPrueba`: 29/29 OK. Detectado y corregido antes de entregar: mensajes con concordancia incorrecta ("Los litros del envase debe…").
- T4: 18 `fx:id` = 18 `@FXML`; `model/` y `repository/` sin javafx ni controller; Gson 1 vez en solución, 0 en base; `base/` sin controller/repository/FXML; sin `module-info.java`.

## 4. NO VERIFICADO / pendiente

- Aspecto visual de la ventana (no se tomó ni revisó captura): disposición, tamaños, claridad de grupos.
- Ejecución real en IntelliJ/Windows con JDK 21 (el PC solo tiene Java 11 sin Maven; se probó en contenedor Linux).
- Escritura manual en los campos de la ventana real: la UI se probó cargando el FXML y accionando el controller por reflexión.
- Mensaje de error de lectura/guardado en la ventana real (solo a nivel de repository y lectura del código del controller).
- `mvn verify` no se corrió (sí `compile`); no hay workflow de GitHub en esta entrega.
- Verificación en el PC de la copia entregada: ver la sección 5.

## 5. Entrega

27 archivos (sin `target/`, sin JSON generados, sin `.idea`) entregados al PC en `02_Hackaton_PooFx/Ejercicios_base/ej04-inventario/`. Comprobado en el PC con `device_bash`: misma cantidad de archivos (27) y la lista de `cksum` de todos los archivos coincide con la del contenedor (mismo md5 de la lista) antes de la última actualización de este mismo documento. No quedó ningún temporal `tmp_ej04.tgz` (no fue necesario). No se escribió nada fuera de esta carpeta.
