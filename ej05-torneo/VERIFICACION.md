# Verificación · ej05-torneo (Torneo Copa Austral)

Generado por el agente programado. Todo lo marcado como verificado salió de ejecutar el código en el contenedor (Java 21, Maven 3.9.11, xvfb); lo que no se pudo comprobar está en la sección 4.

## 1. Supuestos y decisiones

**Qué leí de Stevens ej05 (solo el README; no copié código, nombres ni datos).** El tema es un torneo de videojuegos con una clase abstracta de participantes, dos subtipos con puntaje distinto, una interfaz que implementa solo uno de ellos y un puntaje "destacado". Me quedé únicamente con el tema (torneo, inscripciones, dos tipos) y con la idea estructural de "una interfaz de negocio que implementa un solo subtipo", que es justo la forma de la Librería. El resto lo diseñé desde cero.

**Cómo lo abstraje (todo original):**

- Dominio: torneo deportivo de verano "Copa Austral"; la entidad es una *inscripción* (no un participante ni un jugador).
- `Inscripcion` (abstracta): `nombre`, `partidosJugados` (0–200), `rondas` (1–12); `calcularCuota()` y `getTipo()` abstractos. Se eligió un rango de partidos que admite 0 y nada de "edad", para no parecerse a Stevens.
- `InscripcionEquipo`: `capitan`, `canchaTechada`; cuota base $60.000, +25 % con cancha techada.
- `InscripcionIndividual`: `ranking` (1–500), `aniosFederado` (0–40) y el estado `becado`; cuota base $18.000, −20 % si lleva más de 3 años federado.
- `Becable` (interfaz, 2 métodos: `estaBecado()` / `marcarBecado()`), implementada solo por `InscripcionIndividual`, como `Devolvible` en la Librería.
- Cifras y reglas distintas a las de Librería, veterinaria y Stevens; los cuatro resultados son enteros limpios ($75.000, $60.000, $14.400, $18.000).
- Una sola ventana y un solo FXML; sin ventanas secundarias, relaciones entre entidades, ComboBox de relación ni filtros (eso de Stevens no cabe en la rúbrica).
- Archivos: `equipos.json` e `individuales.json` en la raíz del proyecto.
- Moldes usados solo por forma: `pre-hackathon/solucion` y `base`. No leí `Docs_priv` ni otras carpetas. Leí `RUBRICA.md`, `ENUNCIADO.md`, `repo_final/README.md`; no abrí `Repos_referencia` ni los archivos de `ej01-veterinaria` (solo listé sus nombres), por lo que su "nivel de detalle" se igualó con la plantilla de la Librería.
- Supuesto: los atributos propios de las subclases también se validan en sus setters (la Librería no lo hace), y el DAO los revisa al leer.
- Supuesto: los mensajes de error de archivo nombran el archivo afectado (“Error al leer archivo equipos.json.”) y el de eliminación sin selección dice “Debe seleccionar una inscripción de la tabla para eliminarla.”.
- Se siguió el mismo patrón de la Librería para el formulario: tras un registro exitoso el selector vuelve a "sin tipo" y ambos grupos de datos quedan deshabilitados.
- Como `base` en la Librería, la base no trae `.github/workflows` ni archivos COPIAR-A-…, por instrucción.

## 2. Rúbrica: casillas y evidencia (13 criterios × 4)

Evidencia: **[S]** = revisión estática de archivos (`solucion/…`), **[D]** = `PruebaDatos` (27 comprobaciones), **[U]** = `PruebaUi` (35 comprobaciones, carga el FXML real con su controlador bajo xvfb), **[R]** = `mvn javafx:run` (salida 124 = la ventana abre).

| N° | Criterio | Casillas | Evidencia |
|---|---|---|---|
| 1 | Configuración del proyecto | 4/4 | `pom.xml` tiene Gson 2.11.0 junto a las dos dependencias JavaFX sin alterar (diff contra la base: solo se agregó Gson) [S]; `grep -c` da 1 aparición y `mvn compile` pasa [S]; `Launcher` y `App` idénticos a la base (diff) y `javafx:run` abre [R]; `App` carga `/cl/duoc/torneo/view/main-view.fxml` [S][R]. |
| 2 | Vista en FXML | 4/4 | `main-view.fxml` declara la ventana [S]; selector `cmbTipo`, campos comunes `txtNombre/txtPartidos/txtRondas`, propios `txtCapitan/chkCanchaTechada` y `txtRanking/txtAniosFederado/chkBecado` [S][U]; tabla con 5 columnas y botones Registrar y Eliminar [U]; `lblMensaje` y grupos separados (GridPane, dos HBox, tabla, botones, label) [S]. La claridad visual no se vio en pantalla (ver sección 4). |
| 3 | Vista conectada al controlador | 4/4 | `fx:controller="cl.duoc.torneo.controller.MainController"` [S]; los 18 `fx:id` coinciden uno a uno con los 18 atributos `@FXML` (comparación automática con `diff`) [S]; tipos coinciden y `FXMLLoader` carga sin error [U]; botones con `onAction` y sin `fx:id`; handlers `onRegistrar`/`onEliminar` existen [S]. |
| 4 | Inicio y eventos | 4/4 | Selector cargado con Equipo e Individual en `initialize` [U]; habilitación por tipo cambia al cambiar el selector [U]; Registrar y Eliminar funcionan [U]; Eliminar borra solo la fila elegida y sin selección avisa y conserva 4 filas [U]. |
| 5 | Mensajes de resultado | 4/4 | Mensajes de registro, eliminación, falta de selección y error de lectura/escritura observados en `lblMensaje` [U]; cada uno nombra la inscripción o el archivo, y reemplaza al anterior (`setText`); no hay `System.out` en el código (grep) [S]. |
| 6 | Paquetes y capas | 4/4 | Paquetes `controller`, `model`, `repository`, vista en `resources/…/view` [S]; cada clase en su paquete [S]; `grep javafx` y `grep controller` en `model/` y `repository/` sin resultados [S]; ninguna clase nueva en el paquete raíz (solo `App` y `Launcher`, de la base) ni en `model` [S]. |
| 7 | Controlador solo coordina | 4/4 | Valida entradas y usa constantes del modelo para los rangos [S]; la cuota viene de `calcularCuota()` y solo se formatea con `Inscripcion.pesos` [S]; registra/elimina/lista vía `InscripcionRepository` [S]; sin imports `java.io`/`java.nio`/Gson en el controlador (grep) [S]. |
| 8 | Tabla y lista observable | 4/4 | Una única `ObservableList`, actualizada con `setAll`; en la prueba `tabla.getItems()` es la misma instancia tras registrar y eliminar [U]; sincroniza al iniciar y tras cada operación, sin duplicados al reabrir [U]; las 5 columnas muestran los valores correctos ($75.000 / $60.000 / $14.400 / $18.000) [U]; aparece y desaparece de inmediato [U]. |
| 9 | Validación del formulario | 4/4 | Tipo obligatorio, nombre y capitán sin vacío/espacios [U]; rangos 0–200, 1–12, 1–500, 0–40 y formato numérico (`abc`, 201, 0, 13, 501→0, 41) [U][D]; con tipo Equipo se ignoran basura en ranking/años [U]; con dato inválido no se registra nada (y no se crean archivos) y la ventana sigue operativa [U]. |
| 10 | Mensajes de error y formulario | 4/4 | Errores en `lblMensaje` [U]; nombran el campo («El campo Rondas…») [U]; indican el motivo («debe estar entre 1 y 12», «debe ser un número entero») [U]; valores conservados tras error y formulario vacío tras éxito [U]. |
| 11 | DAO y Repository | 4/4 | `InscripcionDao<T>` con `leer`/`guardar` [S]; `GsonInscripcionDao<T>` con Gson para `equipos.json` e `individuales.json` (dos instancias) [S][D]; `InscripcionRepository` guarda dos listas en memoria, sin JavaFX [S]; el Repository no toca archivos, solo llama al DAO [S]. |
| 12 | Carga y guardado | 4/4 | Al iniciar carga ambos archivos por DAO+Repository y los muestra [D][U]; sin archivos inicia vacío sin error [D][U]; cada registro/eliminación reescribe el JSON y al reabrir aparece todo [D][U]; error de escritura (JSON es una carpeta) → `PersistenciaException`, memoria intacta, no se informa éxito y la tabla no cambia, también al eliminar [D][U]. |
| 13 | Flujo completo | 4/4 | Inicia sin errores y carga lo guardado [R][U]; los 4 datos de prueba se registran con validación [U]; cuotas $75.000, $60.000, $14.400, $18.000 [U][D]; tras eliminar Tigres Norte y reabrir quedan 3 y Mateo Rojas sigue becado (comprobado en el modelo recargado, `PruebaDatos`) [D][U]. |

**Total: 52/52 casillas** (con la reserva de la sección 4 sobre el aspecto visual, que no cambia ninguna casilla verificable por código).

## 3. Resultado de las pruebas (ejecutadas)

1. `mvn -q compile` en `base` y en `solucion`: sin errores.
2. Base ejecutada con `javafx:run`: falla con `IllegalStateException: No se encontró la vista principal: /cl/duoc/torneo/view/main-view.fxml` (el mensaje esperado).
3. `timeout 25 xvfb-run -a mvn -q javafx:run` en `solucion`: salida **124** (la ventana abre y sigue corriendo; sin excepciones en el log).
4. `PruebaDatos` (clases compiladas + classpath de Maven): **27 ok, 0 fallas**. Cubre: inicio sin archivos, los 4 datos con sus cuotas, creación de los 2 JSON, `listar()` devuelve copia, reabrir muestra los 4, estado `becado` persistido, eliminar y reabrir, validaciones del modelo, JSON corrupto, datos inválidos dentro del JSON (partidos 500, nombre vacío, ranking 0, registro `null`), `cargar()` fallido no altera la memoria, y error de escritura (JSON convertido en carpeta) al registrar y al eliminar con la memoria intacta.
5. `PruebaUi` (FXML + controlador reales bajo xvfb): **35 ok, 0 fallas**. Cubre habilitación por tipo, validaciones con mensaje y valores conservados, formulario limpio tras éxito, tabla con la misma `ObservableList`, valores de la tabla, eliminar con y sin selección, reabrir, error de guardado y JSON corrupto al iniciar.
6. Comprobaciones automáticas: `fx:id` del FXML = atributos `@FXML` (18 = 18); botones sin `fx:id` y con `onAction`; `model/` y `repository/` sin `javafx` ni `controller`; Gson una sola vez en el pom; sin `System.out`/`printStackTrace`; sin restos de "libreria/publicación/libro/revista" en `solucion/` ni en `base/` (revisado con grep tras reescribir el README); `model/`, `App` y `Launcher` idénticos entre `base` y `solucion`.

Los programas de prueba (`PruebaDatos.java`, `PruebaUi.java`) no se incluyen en la entrega; los JSON que generaron se borraron del contenedor.

## 4. Lo que NO pude verificar

- **NO VERIFICADO:** la apariencia real de la ventana (no se tomó captura ni se revisó el diseño a ojo); "se distingue con claridad" (criterio 2, casilla 4) se apoya solo en la estructura del FXML.
- **NO VERIFICADO:** apertura del proyecto en IntelliJ IDEA y del FXML en Scene Builder (no hay ninguno de los dos en el contenedor); el FXML sí carga con `FXMLLoader`.
- **NO VERIFICADO:** ejecución en Windows (todo se probó en Linux con xvfb); el shell del PC tiene Java 11 y no Maven, por lo que no se compiló allí.
- **NO VERIFICADO:** que un alumno real pueda resolver el ejercicio en el tiempo previsto y que el enunciado no tenga ambigüedades; solo lo revisé yo.
- **NO VERIFICADO:** persistencia con cierre y reapertura reales del proceso de JavaFX (se simuló recargando el FXML y el repositorio en la misma JVM, y relanzando el repositorio desde los archivos en `PruebaDatos`).
- No se comparó formalmente con `ej01-veterinaria` (no abrí sus archivos); la forma se igualó con la Librería.
- Queda un archivo temporal en el PC que no puedo borrar: `Semestre02_2026/tmp_ej05.tgz` (en la raíz de la carpeta conectada), creado para traer las plantillas.
