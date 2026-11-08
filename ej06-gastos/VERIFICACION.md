# VERIFICACION · ej06-gastos

Generado de forma automática y desatendida (sin preguntas). Fecha: 2026-11-08.

## 1. Supuestos y decisiones

**Inspiración tomada de Stevens ej06 (solo lectura: su README)** y cómo se abstrajo:

| Idea de Stevens ej06 | Qué hice en este ejercicio (original) |
|---|---|
| Tema "gastos personales" con gasto fijo y variable, y un monto final con descuento/recargo | Tema propio: **gastos compartidos de un departamento**; el valor derivado es el **aporte por persona** (`calcularAporte()`), con reglas, cifras y subtipos distintos (salida / servicio). |
| Interfaz de negocio implementada por un solo subtipo (`Deducible`) | `Reembolsable`, implementada solo por `GastoServicio`, con otros métodos y otra semántica. |
| Datos de prueba, columnas, total general, fechas dd-MM-yyyy | **No usados.** Datos, columnas y reglas son propios; no hay fecha ni total general (no caben en la forma de la rúbrica). No se copió ninguna clase, nombre, dato ni redacción. |

Decisiones de diseño (misma forma que la Librería Rincón Lector y la veterinaria ya aprobada):

- `Gasto` (abstracta): `descripcion` (no vacía), `montoTotal` ($1.000–$5.000.000), `participantes` (1–20), validados en los setters con `IllegalArgumentException` con motivo; `calcularAporte()` y `getTipo()` abstractos.
- `GastoSalida`: `lugar`, `conPropina`. Aporte = monto/participantes (división entera) +10 % si hay propina.
- `GastoServicio` (implementa `Reembolsable`): `proveedor`, `diasDeAnticipo` (0–60); `reembolsado` lo gestiona la interfaz. Aporte = monto/participantes −8 % si se pagó con 10 días o más de anticipo.
- `Reembolsable`: `estaReembolsado()` y `marcarReembolsado()`.
- Supuesto: los atributos propios (lugar, proveedor, días) también se validan en sus setters, para que el DAO revalide lo leído del JSON usando los setters (Gson no los llama).
- Supuesto: la rúbrica menciona "libro/revista", "páginas", etc.; se adaptó por correspondencia (tipo, título→descripción, páginas/cantidad→monto/participantes, edición/antigüedad→días de anticipo). Rango del monto: $1.000–$5.000.000; el pom y las estructuras siguen el estilo de `pre-hackathon/base`.
- Supuesto: la columna "Monto total" se muestra con formato de pesos; el tipo no se guarda en JSON (sale de `getTipo()`).
- Archivos JSON: `salidas.json` y `servicios.json` en la raíz del proyecto (ignorados en `.gitignore`).
- Se usó `Ejercicios_base/ej01-veterinaria` como plantilla de forma (DAO genérico `GastoDao<T>`, Repository con dos listas y deshacer). No se crearon `.github/workflows` ni archivos COPIAR-A-….
- El `ENUNCIADO.md` no incluye imágenes y dice que la entrega es según quien dicta el ejercicio. Los datos de prueba piden "4 gastos": 2 por subtipo.

Datos de prueba y aportes obtenidos **ejecutando la solución** (no a mano): Cena de cumpleaños $33.000; Entradas al cine $12.000; Internet del departamento $13.800; Cuenta de la luz $30.000.

## 2. Casillas de la rúbrica (RUBRICA.md, adaptada a Gasto/GastoSalida/GastoServicio)

Pruebas referenciadas: **T1** = `Prueba` (repository/DAO, 21 comprobaciones), **T2** = `UiPrueba` (FXML + controller reales cargados con JavaFX bajo xvfb, 21 comprobaciones), **T3** = `javafx:run` (124 = abre), **T4** = chequeo estático (script Python), **T5** = revisión de código, **T6** = captura de la ventana (snapshot del FXML) vista a ojo.

| N° | Criterio | Casillas | Evidencia |
|---|---|---|---|
| 1 | Configuración | 4/4 | Gson 2.11.0 una vez junto a JavaFX sin cambios (T4, `solucion/pom.xml`); `mvn compile` OK en base y solución; `Launcher`/`App` idénticos a la base (diff); `javafx:run` abre (T3); vista cargada desde `/cl/duoc/gastos/view/main-view.fxml` sin alterar `App` |
| 2 | Vista FXML | 4/4 | `main-view.fxml` (T5); selector, comunes y propios de ambos tipos, tabla de 5 columnas, botones y `lblMensaje` (T2 los usa todos); T6: grupos separados por títulos, `Separator` y `GridPane`/`HBox`, se distinguen con claridad |
| 3 | Vista–controlador | 4/4 | `fx:controller` en el FXML; 18 `fx:id` = 18 `@FXML` uno a uno, tipos coinciden, botones con `onAction` sin `fx:id` (T4); T2 carga el FXML sin error |
| 4 | Inicio y eventos | 4/4 | T2: selector con Salida/Servicio, habilitación por tipo, Registrar y Eliminar funcionan, Eliminar quita solo la fila seleccionada y sin selección avisa y conserva |
| 5 | Mensajes de resultado | 4/4 | T2: mensajes de registro, eliminación y falta de selección en el Label, cada uno reemplaza al anterior e indica gasto/causa; error de lectura al iniciar con `servicios.json` corrupto se muestra en el Label ("Error al leer archivo servicios.json."); error de escritura: T1 (mensaje incluye archivo) y el controller lo muestra con `lblMensaje.setText(e.getMessage())` (T5); sin `System.out/err` (T4) |
| 6 | Paquetes y capas | 4/4 | `controller`, `model`, `repository`, FXML en `resources/.../view`; `model` y `repository` sin imports de javafx ni controller (T4); en el paquete raíz solo `App`/`Launcher` entregados |
| 7 | Controlador solo coordina | 4/4 | T4/T5: usa `calcularAporte()` del modelo, pide todo al `GastoRepository`, no importa java.io/nio/gson |
| 8 | Tabla y lista observable | 4/4 | T2: misma `ObservableList` antes y después, `setAll` tras cada operación (T5), aparece/desaparece al instante, 5 columnas, aportes $33.000/$12.000/$13.800/$30.000 provienen de `calcularAporte()` |
| 9 | Validación | 4/4 | T2: tipo, descripción en blanco, monto no numérico, participantes y anticipo fuera de rango; solo se validan campos del tipo elegido (T5); sin registro ni excepción no controlada |
| 10 | Mensajes de error y formulario | 4/4 | T2: mensajes en el Label que nombran campo y motivo ("El monto total debe ser un número entero.", "El anticipo de pago (días) debe estar entre 0 y 60."); valores conservados tras error y formulario limpio tras registro exitoso |
| 11 | DAO y Repository | 4/4 | `GastoDao<T>`, `GsonGastoDao<T>` (salidas.json / servicios.json), `GastoRepository` con dos listas, sin JavaFX, archivos solo vía DAO (T4, T5) |
| 12 | Carga y guardado | 4/4 | T1: inicio sin archivos → lista vacía; ambos JSON se crean; reabrir muestra lo guardado; registrar/eliminar guardan sin acción extra; escritura en carpeta → `PersistenciaException`, memoria intacta; fallo al eliminar con DAO falso → deshace |
| 13 | Flujo completo | 4/4 | T1/T2: 4 datos de prueba con aportes correctos; eliminar Cine y reabrir → ya no aparece; Internet conserva `reembolsado` al reabrir. La secuencia se probó con el controller y el FXML reales invocando los manejadores, no con clics de ratón ni escribiendo en la ventana a mano |

**Total: 52/52 por código y pruebas** (la rúbrica real no puede evaluarse con certeza; es autoevaluación).

## 3. Resultado de las pruebas (contenedor: Java 21, Maven 3.9.11, xvfb)

- `mvn -q compile` base: OK. `mvn -q compile` solución: OK.
- Base: `javafx:run` falla con `IllegalStateException: No se encontró la vista principal: /cl/duoc/gastos/view/main-view.fxml` (esperado).
- Solución: `timeout 25 xvfb-run -a mvn -q javafx:run` → exit 124 (la ventana abre y sigue viva).
- T1 (`Prueba`): 21/21 OK — inicio sin archivos vacío; 4 aportes esperados; 2 JSON creados; `listar()` copia; reabrir 4; reembolso persistido; eliminar y reabrir; JSON corrupto, monto 10 y proveedor vacío dentro del JSON → `PersistenciaException`; JSON-carpeta → `PersistenciaException` con memoria intacta; fallo al eliminar con DAO falso → memoria intacta.
- T2 (`UiPrueba`): 21/21 OK. En la primera corrida una comprobación falló por error de mi prueba (el selector tenía tipo); además detecté la redacción "Los días de anticipo debe estar…" y la corregí en el controller antes de entregar.
- T4: todos los chequeos estáticos verdaderos (fx:id = @FXML, sin javafx en model/repository, Gson una vez, sin restos de otros dominios).
- Las pruebas T1/T2 son programas aparte (en el contenedor); no se incluyen en la entrega.

## 4. NO VERIFICADO / pendiente

- No se probó con clics reales de ratón/teclado en una ventana: los manejadores se invocaron por reflexión sobre el controller real.
- Entrega al PC: ver sección 5.
- Quedó un temporal en el PC que no puedo borrar: `02_Hackaton_PooFx/tmp_ej06.tgz` (empaquetado de plantillas). Se puede eliminar a mano.
- No se ejecutó la solución en Windows ni con IntelliJ; solo Linux/xvfb.

## 5. Entrega al PC

Carpeta: `02_Hackaton_PooFx/Ejercicios_base/ej06-gastos/` (no existía; no se sobrescribió nada). Se escribieron 27 archivos; en el PC hay 27 archivos y el `cksum` de cada uno coincide con el del contenedor (comprobado con `device_bash`, excepto este `VERIFICACION.md`, que se reescribió al final con esta sección). No hay `target/`, JSON generados ni `.idea`.
