# Enunciado · Centro Deportivo Los Aromos

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

El Centro Deportivo Los Aromos arrienda por turnos horarios sus canchas de pádel y sus salas de reuniones. Ya cuenta con el modelo de clases desarrollado en una etapa anterior: el sistema distingue turnos de cancha y turnos de sala, calcula el total a pagar de cada turno según su tipo y permite marcar como confirmados los turnos de sala. Sin embargo, ese sistema solo funciona por consola y la información se pierde cada vez que el programa se cierra.

La administración del centro ha solicitado transformarlo en una aplicación de escritorio con ventanas, que permita al personal de recepción registrar y eliminar turnos, y que conserve la información en archivos para que esté disponible la próxima vez que se abra la aplicación.

### Proyecto base

El proyecto base es un proyecto Maven con la configuración inicial, la clase de arranque y el modelo ya implementado y probado, correspondiente al diagrama siguiente. Tu trabajo consiste en construir sobre ese modelo la interfaz gráfica y el almacenamiento en archivos. No debes reescribir ni reemplazar las clases entregadas.

```mermaid
classDiagram
    class Turno {
        <<abstract>>
        -String cliente
        -int horaInicio
        -int horas
        +calcularTotal()* int
        +getTipo()* String
    }
    class Confirmable {
        <<interface>>
        +estaConfirmado() boolean
        +confirmar() void
    }
    class TurnoCancha {
        -int numeroCancha
        -boolean conIluminacion
    }
    class TurnoSala {
        -int asistentes
        -int diasAnticipacion
        -boolean confirmado
    }
    Turno <|-- TurnoCancha
    Turno <|-- TurnoSala
    Confirmable <|.. TurnoSala
```

El modelo entregado ya aplica las siguientes reglas:

- Turno de cancha: tarifa base de $12.000 por hora. Si se contrata con iluminación, el total del turno se incrementa en un 25%.
- Turno de sala: tarifa base de $8.000 por hora. Si el turno se reservó con 7 días de anticipación o más, el total del turno se reduce en un 15%.

Datos comunes: el nombre del cliente no puede ser nulo ni vacío, la hora de inicio debe estar entre las 8 y las 21 horas y las horas contratadas deben estar entre 1 y 6. Si un dato no cumple, el modelo lo rechaza con un mensaje que explica el motivo.

## 2. Estructura del proyecto

La solución debe separar responsabilidades según el patrón Modelo-Vista-Controlador, con una capa de acceso a datos basada en el patrón DAO/Repository. No hay una única forma de resolverlo: lo importante es que se cumplan los siguientes puntos.

- **Configuración del proyecto:** el proyecto base ya incluye las dependencias de JavaFX y la clase de arranque de la aplicación. Debes agregar al archivo `pom.xml` la dependencia de la librería Gson, necesaria para trabajar con archivos JSON, sin alterar lo que ya existe.
- **Vista:** la ventana se declara en un archivo FXML (por ejemplo, diseñado con Scene Builder) y se carga desde la clase de arranque. No se acepta una interfaz construida solo por código.
- **Vista y controlador:** el FXML indica su clase controladora, y cada componente que usa el controlador lleva `fx:id` en la vista y un atributo con el mismo nombre y tipo en el controlador, con la anotación `@FXML`.
- **Paquetes:** las clases se separan en paquetes distintos para controlador, modelo y persistencia, y el FXML va en los recursos. El modelo y la persistencia no dependen del controlador ni de clases de JavaFX. Las clases nuevas no van en el paquete raíz ni dentro del paquete del modelo.
- **Controlador:** valida las entradas y coordina la interfaz. Los cálculos y reglas del negocio (como el total a pagar) los entrega el modelo, y las operaciones con los datos se piden al Repository. No lee ni escribe archivos directamente.
- **Persistencia (DAO/Repository):** una interfaz DAO con las operaciones de lectura y escritura; una implementación del DAO con Gson que maneje los archivos; y una clase Repository que mantenga los turnos en memoria, ofrezca las operaciones que usa el controlador y delegue en el DAO la lectura y escritura de archivos.

## 3. Ventana de la aplicación

Al ejecutar la aplicación se abre una única ventana, desde la cual se realizan todas las acciones. La ventana debe contener:

- Un formulario de registro con los campos descritos más abajo y un botón Registrar.
- Una tabla con los turnos registrados y las columnas Tipo, Cliente, Hora de inicio, Horas y Total a pagar. El total que se muestra es el que calcula el modelo para cada turno.
  La tabla usa como fuente de datos una única lista observable, que se mantiene durante toda la ejecución y se actualiza al iniciar y tras cada registro o eliminación (no se crea una lista nueva cada vez). Debe mostrar turnos de cancha y de sala sin duplicar ninguno.
- Un botón Eliminar, que quita del registro el turno seleccionado en la tabla. Si no hay una fila seleccionada, la aplicación informa que se debe seleccionar un turno y no realiza ningún cambio.
- Un área de mensajes dentro de la misma ventana, donde se informa el resultado de cada acción: registro exitoso, dato no válido, eliminación, falta de selección o error al leer o guardar los archivos. Cada mensaje identifica el turno o la causa, y reemplaza al anterior. Los mensajes no se muestran por consola.

**Campos del formulario**

- Un selector de tipo con las opciones Cancha y Sala.
- Datos comunes: cliente, hora de inicio y horas del turno.
- Datos del turno de cancha: número de cancha y si se contrata con iluminación (sí/no).
- Datos del turno de sala: cantidad de asistentes, días de anticipación y si el turno ya está confirmado (sí/no).

Al cambiar el tipo en el selector, solo quedan habilitados los datos específicos del tipo elegido; los datos del otro tipo quedan deshabilitados y no se consideran al registrar.

Al presionar Registrar, la aplicación valida los datos según la sección 4. Si son correctos, registra el turno, la tabla lo muestra de inmediato, el área de mensajes informa la confirmación (por ejemplo: “Valentina Rojas (Cancha) registrado correctamente.”) y el formulario queda vacío para un nuevo registro.

## 4. Validaciones del formulario

Antes de registrar, el formulario debe verificar los datos ingresados según las siguientes reglas:

- Tipo: debe estar seleccionado.
- Cliente: no puede estar vacío ni contener solo espacios.
- Hora de inicio: número entero entre 8 y 21.
- Horas del turno: número entero entre 1 y 6.
- Número de cancha (solo cancha): número entero entre 1 y 8.
- Asistentes (solo sala): número entero entre 1 y 40.
- Días de anticipación (solo sala): número entero entre 0 y 60.

Las reglas de cliente, hora de inicio y horas del turno son las mismas que ya aplica el modelo entregado. Si algún dato no es válido:

- el turno no se registra;
- el área de mensajes indica qué campo es incorrecto y por qué;
- los valores ya ingresados se mantienen para que puedan corregirse;
- la aplicación no se detiene ni muestra errores por consola.

## 5. Almacenamiento de la información

La información de los turnos debe conservarse entre una ejecución y otra, en formato JSON, en la carpeta raíz del proyecto:

- `canchas.json`: contiene los turnos de cancha registrados.
- `salas.json`: contiene los turnos de sala registrados.

El comportamiento esperado es el siguiente:

- Al iniciar, la aplicación carga automáticamente la información de ambos archivos y la muestra en la tabla.
- Si los archivos no existen (por ejemplo, la primera vez que se ejecuta), la aplicación inicia con la tabla vacía y sin mostrar errores.
- Cada vez que se registra o se elimina un turno, los archivos se actualizan automáticamente, sin que el usuario deba presionar un botón para guardar.
- Lo que muestra la tabla y lo guardado en los archivos debe coincidir en todo momento.
- Si no se puede leer o guardar un archivo, el área de mensajes lo informa en la misma ventana. Si falla un guardado, no se informa éxito y el cambio no se aplica, de modo que la tabla y los archivos siguen coincidiendo.

## 6. Datos de prueba y resultado esperado

Registra desde la aplicación los siguientes turnos:

| Tipo | Cliente | Hora de inicio | Horas | Datos específicos | Total a pagar |
|---|---|---|---|---|---|
| Cancha | Valentina Rojas | 18 | 2 | Cancha: 3 \| Iluminación: sí | $30.000 |
| Cancha | Hugo Pardo | 10 | 1 | Cancha: 5 \| Iluminación: no | $12.000 |
| Sala | Camila Fuentes | 15 | 3 | Asistentes: 12 \| Días de anticipación: 10 \| Confirmado: sí | $20.400 |
| Sala | Iván Araya | 9 | 1 | Asistentes: 4 \| Días de anticipación: 2 \| Confirmado: no | $8.000 |

Luego de registrarlos, la ventana debe mostrar los cuatro turnos con sus totales. Si se presiona Eliminar sin seleccionar una fila, el aviso aparece en la misma ventana.

Si un dato del formulario no es válido (por ejemplo, una hora de inicio `abc` o unas horas del turno `0`), el área de mensajes indica el campo con problemas y se mantienen los valores ingresados.

**Verificación de almacenamiento**

- Cierra la aplicación y vuelve a abrirla: la tabla debe mostrar los mismos cuatro turnos, y el turno de Camila Fuentes debe seguir confirmado (se comprueba en `salas.json`).
- Elimina el turno de Hugo Pardo, cierra la aplicación y vuelve a abrirla: Hugo Pardo ya no debe aparecer.
