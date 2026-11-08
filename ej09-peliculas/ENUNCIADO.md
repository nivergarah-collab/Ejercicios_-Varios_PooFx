# Enunciado · Cartelera Cine Horizonte

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

El cine Cine Horizonte ya cuenta con el modelo de clases desarrollado en una etapa anterior: el sistema distingue funciones regulares y funciones populares, calcula el precio de la entrada según el tipo de función y permite marcar las funciones populares cuyas entradas ya fueron reembolsadas. Sin embargo, ese sistema solo funciona por consola y la información se pierde cada vez que el programa se cierra.

La administración del cine ha solicitado transformarlo en una aplicación de escritorio con ventanas, que permita al personal de cartelera registrar y eliminar funciones, y que conserve la información en archivos para que esté disponible la próxima vez que se abra la aplicación.

### Proyecto base

El proyecto base es un proyecto Maven con la configuración inicial, la clase de arranque y el modelo ya implementado y probado, correspondiente al diagrama siguiente. Tu trabajo consiste en construir sobre ese modelo la interfaz gráfica y el almacenamiento en archivos. No debes reescribir ni reemplazar las clases entregadas.

```mermaid
classDiagram
    class Funcion {
        <<abstract>>
        -String pelicula
        -int duracionMin
        -int entradas
        +calcularPrecioEntrada()* int
        +getTipo()* String
    }
    class Reembolsable {
        <<interface>>
        +estaReembolsada() boolean
        +marcarReembolsada() void
    }
    class FuncionRegular {
        -String sala
        -boolean es3D
    }
    class FuncionPopular {
        -String distribuidora
        -int diasEnCartelera
        -boolean reembolsada
    }
    Funcion <|-- FuncionRegular
    Funcion <|-- FuncionPopular
    Reembolsable <|.. FuncionPopular
```

El modelo entregado ya aplica las siguientes reglas:

- Función regular: precio base de la entrada de $6.500. Si la función es en 3D, el precio se incrementa en un 20%.
- Función popular: precio base de la entrada de $4.800. Si la película lleva 30 días o más en cartelera, el precio se reduce en un 25%.

Datos comunes: el nombre de la película no puede ser nulo ni vacío, la duración debe estar entre 60 y 240 minutos y las entradas vendidas deben estar entre 1 y 300. Si un dato no cumple, el modelo lo rechaza con un mensaje que explica el motivo.

## 2. Estructura del proyecto

La solución debe separar responsabilidades según el patrón Modelo-Vista-Controlador, con una capa de acceso a datos basada en el patrón DAO/Repository. No hay una única forma de resolverlo: lo importante es que se cumplan los siguientes puntos.

- **Configuración del proyecto:** el proyecto base ya incluye las dependencias de JavaFX y la clase de arranque de la aplicación. Debes agregar al archivo `pom.xml` la dependencia de la librería Gson, necesaria para trabajar con archivos JSON, sin alterar lo que ya existe.
- **Vista:** la ventana se declara en un archivo FXML (por ejemplo, diseñado con Scene Builder) y se carga desde la clase de arranque. No se acepta una interfaz construida solo por código.
- **Vista y controlador:** el FXML indica su clase controladora, y cada componente que usa el controlador lleva `fx:id` en la vista y un atributo con el mismo nombre y tipo en el controlador, con la anotación `@FXML`.
- **Paquetes:** las clases se separan en paquetes distintos para controlador, modelo y persistencia, y el FXML va en los recursos. El modelo y la persistencia no dependen del controlador ni de clases de JavaFX. Las clases nuevas no van en el paquete raíz ni dentro del paquete del modelo.
- **Controlador:** valida las entradas y coordina la interfaz. Los cálculos y reglas del negocio (como el precio de la entrada) los entrega el modelo, y las operaciones con los datos se piden al Repository. No lee ni escribe archivos directamente.
- **Persistencia (DAO/Repository):** una interfaz DAO con las operaciones de lectura y escritura; una implementación del DAO con Gson que maneje los archivos; y una clase Repository que mantenga las funciones en memoria, ofrezca las operaciones que usa el controlador y delegue en el DAO la lectura y escritura de archivos.

## 3. Ventana de la aplicación

Al ejecutar la aplicación se abre una única ventana, desde la cual se realizan todas las acciones. La ventana debe contener:

- Un formulario de registro con los campos descritos más abajo y un botón Registrar.
- Una tabla con las funciones registradas y las columnas Tipo, Película, Duración (min), Entradas y Precio entrada. El precio que se muestra es el que calcula el modelo para cada función.
  La tabla usa como fuente de datos una única lista observable, que se mantiene durante toda la ejecución y se actualiza al iniciar y tras cada registro o eliminación (no se crea una lista nueva cada vez). Debe mostrar funciones regulares y populares sin duplicar ninguna.
- Un botón Eliminar, que quita del registro la función seleccionada en la tabla. Si no hay una fila seleccionada, la aplicación informa que se debe seleccionar una función y no realiza ningún cambio.
- Un área de mensajes dentro de la misma ventana, donde se informa el resultado de cada acción: registro exitoso, dato no válido, eliminación, falta de selección o error al leer o guardar los archivos. Cada mensaje identifica la función o la causa, y reemplaza al anterior. Los mensajes no se muestran por consola.

**Campos del formulario**

- Un selector de tipo con las opciones Función regular y Función popular.
- Datos comunes: película, duración (minutos) y entradas vendidas.
- Datos de la función regular: sala y si es en 3D (sí/no).
- Datos de la función popular: distribuidora, días en cartelera y si las entradas ya fueron reembolsadas (sí/no).

Al cambiar el tipo en el selector, solo quedan habilitados los datos específicos del tipo elegido; los datos del otro tipo quedan deshabilitados y no se consideran al registrar.

Al presionar Registrar, la aplicación valida los datos según la sección 4. Si son correctos, registra la función, la tabla la muestra de inmediato, el área de mensajes informa la confirmación (por ejemplo: “El Faro Dormido (Función regular) registrada correctamente.”) y el formulario queda vacío para un nuevo registro.

## 4. Validaciones del formulario

Antes de registrar, el formulario debe verificar los datos ingresados según las siguientes reglas:

- Tipo: debe estar seleccionado.
- Película: no puede estar vacía ni contener solo espacios.
- Duración: número entero entre 60 y 240 minutos.
- Entradas vendidas: número entero entre 1 y 300.
- Sala (solo función regular): no puede estar vacía ni contener solo espacios.
- Distribuidora (solo función popular): no puede estar vacía ni contener solo espacios.
- Días en cartelera (solo función popular): número entero entre 0 y 365.

Las reglas de película, duración y entradas son las mismas que ya aplica el modelo entregado. Si algún dato no es válido:

- la función no se registra;
- el área de mensajes indica qué campo es incorrecto y por qué;
- los valores ya ingresados se mantienen para que puedan corregirse;
- la aplicación no se detiene ni muestra errores por consola.

## 5. Almacenamiento de la información

La información de las funciones debe conservarse entre una ejecución y otra, en formato JSON, en la carpeta raíz del proyecto:

- `funciones_regulares.json`: contiene las funciones regulares registradas.
- `funciones_populares.json`: contiene las funciones populares registradas.

El comportamiento esperado es el siguiente:

- Al iniciar, la aplicación carga automáticamente la información de ambos archivos y la muestra en la tabla.
- Si los archivos no existen (por ejemplo, la primera vez que se ejecuta), la aplicación inicia con la tabla vacía y sin mostrar errores.
- Cada vez que se registra o se elimina una función, los archivos se actualizan automáticamente, sin que el usuario deba presionar un botón para guardar.
- Lo que muestra la tabla y lo guardado en los archivos debe coincidir en todo momento.
- Si no se puede leer o guardar un archivo, el área de mensajes lo informa en la misma ventana. Si falla un guardado, no se informa éxito y el cambio no se aplica, de modo que la tabla y los archivos siguen coincidiendo.

## 6. Datos de prueba y resultado esperado

Registra desde la aplicación las siguientes funciones:

| Tipo | Película | Duración (min) | Entradas | Datos específicos | Precio entrada |
|---|---|---|---|---|---|
| Función regular | El Faro Dormido | 120 | 40 | Sala: Sala 1 \| 3D: sí | $7.800 |
| Función regular | Ciudad de Cristal | 95 | 25 | Sala: Sala 3 \| 3D: no | $6.500 |
| Función popular | Verano Eterno | 105 | 60 | Distribuidora: Andes Films \| Días en cartelera: 45 \| Entradas reembolsadas: sí | $3.600 |
| Función popular | La Última Estación | 130 | 30 | Distribuidora: Pampa Cine \| Días en cartelera: 10 \| Entradas reembolsadas: no | $4.800 |

Luego de registrarlas, la ventana debe mostrar las cuatro funciones con sus precios. Si se presiona Eliminar sin seleccionar una fila, el aviso aparece en la misma ventana.

Si un dato del formulario no es válido (por ejemplo, una duración `abc` o unas entradas `0`), el área de mensajes indica el campo con problemas y se mantienen los valores ingresados.

**Verificación de almacenamiento**

- Cierra la aplicación y vuelve a abrirla: la tabla debe mostrar las mismas cuatro funciones, y la función popular Verano Eterno debe seguir con las entradas reembolsadas (se comprueba en `funciones_populares.json`).
- Elimina Ciudad de Cristal, cierra la aplicación y vuelve a abrirla: Ciudad de Cristal ya no debe aparecer.
