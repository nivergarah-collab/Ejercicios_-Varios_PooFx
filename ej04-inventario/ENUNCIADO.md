# Enunciado · Bodega Pintamar

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

La ferretería Pintamar ya cuenta con el modelo de clases desarrollado en una etapa anterior: el sistema distingue pinturas y herramientas guardadas en la bodega, calcula el precio de venta de cada artículo según su tipo y permite marcar las herramientas que salieron en préstamo. Sin embargo, ese sistema solo funciona por consola y la información se pierde cada vez que el programa se cierra.

La administración de la ferretería ha solicitado transformarlo en una aplicación de escritorio con ventanas, que permita al personal de bodega registrar y eliminar artículos, y que conserve la información en archivos para que esté disponible la próxima vez que se abra la aplicación.

### Proyecto base

El proyecto base es un proyecto Maven con la configuración inicial, la clase de arranque y el modelo ya implementado y probado, correspondiente al diagrama siguiente. Tu trabajo consiste en construir sobre ese modelo la interfaz gráfica y el almacenamiento en archivos. No debes reescribir ni reemplazar las clases entregadas.

```mermaid
classDiagram
    class Articulo {
        <<abstract>>
        -String nombre
        -int stock
        -int anaquel
        +calcularPrecioVenta()* int
        +getTipo()* String
    }
    class Prestable {
        <<interface>>
        +estaPrestada() boolean
        +marcarPrestada() void
    }
    class Pintura {
        -int litrosEnvase
        -boolean antioxido
    }
    class Herramienta {
        -String marca
        -int mesesEnBodega
        -boolean prestada
    }
    Articulo <|-- Pintura
    Articulo <|-- Herramienta
    Prestable <|.. Herramienta
```

El modelo entregado ya aplica las siguientes reglas:

- Pintura: el precio de venta del envase es de $3.200 por cada litro que contiene. Si la pintura es anti-óxido, el precio se incrementa en un 15%.
- Herramienta: precio de venta base de $22.000. Si lleva más de 12 meses guardada en la bodega, el precio se reduce en un 25%.

Datos comunes: el nombre no puede ser nulo ni vacío, el stock debe estar entre 1 y 500 unidades y el anaquel (estante de la bodega) debe estar entre 1 y 40. Si un dato no cumple, el modelo lo rechaza con un mensaje que explica el motivo.

## 2. Estructura del proyecto

La solución debe separar responsabilidades según el patrón Modelo-Vista-Controlador, con una capa de acceso a datos basada en el patrón DAO/Repository. No hay una única forma de resolverlo: lo importante es que se cumplan los siguientes puntos.

- **Configuración del proyecto:** el proyecto base ya incluye las dependencias de JavaFX y la clase de arranque de la aplicación. Debes agregar al archivo `pom.xml` la dependencia de la librería Gson, necesaria para trabajar con archivos JSON, sin alterar lo que ya existe.
- **Vista:** la ventana se declara en un archivo FXML (por ejemplo, diseñado con Scene Builder) y se carga desde la clase de arranque. No se acepta una interfaz construida solo por código.
- **Vista y controlador:** el FXML indica su clase controladora, y cada componente que usa el controlador lleva `fx:id` en la vista y un atributo con el mismo nombre y tipo en el controlador, con la anotación `@FXML`.
- **Paquetes:** las clases se separan en paquetes distintos para controlador, modelo y persistencia, y el FXML va en los recursos. El modelo y la persistencia no dependen del controlador ni de clases de JavaFX. Las clases nuevas no van en el paquete raíz ni dentro del paquete del modelo.
- **Controlador:** valida las entradas y coordina la interfaz. Los cálculos y reglas del negocio (como el precio de venta) los entrega el modelo, y las operaciones con los datos se piden al Repository. No lee ni escribe archivos directamente.
- **Persistencia (DAO/Repository):** una interfaz DAO con las operaciones de lectura y escritura; una implementación del DAO con Gson que maneje los archivos; y una clase Repository que mantenga los artículos en memoria, ofrezca las operaciones que usa el controlador y delegue en el DAO la lectura y escritura de archivos.

## 3. Ventana de la aplicación

Al ejecutar la aplicación se abre una única ventana, desde la cual se realizan todas las acciones. La ventana debe contener:

- Un formulario de registro con los campos descritos más abajo y un botón Registrar.
- Una tabla con los artículos registrados y las columnas Tipo, Nombre, Stock, Anaquel y Precio de venta. El precio que se muestra es el que calcula el modelo para cada artículo.
  La tabla usa como fuente de datos una única lista observable, que se mantiene durante toda la ejecución y se actualiza al iniciar y tras cada registro o eliminación (no se crea una lista nueva cada vez). Debe mostrar pinturas y herramientas sin duplicar ninguna.
- Un botón Eliminar, que quita del registro el artículo seleccionado en la tabla. Si no hay una fila seleccionada, la aplicación informa que se debe seleccionar un artículo y no realiza ningún cambio.
- Un área de mensajes dentro de la misma ventana, donde se informa el resultado de cada acción: registro exitoso, dato no válido, eliminación, falta de selección o error al leer o guardar los archivos. Cada mensaje identifica el artículo o la causa, y reemplaza al anterior. Los mensajes no se muestran por consola.

**Campos del formulario**

- Un selector de tipo con las opciones Pintura y Herramienta.
- Datos comunes: nombre, stock y anaquel.
- Datos de la pintura: litros del envase y si es anti-óxido (sí/no).
- Datos de la herramienta: marca, meses en bodega y si está en préstamo (sí/no).

Al cambiar el tipo en el selector, solo quedan habilitados los datos específicos del tipo elegido; los datos del otro tipo quedan deshabilitados y no se consideran al registrar.

Al presionar Registrar, la aplicación valida los datos según la sección 4. Si son correctos, registra el artículo, la tabla lo muestra de inmediato, el área de mensajes informa la confirmación (por ejemplo: “Esmalte Sintético Blanco (Pintura) registrado correctamente.”) y el formulario queda vacío para un nuevo registro.

## 4. Validaciones del formulario

Antes de registrar, el formulario debe verificar los datos ingresados según las siguientes reglas:

- Tipo: debe estar seleccionado.
- Nombre: no puede estar vacío ni contener solo espacios.
- Stock: número entero entre 1 y 500.
- Anaquel: número entero entre 1 y 40.
- Litros del envase (solo pintura): número entero entre 1 y 20.
- Marca (solo herramienta): no puede estar vacía ni contener solo espacios.
- Meses en bodega (solo herramienta): número entero entre 0 y 60.

Las reglas de nombre, stock y anaquel son las mismas que ya aplica el modelo entregado. Si algún dato no es válido:

- el artículo no se registra;
- el área de mensajes indica qué campo es incorrecto y por qué;
- los valores ya ingresados se mantienen para que puedan corregirse;
- la aplicación no se detiene ni muestra errores por consola.

## 5. Almacenamiento de la información

La información de los artículos debe conservarse entre una ejecución y otra, en formato JSON, en la carpeta raíz del proyecto:

- `pinturas.json`: contiene las pinturas registradas.
- `herramientas.json`: contiene las herramientas registradas.

El comportamiento esperado es el siguiente:

- Al iniciar, la aplicación carga automáticamente la información de ambos archivos y la muestra en la tabla.
- Si los archivos no existen (por ejemplo, la primera vez que se ejecuta), la aplicación inicia con la tabla vacía y sin mostrar errores.
- Cada vez que se registra o se elimina un artículo, los archivos se actualizan automáticamente, sin que el usuario deba presionar un botón para guardar.
- Lo que muestra la tabla y lo guardado en los archivos debe coincidir en todo momento.
- Si no se puede leer o guardar un archivo, el área de mensajes lo informa en la misma ventana. Si falla un guardado, no se informa éxito y el cambio no se aplica, de modo que la tabla y los archivos siguen coincidiendo.

## 6. Datos de prueba y resultado esperado

Registra desde la aplicación los siguientes artículos:

| Tipo | Nombre | Stock | Anaquel | Datos específicos | Precio de venta |
|---|---|---|---|---|---|
| Pintura | Esmalte Sintético Blanco | 40 | 3 | Litros: 4 \| Anti-óxido: sí | $14.720 |
| Pintura | Látex Interior | 25 | 5 | Litros: 10 \| Anti-óxido: no | $32.000 |
| Herramienta | Taladro Percutor | 6 | 12 | Marca: Vulcano \| Meses en bodega: 3 \| En préstamo: no | $22.000 |
| Herramienta | Sierra Circular | 2 | 14 | Marca: Tigre \| Meses en bodega: 18 \| En préstamo: sí | $16.500 |

Luego de registrarlos, la ventana debe mostrar los cuatro artículos con sus precios. Si se presiona Eliminar sin seleccionar una fila, el aviso aparece en la misma ventana.

Si un dato del formulario no es válido (por ejemplo, un stock `abc` o un anaquel `0`), el área de mensajes indica el campo con problemas y se mantienen los valores ingresados.

**Verificación de almacenamiento**

- Cierra la aplicación y vuelve a abrirla: la tabla debe mostrar los mismos cuatro artículos, y la Sierra Circular debe seguir en préstamo (se comprueba en `herramientas.json`).
- Elimina el Látex Interior, cierra la aplicación y vuelve a abrirla: el Látex Interior ya no debe aparecer.
