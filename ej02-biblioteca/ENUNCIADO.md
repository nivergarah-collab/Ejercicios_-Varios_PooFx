# Enunciado ejercicio base · Biblioteca Municipal Los Aromos

## Condiciones del ejercicio

**Del entorno y la entrega**

- El ejercicio es de carácter individual.
- El programa debe compilar y ejecutarse correctamente. Una solución que no compile no permite evidenciar ningún indicador de logro.
- La entrega se realiza según las instrucciones de quien dicta el ejercicio.

**De tu solución**

- El código debe organizarse en clases separadas, respetando las convenciones de nomenclatura de Java: PascalCase para nombres de clase, camelCase para métodos y atributos.
- Puedes crear métodos o clases auxiliares siempre que no contradigan los requerimientos del caso.
- Para que la práctica sirva, resuélvelo sin ayuda de herramientas de inteligencia artificial ni de soluciones ya hechas.

---

## 1. Contexto del caso

La Biblioteca Municipal Los Aromos ya cuenta con el modelo de clases desarrollado en una etapa anterior: el sistema distingue préstamos de libros y de material audiovisual, calcula la tarifa de préstamo de cada material según su tipo y permite marcar como renovados los préstamos de libros. Sin embargo, ese sistema solo funciona por consola y la información se pierde cada vez que el programa se cierra.

La administración de la biblioteca ha solicitado transformarlo en una aplicación de escritorio con ventanas, que permita al personal del mesón registrar y eliminar préstamos, y que conserve la información en archivos para que esté disponible la próxima vez que se abra la aplicación.

### Proyecto base

El proyecto base es un proyecto Maven con la configuración inicial, la clase de arranque y el modelo ya implementado y probado, correspondiente al diagrama siguiente. Tu trabajo consiste en construir sobre ese modelo la interfaz gráfica y el almacenamiento en archivos. No debes reescribir ni reemplazar las clases entregadas.

```mermaid
classDiagram
    class Prestamo {
        <<abstract>>
        -String socio
        -int diasPrestamo
        -int materiales
        +calcularTarifa()* int
        +getTipo()* String
    }
    class Renovable {
        <<interface>>
        +estaRenovado() boolean
        +renovar() void
    }
    class PrestamoLibro {
        -String titulo
        -boolean novedad
        -boolean renovado
    }
    class PrestamoAudiovisual {
        -String formato
        -int duracionMinutos
    }
    Prestamo <|-- PrestamoLibro
    Prestamo <|-- PrestamoAudiovisual
    Renovable <|.. PrestamoLibro
```

El modelo entregado ya aplica las siguientes reglas:

- Préstamo de libro: tarifa base de $1.800 por material. Si el libro es una novedad, la tarifa se incrementa en un 25%.
- Préstamo audiovisual: tarifa base de $2.400 por material. Si el material dura más de 120 minutos, la tarifa se incrementa en un 20%.

Datos comunes: el nombre del socio no puede ser nulo ni vacío, el plazo de préstamo debe estar entre 1 y 30 días y los materiales prestados deben estar entre 1 y 5. Si un dato no cumple, el modelo lo rechaza con un mensaje que explica el motivo.

## 2. Estructura del proyecto

La solución debe separar responsabilidades según el patrón Modelo-Vista-Controlador, con una capa de acceso a datos basada en el patrón DAO/Repository. No hay una única forma de resolverlo: lo importante es que se cumplan los siguientes puntos.

- **Configuración del proyecto:** el proyecto base ya incluye las dependencias de JavaFX y la clase de arranque de la aplicación. Debes agregar al archivo `pom.xml` la dependencia de la librería Gson (versión 2.11.0), necesaria para trabajar con archivos JSON, sin alterar lo que ya existe.
- **Vista:** la ventana se declara en un archivo FXML (por ejemplo, diseñado con Scene Builder) y se carga desde la clase de arranque. No se acepta una interfaz construida solo por código.
- **Vista y controlador:** el FXML indica su clase controladora, y cada componente que usa el controlador lleva `fx:id` en la vista y un atributo con el mismo nombre y tipo en el controlador, con la anotación `@FXML`.
- **Paquetes:** las clases se separan en paquetes distintos para controlador, modelo y persistencia, y el FXML va en los recursos. El modelo y la persistencia no dependen del controlador ni de clases de JavaFX. Las clases nuevas no van en el paquete raíz ni dentro del paquete del modelo.
- **Controlador:** valida las entradas y coordina la interfaz. Los cálculos y reglas del negocio (como la tarifa) los entrega el modelo, y las operaciones con los datos se piden al Repository. No lee ni escribe archivos directamente.
- **Persistencia (DAO/Repository):** una interfaz DAO con las operaciones de lectura y escritura; una implementación del DAO con Gson que maneje los archivos; y una clase Repository que mantenga los préstamos en memoria, ofrezca las operaciones que usa el controlador y delegue en el DAO la lectura y escritura de archivos.

## 3. Ventana de la aplicación

Al ejecutar la aplicación se abre una única ventana, desde la cual se realizan todas las acciones. La ventana debe contener:

- Un formulario de registro con los campos descritos más abajo y un botón Registrar.
- Una tabla con los préstamos registrados y las columnas Tipo, Socio, Días, Materiales y Tarifa. La tarifa que se muestra es la que calcula el modelo para cada préstamo.
  La tabla usa como fuente de datos una única lista observable, que se mantiene durante toda la ejecución y se actualiza al iniciar y tras cada registro o eliminación (no se crea una lista nueva cada vez). Debe mostrar préstamos de libros y audiovisuales sin duplicar ninguno.
- Un botón Eliminar, que quita del registro el préstamo seleccionado en la tabla. Si no hay una fila seleccionada, la aplicación informa que se debe seleccionar un préstamo y no realiza ningún cambio.
- Un área de mensajes dentro de la misma ventana, donde se informa el resultado de cada acción: registro exitoso, dato no válido, eliminación, falta de selección o error al leer o guardar los archivos. Cada mensaje identifica el préstamo o la causa, y reemplaza al anterior. Los mensajes no se muestran por consola.

**Campos del formulario**

- Un selector de tipo con las opciones Libro y Audiovisual.
- Datos comunes: socio, plazo en días y cantidad de materiales.
- Datos del préstamo de libro: título, si es novedad (sí/no) y si ya fue renovado (sí/no).
- Datos del préstamo audiovisual: formato (por ejemplo, DVD) y duración en minutos.

Al cambiar el tipo en el selector, solo quedan habilitados los datos específicos del tipo elegido; los datos del otro tipo quedan deshabilitados y no se consideran al registrar.

Al presionar Registrar, la aplicación valida los datos según la sección 4. Si son correctos, registra el préstamo, la tabla lo muestra de inmediato, el área de mensajes informa la confirmación (por ejemplo: “Préstamo de Camila Rojas (Libro) registrado correctamente.”) y el formulario queda vacío para un nuevo registro.

## 4. Validaciones del formulario

Antes de registrar, el formulario debe verificar los datos ingresados según las siguientes reglas:

- Tipo: debe estar seleccionado.
- Socio: no puede estar vacío ni contener solo espacios.
- Plazo: número entero de días entre 1 y 30.
- Materiales: número entero entre 1 y 5.
- Título (solo libro): no puede estar vacío ni contener solo espacios.
- Formato (solo audiovisual): no puede estar vacío ni contener solo espacios.
- Duración (solo audiovisual): número entero de minutos entre 1 y 600.

Las reglas de socio, plazo y materiales son las mismas que ya aplica el modelo entregado. Si algún dato no es válido:

- el préstamo no se registra;
- el área de mensajes indica qué campo es incorrecto y por qué;
- los valores ya ingresados se mantienen para que puedan corregirse;
- la aplicación no se detiene ni muestra errores por consola.

## 5. Almacenamiento de la información

La información de los préstamos debe conservarse entre una ejecución y otra, en formato JSON, en la carpeta raíz del proyecto:

- `prestamos-libros.json`: contiene los préstamos de libros registrados.
- `prestamos-audiovisuales.json`: contiene los préstamos audiovisuales registrados.

El comportamiento esperado es el siguiente:

- Al iniciar, la aplicación carga automáticamente la información de ambos archivos y la muestra en la tabla.
- Si los archivos no existen (por ejemplo, la primera vez que se ejecuta), la aplicación inicia con la tabla vacía y sin mostrar errores.
- Cada vez que se registra o se elimina un préstamo, los archivos se actualizan automáticamente, sin que el usuario deba presionar un botón para guardar.
- Lo que muestra la tabla y lo guardado en los archivos debe coincidir en todo momento.
- Si no se puede leer o guardar un archivo, el área de mensajes lo informa. Si falla un guardado, no se informa éxito y el cambio no se aplica, de modo que la tabla y los archivos siguen coincidiendo.

## 6. Datos de prueba y resultado esperado

Registra desde la aplicación los siguientes préstamos:

| Tipo | Socio | Días | Materiales | Datos específicos | Tarifa |
|---|---|---|---|---|---|
| Libro | Camila Rojas | 14 | 1 | Título: Mapas del Desierto \| Novedad: sí \| Renovado: no | $2.250 |
| Libro | Felipe Mora | 7 | 2 | Título: Huertas Urbanas \| Novedad: no \| Renovado: sí | $1.800 |
| Audiovisual | Ignacia Vera | 3 | 1 | Formato: Blu-ray \| Duración: 150 minutos | $2.880 |
| Audiovisual | Tomás Lagos | 5 | 1 | Formato: DVD \| Duración: 90 minutos | $2.400 |

Luego de registrarlos, la ventana debe mostrar los cuatro préstamos con sus tarifas. Si se presiona Eliminar sin seleccionar una fila, el aviso aparece en la misma ventana.

Si un dato del formulario no es válido (por ejemplo, un plazo `abc` o unos materiales `0`), el área de mensajes indica el campo con problemas y se mantienen los valores ingresados.

**Verificación de almacenamiento**

- Cierra la aplicación y vuelve a abrirla: la tabla debe mostrar los mismos cuatro préstamos, y el préstamo de Felipe Mora debe seguir marcado como renovado en `prestamos-libros.json`.
- Elimina el préstamo de Tomás Lagos, cierra la aplicación y vuelve a abrirla: ese préstamo ya no debe aparecer.
