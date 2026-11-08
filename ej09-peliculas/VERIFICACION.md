# Verificación · ej09-peliculas (Cartelera Cine Horizonte)

Generado de forma automática (sesión desatendida). Fecha: 2026-11-08.

## 1. Supuestos y decisiones

- **Inspiración (Stevens ej09, solo lectura):** su README y su clase `Pelicula` muestran un catálogo de una sola entidad con título, director, género, año, duración y un indicador booleano ("vista"), más el flujo Controlador → Repository → DAO → JSON. De ahí tomé solo el tema (cine) y la idea de un indicador booleano persistido. No copié clases, nombres, atributos, datos de prueba ni la redacción de su README. Las reglas de precio, la jerarquía y los datos son propios.
- **Abstracción a la forma de la Librería Rincón Lector:** entidad abstracta `Funcion` (película, duración 60–240 min, entradas vendidas 1–300; validados en setters con `IllegalArgumentException`), `calcularPrecioEntrada()` abstracto en pesos y `getTipo()` abstracto. Dos subclases: `FuncionRegular` (sala, es3D; $6.500, +20 % si es 3D) y `FuncionPopular` (distribuidora, días en cartelera 0–365; $4.800, −25 % si lleva 30 días o más en cartelera; implementa la interfaz `Reembolsable` con `estaReembolsada()` / `marcarReembolsada()`). Los precios con los datos de prueba son enteros limpios (7.800 / 6.500 / 3.600 / 4.800).
- Usé `Ejercicios_base/ej01-veterinaria` como plantilla de forma, calidad y estructura (copiada y adaptada con otro dominio, otros nombres y otras reglas) y `RUBRICA.md` como fuente de verdad. Una sola ventana, un FXML, sin relaciones entre entidades.
- Archivos JSON: `funciones_regulares.json` y `funciones_populares.json`. Paquete `cl.duoc.peliculas`, artifactId `peliculas`.
- No se crearon `.github/workflows` ni archivos COPIAR-A-… (pedido del usuario).
- Temporal: quedó `Semestre02_2026/tmp_ej09.tgz` en el PC (carpeta raíz del semestre, fuera de `ej09-peliculas`); no pude borrarlo, se puede eliminar a mano.

## 2. Criterios de la rúbrica (13 criterios, 52 casillas)

| N° | Criterio | Casillas | Evidencia |
|---|---|---|---|
| 1 | Configuración | 4/4 | `solucion/pom.xml` tiene Gson 2.11.0 una sola vez (conteo automático = 1), JavaFX intacto; `mvn compile` OK; ejecución con `Launcher` sin cambios (`mvn javafx:run` → código 124 = ventana abierta); la vista se carga desde `cl/duoc/peliculas/view/main-view.fxml` (`App` sin alterar). |
| 2 | Vista FXML | 4/4 | `main-view.fxml`: selector, campos comunes, campos de regular y popular, tabla de 5 columnas, botones Registrar/Eliminar, `lblMensaje`; secciones con rótulos y separadores. Prueba de interfaz cargó el FXML. |
| 3 | Vista–controlador | 4/4 | `fx:controller` presente; comprobación automática: los 18 `fx:id` coinciden con los atributos `@FXML` (diferencia vacía); `onAction` en los 2 botones sin `fx:id`. |
| 4 | Inicio y eventos | 4/4 | Prueba de interfaz (UiPrueba): combo con 2 tipos, grupos deshabilitados al inicio, tipo regular habilita solo su grupo, Eliminar sin selección avisa y conserva datos. El cambio a "Función popular" está en el código (`actualizarCamposPorTipo`), no se probó con clic. |
| 5 | Mensajes | 4/4 | Mensajes de registro, eliminación, falta de selección y error de lectura/escritura van a `lblMensaje` (sin `System.out`; grep vacío). Prueba de UI vio registro y eliminación; el mensaje de error de archivo se probó en el Repository (excepción con texto) y se muestra en el controlador por código, no por clic de GUI. |
| 6 | Paquetes y capas | 4/4 | `controller/`, `model/`, `repository/`, vista en recursos; `model` y `repository` sin imports de `javafx` ni del controller (grep vacío); nada nuevo en el paquete raíz. |
| 7 | Controlador solo coordina | 4/4 | Usa `calcularPrecioEntrada()` y `Funcion.pesos`; no recalcula; sin acceso a archivos (revisión de código). |
| 8 | Tabla y lista observable | 4/4 | Una sola `ObservableList` con `setAll`; UiPrueba confirmó misma instancia tras registrar y que registrar/eliminar actualiza la tabla de inmediato. |
| 9 | Validación | 4/4 | Rangos 60–240, 1–300, 0–365 y textos no vacíos en `onRegistrar`; solo se validan los campos del tipo elegido; UiPrueba: duración "abc" y entradas "0" rechazadas sin excepción. |
| 10 | Mensajes de error y formulario | 4/4 | UiPrueba: el mensaje nombra el campo y el motivo, los valores se conservan tras el error y el formulario queda limpio tras el registro. |
| 11 | DAO y Repository | 4/4 | `FuncionDao`, `GsonFuncionDao` (dos archivos), `FuncionRepository` (una lista por subtipo, sin JavaFX, delega en DAO). |
| 12 | Carga y guardado | 4/4 | Prueba.java: inicio sin archivos con lista vacía; ambos JSON se crean; reabrir muestra los 4; eliminar y reabrir; error de escritura → `PersistenciaException` y memoria intacta (registro y eliminación). |
| 13 | Flujo completo | 4/4 | Precios 7.800 / 6.500 / 3.600 / 4.800 salen de la ejecución; el indicador "reembolsada" de Verano Eterno persiste; Ciudad de Cristal desaparece tras eliminar y reabrir. Ver nota en la sección 4. |

**Total: 52/52 casillas** por revisión de código y pruebas ejecutadas (con las salvedades de la sección 4).

## 3. Resultado de las pruebas (contenedor: Java 21, Maven 3.9.11, xvfb)

- `mvn -q compile` en `base` y `solucion`: OK.
- Base ejecutada sin vista: `IllegalStateException: No se encontró la vista principal: /cl/duoc/peliculas/view/main-view.fxml` (esperado).
- `timeout 25 xvfb-run -a mvn -q javafx:run` en `solucion`: código 124 (la ventana abre). No se generaron JSON al abrir sin datos.
- `Prueba.java` (21 comprobaciones, 0 fallos): inicio vacío; 4 datos de prueba con valores esperados; 2 JSON creados; `listar()` devuelve copia; reabrir muestra 4; eliminar y reabrir; JSON corrupto, dato inválido (duración 5) y sala vacía en el JSON → `PersistenciaException`; JSON como carpeta → `PersistenciaException` y memoria intacta; el modelo rechaza película vacía.
- `UiPrueba.java` (carga del FXML real con controlador en xvfb, 0 fallos): ver criterios 4, 5, 8, 9 y 10.
- Comprobaciones estáticas: `fx:id` = `@FXML`; sin `javafx` en `model/` ni `repository/`; Gson una vez en el pom; sin `System.out`.

## 4. NO VERIFICADO / pendiente

- Los 4 registros de prueba se hicieron a través del Repository; por la interfaz (controlador) solo registré y eliminé una función de ejemplo. No hice un recorrido manual con clics y reinicio real de la ventana.
- Cambio del selector a "Función popular", validación de los campos de la popular y el mensaje de error de archivo en la ventana: comprobados por lectura de código, no por prueba de interfaz.
- No se probó en Windows ni con IntelliJ; no se revisó visualmente la ventana (sin capturas).
- Los valores esperados del enunciado vienen de la ejecución de `Prueba.java`.
