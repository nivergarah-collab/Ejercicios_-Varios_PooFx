# VERIFICACION · ej01-veterinaria

Generado de forma automática y desatendida (sin preguntas). Fecha: 2026-11-08.

## 1. Supuestos y decisiones

**Inspiración tomada de Stevens ej01 (solo lectura: README y lista de archivos)** y cómo se abstrajo:

| Idea de Stevens ej01 | Qué hice en este ejercicio (original) |
|---|---|
| Tema veterinaria con Dueño y Mascota relacionados, ventana modal, ComboBox de relación, filtro por especie | **Descartado** (no cabe en la rúbrica). No hay relación entre entidades ni segunda ventana. |
| Tabla con cálculo derivado (edad calculada) | El valor derivado es el **costo por sesión** (`calcularCosto()`), definido por el modelo con regla propia. |
| Datos del animal (especie, raza) | Atributos comunes propios: `paciente`, `pesoKg`, `sesiones`. No se copió ninguna clase, nombre, dato de prueba ni redacción. |

Decisiones de diseño (jerarquía propia, misma forma que la Librería Rincón Lector):

- `Atencion` (abstracta): `paciente` (no vacío), `pesoKg` (1–150), `sesiones` (1–20) validados en los setters con `IllegalArgumentException` con motivo; `calcularCosto()` y `getTipo()` abstractos.
- `Consulta`: `veterinario`, `urgente`. Costo base $20.000, +50 % si es urgente.
- `Vacunacion` (implementa `Recordable`): `numeroDosis` (1–12), `mesesDesdeUltimaDosis` (0–120). Costo base $15.000, −20 % si la dosis anterior fue hace 6 meses o menos. El estado `recordatorioEnviado` lo gestiona la interfaz (como `listaParaDevolucion` en la Librería).
- `Recordable`: `estaRecordatorioEnviado()` y `marcarRecordatorioEnviado()`.
- Supuesto: los atributos propios de las subclases también se validan en sus setters (la Librería solo validaba los comunes); así el DAO puede revalidar lo leído de JSON usando los setters.
- Supuesto: el tipo "Vacunación" lleva tilde también en el selector y en el JSON no aparece (el tipo no se guarda; sale de `getTipo()`).
- Archivos JSON: `consultas.json` y `vacunaciones.json` en la raíz del proyecto (ignorados en `.gitignore`).
- El `ENUNCIADO.md` no incluye imágenes y menciona "entrega según las instrucciones de quien dicta el ejercicio".
- Los datos de prueba pedidos son "4 publicaciones": se usaron 4 atenciones, 2 por subtipo.

Datos de prueba y costos obtenidos **ejecutando la solución** (no a mano): Max (Consulta urgente) $30.000; Tom (Consulta) $20.000; Luna (Vacunación, 4 meses) $12.000; Rocky (Vacunación, 10 meses) $15.000.

## 2. Casillas de la rúbrica (RUBRICA.md, adaptada a Atención/Consulta/Vacunación)

Pruebas referenciadas: **T1** = `Prueba` (repository/DAO, 24 comprobaciones), **T2** = `UiPrueba` (FXML + controller reales cargados con JavaFX bajo xvfb, 20 comprobaciones), **T3** = `javafx:run` (exit 124 = abre), **T4** = chequeo estático (script Python), **T5** = revisión de código leyendo los archivos.

| N° | Criterio | Casillas | Evidencia |
|---|---|---|---|
| 1 | Configuración | 4/4 | Gson 2.11.0 una vez junto a JavaFX sin cambios (T4, `solucion/pom.xml`); `mvn compile` OK en base y solución; `Launcher`/`App` idénticos a la base salvo nombre de paquete (diff), `javafx:run` abre (T3); vista cargada desde `/cl/duoc/veterinaria/view/main-view.fxml` sin alterar `App` |
| 2 | Vista FXML | 3/4 + 1 sin verificar visualmente | `main-view.fxml` declarado en FXML (T5); selector, comunes y propios de ambos tipos, tabla de 5 columnas, botones Registrar/Eliminar y `lblMensaje` presentes (T2 los usa todos). La casilla "cada grupo se distingue con claridad": hay títulos, `Separator` y agrupación en `GridPane`/`HBox`, pero **NO VERIFICADO visualmente** (no se miró captura) |
| 3 | Vista–controlador | 4/4 | `fx:controller` en el FXML; 18 `fx:id` = 18 `@FXML` uno a uno, tipos coinciden, botones usan `onAction` sin `fx:id` (T4); T2 carga el FXML sin error |
| 4 | Inicio y eventos | 4/4 | T2: selector con Consulta/Vacunación, habilitación por tipo, Registrar y Eliminar funcionan, Eliminar quita solo la fila seleccionada y sin selección avisa y conserva |
| 5 | Mensajes de resultado | 4/4 | T2: mensajes de registro, eliminación y falta de selección en el Label, cada uno reemplaza al anterior e indica atención/causa; errores de lectura/escritura: el controller los muestra con `lblMensaje.setText(e.getMessage())` (T5) y T1 prueba que el mensaje de `PersistenciaException` incluye archivo; sin `System.out/err` (T4). No se probó con la ventana real un error de lectura (solo vía T1 y lectura del código) |
| 6 | Paquetes y capas | 4/4 | `controller`, `model`, `repository`, FXML en `resources/.../view`; `model` y `repository` sin imports de javafx ni controller (T4); nada en el paquete raíz salvo `App`/`Launcher` entregados |
| 7 | Controlador solo coordina | 4/4 | T5/T4: usa `calcularCosto()` del modelo, pide todo al `AtencionRepository`, no importa java.io/nio/gson |
| 8 | Tabla y lista observable | 4/4 | T2: misma `ObservableList` (`getItems()` idéntico antes y después), `setAll` tras cada operación, aparece/desaparece al instante, 5 columnas, costo $12.000 de Luna proviene de `calcularCosto()` |
| 9 | Validación | 4/4 | T2: tipo, paciente/veterinario en blanco, número no entero, rango; solo se validan campos del tipo elegido (T5); sin registro ni excepción no controlada |
| 10 | Mensajes de error | 4/4 | T2: mensajes en el Label ("El peso debe ser un número entero.", "El número de sesiones debe estar entre 1 y 20.") nombran campo y motivo; valores conservados tras error y formulario limpio tras registro exitoso |
| 11 | DAO y Repository | 4/4 | `AtencionDao`, `GsonAtencionDao` (consultas.json/vacunaciones.json), `AtencionRepository` con dos listas, sin JavaFX, archivos solo vía DAO (T4, T5) |
| 12 | Carga y guardado | 4/4 | T1: inicio sin archivos → lista vacía; ambos JSON se crean; reabrir muestra lo guardado; registrar/eliminar guardan sin acción extra; escritura en carpeta → `PersistenciaException`, memoria intacta; fallo al eliminar con DAO falso → deshace |
| 13 | Flujo completo | 4/4 | T1/T2: 4 datos de prueba con costos $30.000, $20.000, $12.000, $15.000; eliminar Tom y reabrir → ya no aparece; Luna conserva recordatorio enviado al reabrir. La secuencia se probó por API/controller, no escribiendo en la ventana a mano |

**Total: 52/52 por código y pruebas, con la salvedad de la casilla 2.4 (claridad visual) NO VERIFICADA visualmente → 51/52 estrictamente verificadas.**

## 3. Resultado de las pruebas (ejecutadas en el contenedor: Java 21, Maven 3.9.11, xvfb)

- `mvn -q compile` base: OK. `mvn -q compile` solución: OK.
- Base: `javafx:run` falla con `IllegalStateException: No se encontró la vista principal: /cl/duoc/veterinaria/view/main-view.fxml` (esperado).
- Solución: `timeout 25 xvfb-run -a mvn -q javafx:run` → exit 124 (la ventana abre y sigue viva).
- T1 (`Prueba`): todas OK — inicio sin archivos vacío; 4 costos esperados; 2 JSON creados; `listar()` devuelve copia; reabrir 4; recordatorio persistido; eliminar y reabrir; JSON corrupto → `PersistenciaException`; peso 0 en JSON → `PersistenciaException`; veterinario vacío en JSON → `PersistenciaException`; JSON-carpeta → `PersistenciaException` con memoria intacta; fallo al eliminar con DAO falso → memoria intacta.
- T2 (`UiPrueba`): 20/20 OK (ver arriba). Hubo un defecto de redacción en dos mensajes ("Las sesiones debe estar…") detectado en la prueba y corregido antes de entregar.
- T4: ver sección 2 (todos los chequeos estáticos verdaderos).

## 4. NO VERIFICADO / pendiente

- **Archivo protegido:** la herramienta de entrega al PC rechazó `.github/workflows/build.yml` ("is a protected file and cannot be written via remote tools") en `base/` y `solucion/`. No se eludió la protección. El contenido idéntico a la plantilla quedó como `COPIAR-A-.github-workflows-build.yml` en cada carpeta; hay que copiarlo a mano a `.github/workflows/build.yml`. Los proyectos compilan igual sin él.
- Aspecto visual de la ventana (no se tomó ni revisó captura): disposición, tamaños, claridad de grupos.
- Ejecución real en IntelliJ/Windows con JDK 21 (el PC solo tiene Java 11 sin Maven; se probó en contenedor Linux).
- El workflow `.github/workflows/build.yml` (copiado de la plantilla con `mvn verify`) no se ejecutó en GitHub; `mvn verify` no se corrió (sí `compile`). Además, ese archivo no pudo entregarse con su nombre real (ver primer punto).
- Escritura manual en los campos de la ventana real: la UI se probó cargando el FXML y accionando el controller por reflexión.
- Mensaje de error de lectura al iniciar la ventana real con JSON corrupto: probado solo a nivel de repository y por lectura del código del controller.
