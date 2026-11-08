# Enunciado · Clínica Veterinaria Patitas

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

La Clínica Veterinaria Patitas ya cuenta con el modelo de clases desarrollado en una etapa anterior: el sistema distingue consultas médicas y vacunaciones, calcula el costo de cada sesión según el tipo de atención y permite marcar las vacunaciones cuyo recordatorio de próxima dosis ya fue enviado al dueño. Sin embargo, ese sistema solo funciona por consola y la información se pierde cada vez que el programa se cierra.

La administración de la clínica ha solicitado transformarlo en una aplicación de escritorio con ventanas, que permita al personal de recepción registrar y eliminar atenciones, y que conserve la información en archivos para que esté disponible la próxima vez que se abra la aplicación.

### Proyecto base

El proyecto base es un proyecto Maven con la configuración inicial, la clase de arranque y el modelo ya implementado y probado, correspondiente al diagrama siguiente. Tu trabajo consiste en construir sobre ese modelo la interfaz gráfica y el almacenamiento en archivos. No debes reescribir ni reemplazar las clases entregadas.

```mermaid
classDiagram
    class Atencion {
        <<abstract>>
        -String paciente
        -int pesoKg
        -int sesiones
        +calcularCosto()* int
        +getTipo()* String
    }
    class Recordable {
        <<interface>>
        +estaRecordatorioEnviado() boolean
        +marcarRecordatorioEnviado() void
    }
    class Consulta {
        -String veterinario
        -boolean urgente
    }
    class Vacunacion {
        -int numeroDosis
        -int mesesDesdeUltimaDosis
        -boolean recordatorioEnviado
    }
    Atencion <|-- Consulta
    Atencion <|-- Vacunacion
    Recordable <|.. Vacunacion
```

El modelo entregado ya aplica las siguientes reglas:

- Consulta: costo base por sesión de $20.000. Si es una consulta de urgencia, el costo se incrementa en un 50%.
- Vacunación: costo base por sesión de $15.000. Si la dosis anterior se aplicó hace 6 meses o menos (paciente al día), el costo se reduce en un 20%.

Datos comunes: el nombre del paciente no puede ser nulo ni vacío, el peso debe estar entre 1 y 150 kg y las sesiones contratadas deben estar entre 1 y 20. Si un dato no cumple, el modelo lo rechaza con un mensaje que explica el motivo.

## 2. Estructura del proyecto

La solución debe separar responsabilidades según el patrón Modelo-Vista-Controlador, con una capa de acceso a datos basada en el patrón DAO/Repository. No hay una única forma de resolverlo: lo importante es que se cumplan los siguientes puntos.

- **Configuración del proyecto:** el proyecto base ya incluye las dependencias de JavaFX y la clase de arranque de la aplicación. Debes agregar al archivo `pom.xml` la dependencia de la librería Gson, necesaria para trabajar con archivos JSON, sin alterar lo que ya existe.
- **Vista:** la ventana se declara en un archivo FXML (por ejemplo, diseñado con Scene Builder) y se carga desde la clase de arranque. No se acepta una interfaz construida solo por código.
- **Vista y controlador:** el FXML indica su clase controladora, y cada componente que usa el controlador lleva `fx:id` en la vista y un atributo con el mismo nombre y tipo en el controlador, con la anotación `@FXML`.
- **Paquetes:** las clases se separan en paquetes distintos para controlador, modelo y persistencia, y el FXML va en los recursos. El modelo y la persistencia no dependen del controlador ni de clases de JavaFX. Las clases nuevas no van en el paquete raíz ni dentro del paquete del modelo.
- **Controlador:** valida las entradas y coordina la interfaz. Los cálculos y reglas del negocio (como el costo de la sesión) los entrega el modelo, y las operaciones con los datos se piden al Repository. No lee ni escribe archivos directamente.
- **Persistencia (DAO/Repository):** una interfaz DAO con las operaciones de lectura y escritura; una implementación del DAO con Gson que maneje los archivos; y una clase Repository que mantenga las atenciones en memoria, ofrezca las operaciones que usa el controlador y delegue en el DAO la lectura y escritura de archivos.

## 3. Ventana de la aplicación

Al ejecutar la aplicación se abre una única ventana, desde la cual se realizan todas las acciones. La ventana debe contener:

- Un formulario de registro con los campos descritos más abajo y un botón Registrar.
- Una tabla con las atenciones registradas y las columnas Tipo, Paciente, Peso (kg), Sesiones y Costo por sesión. El costo que se muestra es el que calcula el modelo para cada atención.
  La tabla usa como fuente de datos una única lista observable, que se mantiene durante toda la ejecución y se actualiza al iniciar y tras cada registro o eliminación (no se crea una lista nueva cada vez). Debe mostrar consultas y vacunaciones sin duplicar ninguna.
- Un botón Eliminar, que quita del registro la atención seleccionada en la tabla. Si no hay una fila seleccionada, la aplicación informa que se debe seleccionar una atención y no realiza ningún cambio.
- Un área de mensajes dentro de la misma ventana, donde se informa el resultado de cada acción: registro exitoso, dato no válido, eliminación, falta de selección o error al leer o guardar los archivos. Cada mensaje identifica la atención o la causa, y reemplaza al anterior. Los mensajes no se muestran por consola.

**Campos del formulario**

- Un selector de tipo con las opciones Consulta y Vacunación.
- Datos comunes: paciente, peso (kg) y sesiones.
- Datos de la consulta: veterinario y si es urgente (sí/no).
- Datos de la vacunación: número de dosis, meses desde la última dosis y si el recordatorio ya fue enviado (sí/no).

Al cambiar el tipo en el selector, solo quedan habilitados los datos específicos del tipo elegido; los datos del otro tipo quedan deshabilitados y no se consideran al registrar.

Al presionar Registrar, la aplicación valida los datos según la sección 4. Si son correctos, registra la atención, la tabla la muestra de inmediato, el área de mensajes informa la confirmación (por ejemplo: “Max (Consulta) registrada correctamente.”) y el formulario queda vacío para un nuevo registro.

## 4. Validaciones del formulario

Antes de registrar, el formulario debe verificar los datos ingresados según las siguientes reglas:

- Tipo: debe estar seleccionado.
- Paciente: no puede estar vacío ni contener solo espacios.
- Peso: número entero entre 1 y 150 kg.
- Sesiones: número entero entre 1 y 20.
- Veterinario (solo consulta): no puede estar vacío ni contener solo espacios.
- Número de dosis (solo vacunación): número entero entre 1 y 12.
- Meses desde la última dosis (solo vacunación): número entero entre 0 y 120.

Las reglas de paciente, peso y sesiones son las mismas que ya aplica el modelo entregado. Si algún dato no es válido:

- la atención no se registra;
- el área de mensajes indica qué campo es incorrecto y por qué;
- los valores ya ingresados se mantienen para que puedan corregirse;
- la aplicación no se detiene ni muestra errores por consola.

## 5. Almacenamiento de la información

La información de las atenciones debe conservarse entre una ejecución y otra, en formato JSON, en la carpeta raíz del proyecto:

- `consultas.json`: contiene las consultas registradas.
- `vacunaciones.json`: contiene las vacunaciones registradas.

El comportamiento esperado es el siguiente:

- Al iniciar, la aplicación carga automáticamente la información de ambos archivos y la muestra en la tabla.
- Si los archivos no existen (por ejemplo, la primera vez que se ejecuta), la aplicación inicia con la tabla vacía y sin mostrar errores.
- Cada vez que se registra o se elimina una atención, los archivos se actualizan automáticamente, sin que el usuario deba presionar un botón para guardar.
- Lo que muestra la tabla y lo guardado en los archivos debe coincidir en todo momento.
- Si no se puede leer o guardar un archivo, el área de mensajes lo informa en la misma ventana. Si falla un guardado, no se informa éxito y el cambio no se aplica, de modo que la tabla y los archivos siguen coincidiendo.

## 6. Datos de prueba y resultado esperado

Registra desde la aplicación las siguientes atenciones:

| Tipo | Paciente | Peso (kg) | Sesiones | Datos específicos | Costo por sesión |
|---|---|---|---|---|---|
| Consulta | Max | 30 | 2 | Veterinario: Dra. Pérez \| Urgente: sí | $30.000 |
| Consulta | Tom | 6 | 1 | Veterinario: Dr. Lagos \| Urgente: no | $20.000 |
| Vacunación | Luna | 12 | 3 | Dosis: 2 \| Meses desde la última dosis: 4 \| Recordatorio enviado: sí | $12.000 |
| Vacunación | Rocky | 25 | 1 | Dosis: 1 \| Meses desde la última dosis: 10 \| Recordatorio enviado: no | $15.000 |

Luego de registrarlas, la ventana debe mostrar las cuatro atenciones con sus costos. Si se presiona Eliminar sin seleccionar una fila, el aviso aparece en la misma ventana.

Si un dato del formulario no es válido (por ejemplo, un peso `abc` o unas sesiones `0`), el área de mensajes indica el campo con problemas y se mantienen los valores ingresados.

**Verificación de almacenamiento**

- Cierra la aplicación y vuelve a abrirla: la tabla debe mostrar las mismas cuatro atenciones, y la vacunación de Luna debe seguir con el recordatorio enviado (se comprueba en `vacunaciones.json`).
- Elimina a Tom, cierra la aplicación y vuelve a abrirla: Tom ya no debe aparecer.
