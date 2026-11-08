# Enunciado · Agenda Aurora

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

La oficina Aurora organiza su día a día con una agenda de compromisos. Ya cuenta con el modelo de clases desarrollado en una etapa anterior: el sistema distingue reuniones y eventos, calcula el costo por asistente de cada compromiso según su tipo y permite marcar los eventos cuya asistencia de invitados ya fue confirmada. Sin embargo, ese sistema solo funciona por consola y la información se pierde cada vez que el programa se cierra.

La administración de la oficina ha solicitado transformarlo en una aplicación de escritorio con ventanas, que permita al personal registrar y eliminar compromisos, y que conserve la información en archivos para que esté disponible la próxima vez que se abra la aplicación.

### Proyecto base

El proyecto base es un proyecto Maven con la configuración inicial, la clase de arranque y el modelo ya implementado y probado, correspondiente al diagrama siguiente. Tu trabajo consiste en construir sobre ese modelo la interfaz gráfica y el almacenamiento en archivos. No debes reescribir ni reemplazar las clases entregadas.

```mermaid
classDiagram
    class Compromiso {
        <<abstract>>
        -String titulo
        -int duracionMin
        -int asistentes
        +calcularCostoPorAsistente()* int
        +getTipo()* String
    }
    class Confirmable {
        <<interface>>
        +estaConfirmado() boolean
        +confirmar() void
    }
    class Reunion {
        -String organizador
        -boolean virtual
    }
    class Evento {
        -String lugar
        -int diasAnticipacion
        -boolean confirmado
    }
    Compromiso <|-- Reunion
    Compromiso <|-- Evento
    Confirmable <|.. Evento
```

El modelo entregado ya aplica las siguientes reglas:

- Reunión: costo base por asistente de $7.500. Si la reunión es virtual, el costo se reduce en un 40%.
- Evento: costo base por asistente de $9.000. Si el evento se agenda con 7 días o menos de anticipación (organización de última hora), el costo se incrementa en un 35%.

Datos comunes: el título no puede ser nulo ni vacío, la duración debe estar entre 15 y 480 minutos y los asistentes deben estar entre 1 y 200. Si un dato no cumple, el modelo lo rechaza con un mensaje que explica el motivo.

## 2. Estructura del proyecto

La solución debe separar responsabilidades según el patrón Modelo-Vista-Controlador, con una capa de acceso a datos basada en el patrón DAO/Repository. No hay una única forma de resolverlo: lo importante es que se cumplan los siguientes puntos.

- **Configuración del proyecto:** el proyecto base ya incluye las dependencias de JavaFX y la clase de arranque de la aplicación. Debes agregar al archivo `pom.xml` la dependencia de la librería Gson, necesaria para trabajar con archivos JSON, sin alterar lo que ya existe.
- **Vista:** la ventana se declara en un archivo FXML (por ejemplo, diseñado con Scene Builder) y se carga desde la clase de arranque. No se acepta una interfaz construida solo por código.
- **Vista y controlador:** el FXML indica su clase controladora, y cada componente que usa el controlador lleva `fx:id` en la vista y un atributo con el mismo nombre y tipo en el controlador, con la anotación `@FXML`.
- **Paquetes:** las clases se separan en paquetes distintos para controlador, modelo y persistencia, y el FXML va en los recursos. El modelo y la persistencia no dependen del controlador ni de clases de JavaFX. Las clases nuevas no van en el paquete raíz ni dentro del paquete del modelo.
- **Controlador:** valida las entradas y coordina la interfaz. Los cálculos y reglas del negocio (como el costo por asistente) los entrega el modelo, y las operaciones con los datos se piden al Repository. No lee ni escribe archivos directamente.
- **Persistencia (DAO/Repository):** una interfaz DAO con las operaciones de lectura y escritura; una implementación del DAO con Gson que maneje los archivos; y una clase Repository que mantenga los compromisos en memoria, ofrezca las operaciones que usa el controlador y delegue en el DAO la lectura y escritura de archivos.

## 3. Ventana de la aplicación

Al ejecutar la aplicación se abre una única ventana, desde la cual se realizan todas las acciones. La ventana debe contener:

- Un formulario de registro con los campos descritos más abajo y un botón Registrar.
- Una tabla con los compromisos registrados y las columnas Tipo, Título, Duración (min), Asistentes y Costo por asistente. El costo que se muestra es el que calcula el modelo para cada compromiso.
  La tabla usa como fuente de datos una única lista observable, que se mantiene durante toda la ejecución y se actualiza al iniciar y tras cada registro o eliminación (no se crea una lista nueva cada vez). Debe mostrar reuniones y eventos sin duplicar ninguno.
- Un botón Eliminar, que quita del registro el compromiso seleccionado en la tabla. Si no hay una fila seleccionada, la aplicación informa que se debe seleccionar un compromiso y no realiza ningún cambio.
- Un área de mensajes dentro de la misma ventana, donde se informa el resultado de cada acción: registro exitoso, dato no válido, eliminación, falta de selección o error al leer o guardar los archivos. Cada mensaje identifica el compromiso o la causa, y reemplaza al anterior. Los mensajes no se muestran por consola.

**Campos del formulario**

- Un selector de tipo con las opciones Reunión y Evento.
- Datos comunes: título, duración (minutos) y asistentes.
- Datos de la reunión: organizador y si es virtual (sí/no).
- Datos del evento: lugar, días de anticipación con que se agenda y si la asistencia ya fue confirmada (sí/no).

Al cambiar el tipo en el selector, solo quedan habilitados los datos específicos del tipo elegido; los datos del otro tipo quedan deshabilitados y no se consideran al registrar.

Al presionar Registrar, la aplicación valida los datos según la sección 4. Si son correctos, registra el compromiso, la tabla lo muestra de inmediato, el área de mensajes informa la confirmación (por ejemplo: “Revisión de presupuesto (Reunión) registrado correctamente.”) y el formulario queda vacío para un nuevo registro.

## 4. Validaciones del formulario

Antes de registrar, el formulario debe verificar los datos ingresados según las siguientes reglas:

- Tipo: debe estar seleccionado.
- Título: no puede estar vacío ni contener solo espacios.
- Duración: número entero entre 15 y 480 minutos.
- Asistentes: número entero entre 1 y 200.
- Organizador (solo reunión): no puede estar vacío ni contener solo espacios.
- Lugar (solo evento): no puede estar vacío ni contener solo espacios.
- Días de anticipación (solo evento): número entero entre 0 y 365.

Las reglas de título, duración y asistentes son las mismas que ya aplica el modelo entregado. Si algún dato no es válido:

- el compromiso no se registra;
- el área de mensajes indica qué campo es incorrecto y por qué;
- los valores ya ingresados se mantienen para que puedan corregirse;
- la aplicación no se detiene ni muestra errores por consola.

## 5. Almacenamiento de la información

La información de los compromisos debe conservarse entre una ejecución y otra, en formato JSON, en la carpeta raíz del proyecto:

- `reuniones.json`: contiene las reuniones registradas.
- `eventos.json`: contiene los eventos registrados.

El comportamiento esperado es el siguiente:

- Al iniciar, la aplicación carga automáticamente la información de ambos archivos y la muestra en la tabla.
- Si los archivos no existen (por ejemplo, la primera vez que se ejecuta), la aplicación inicia con la tabla vacía y sin mostrar errores.
- Cada vez que se registra o se elimina un compromiso, los archivos se actualizan automáticamente, sin que el usuario deba presionar un botón para guardar.
- Lo que muestra la tabla y lo guardado en los archivos debe coincidir en todo momento.
- Si no se puede leer o guardar un archivo, el área de mensajes lo informa en la misma ventana. Si falla un guardado, no se informa éxito y el cambio no se aplica, de modo que la tabla y los archivos siguen coincidiendo.

## 6. Datos de prueba y resultado esperado

Registra desde la aplicación los siguientes compromisos:

| Tipo | Título | Duración (min) | Asistentes | Datos específicos | Costo por asistente |
|---|---|---|---|---|---|
| Reunión | Revisión de presupuesto | 90 | 8 | Organizador: Marcela Soto \| Virtual: no | $7.500 |
| Reunión | Coordinación con proveedores | 45 | 5 | Organizador: Andrés Rojas \| Virtual: sí | $4.500 |
| Evento | Cena de aniversario | 180 | 40 | Lugar: Casino Central \| Anticipación: 3 días \| Confirmado: sí | $12.150 |
| Evento | Taller de capacitación | 240 | 25 | Lugar: Auditorio Norte \| Anticipación: 30 días \| Confirmado: no | $9.000 |

Luego de registrarlos, la ventana debe mostrar los cuatro compromisos con sus costos. Si se presiona Eliminar sin seleccionar una fila, el aviso aparece en la misma ventana.

Si un dato del formulario no es válido (por ejemplo, una duración `abc` o unos asistentes `0`), el área de mensajes indica el campo con problemas y se mantienen los valores ingresados.

**Verificación de almacenamiento**

- Cierra la aplicación y vuelve a abrirla: la tabla debe mostrar los mismos cuatro compromisos, y la cena de aniversario debe seguir confirmada (se comprueba en `eventos.json`).
- Elimina la coordinación con proveedores, cierra la aplicación y vuelve a abrirla: esa reunión ya no debe aparecer.
