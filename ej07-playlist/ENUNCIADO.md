# Enunciado · Playlist Ritmo Sur

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

La radio en línea Ritmo Sur ya cuenta con el modelo de clases desarrollado en una etapa anterior: el sistema distingue canciones y podcasts, calcula el costo de la licencia de cada pista según su tipo y duración, y permite marcar los podcasts que ya fueron sincronizados con el dispositivo para escucharlos sin conexión. Sin embargo, ese sistema solo funciona por consola y la información se pierde cada vez que el programa se cierra.

El equipo de programación de la radio ha solicitado transformarlo en una aplicación de escritorio con ventanas, que permita registrar y eliminar pistas de la playlist, y que conserve la información en archivos para que esté disponible la próxima vez que se abra la aplicación.

### Proyecto base

El proyecto base es un proyecto Maven con la configuración inicial, la clase de arranque y el modelo ya implementado y probado, correspondiente al diagrama siguiente. Tu trabajo consiste en construir sobre ese modelo la interfaz gráfica y el almacenamiento en archivos. No debes reescribir ni reemplazar las clases entregadas.

```mermaid
classDiagram
    class Pista {
        <<abstract>>
        -String titulo
        -String artista
        -int duracionMin
        +calcularCostoLicencia()* int
        +getTipo()* String
    }
    class Sincronizable {
        <<interface>>
        +estaSincronizado() boolean
        +marcarSincronizado() void
    }
    class Cancion {
        -String album
        -boolean estreno
    }
    class Podcast {
        -int episodio
        -boolean sincronizado
    }
    Pista <|-- Cancion
    Pista <|-- Podcast
    Sincronizable <|.. Podcast
```

El modelo entregado ya aplica las siguientes reglas:

- Canción: licencia de $700 por minuto de duración. Si la canción es un estreno, la licencia se incrementa en un 30%.
- Podcast: licencia de $400 por minuto de duración. Si el episodio es uno de los primeros 10 (ciclo inicial), la licencia se reduce en un 25%.

Datos comunes: el título y el artista no pueden ser nulos ni vacíos, y la duración debe estar entre 1 y 240 minutos. Si un dato no cumple, el modelo lo rechaza con un mensaje que explica el motivo.

## 2. Estructura del proyecto

La solución debe separar responsabilidades según el patrón Modelo-Vista-Controlador, con una capa de acceso a datos basada en el patrón DAO/Repository. No hay una única forma de resolverlo: lo importante es que se cumplan los siguientes puntos.

- **Configuración del proyecto:** el proyecto base ya incluye las dependencias de JavaFX y la clase de arranque de la aplicación. Debes agregar al archivo `pom.xml` la dependencia de la librería Gson, necesaria para trabajar con archivos JSON, sin alterar lo que ya existe.
- **Vista:** la ventana se declara en un archivo FXML (por ejemplo, diseñado con Scene Builder) y se carga desde la clase de arranque. No se acepta una interfaz construida solo por código.
- **Vista y controlador:** el FXML indica su clase controladora, y cada componente que usa el controlador lleva `fx:id` en la vista y un atributo con el mismo nombre y tipo en el controlador, con la anotación `@FXML`.
- **Paquetes:** las clases se separan en paquetes distintos para controlador, modelo y persistencia, y el FXML va en los recursos. El modelo y la persistencia no dependen del controlador ni de clases de JavaFX. Las clases nuevas no van en el paquete raíz ni dentro del paquete del modelo.
- **Controlador:** valida las entradas y coordina la interfaz. Los cálculos y reglas del negocio (como el costo de la licencia) los entrega el modelo, y las operaciones con los datos se piden al Repository. No lee ni escribe archivos directamente.
- **Persistencia (DAO/Repository):** una interfaz DAO con las operaciones de lectura y escritura; una implementación del DAO con Gson que maneje los archivos; y una clase Repository que mantenga las pistas en memoria, ofrezca las operaciones que usa el controlador y delegue en el DAO la lectura y escritura de archivos.

## 3. Ventana de la aplicación

Al ejecutar la aplicación se abre una única ventana, desde la cual se realizan todas las acciones. La ventana debe contener:

- Un formulario de registro con los campos descritos más abajo y un botón Registrar.
- Una tabla con las pistas registradas y las columnas Tipo, Título, Artista, Duración (min) y Licencia. La licencia que se muestra es la que calcula el modelo para cada pista.
  La tabla usa como fuente de datos una única lista observable, que se mantiene durante toda la ejecución y se actualiza al iniciar y tras cada registro o eliminación (no se crea una lista nueva cada vez). Debe mostrar canciones y podcasts sin duplicar ninguna.
- Un botón Eliminar, que quita de la playlist la pista seleccionada en la tabla. Si no hay una fila seleccionada, la aplicación informa que se debe seleccionar una pista y no realiza ningún cambio.
- Un área de mensajes dentro de la misma ventana, donde se informa el resultado de cada acción: registro exitoso, dato no válido, eliminación, falta de selección o error al leer o guardar los archivos. Cada mensaje identifica la pista o la causa, y reemplaza al anterior. Los mensajes no se muestran por consola.

**Campos del formulario**

- Un selector de tipo con las opciones Canción y Podcast.
- Datos comunes: título, artista y duración (minutos).
- Datos de la canción: álbum y si es un estreno (sí/no).
- Datos del podcast: número de episodio y si ya está sincronizado (sí/no).

Al cambiar el tipo en el selector, solo quedan habilitados los datos específicos del tipo elegido; los datos del otro tipo quedan deshabilitados y no se consideran al registrar.

Al presionar Registrar, la aplicación valida los datos según la sección 4. Si son correctos, registra la pista, la tabla la muestra de inmediato, el área de mensajes informa la confirmación (por ejemplo: “Marea Baja (Canción) registrada correctamente.”) y el formulario queda vacío para un nuevo registro.

## 4. Validaciones del formulario

Antes de registrar, el formulario debe verificar los datos ingresados según las siguientes reglas:

- Tipo: debe estar seleccionado.
- Título: no puede estar vacío ni contener solo espacios.
- Artista: no puede estar vacío ni contener solo espacios.
- Duración: número entero entre 1 y 240 minutos.
- Álbum (solo canción): no puede estar vacío ni contener solo espacios.
- Número de episodio (solo podcast): número entero entre 1 y 500.

Las reglas de título, artista y duración son las mismas que ya aplica el modelo entregado. Si algún dato no es válido:

- la pista no se registra;
- el área de mensajes indica qué campo es incorrecto y por qué;
- los valores ya ingresados se mantienen para que puedan corregirse;
- la aplicación no se detiene ni muestra errores por consola.

## 5. Almacenamiento de la información

La información de las pistas debe conservarse entre una ejecución y otra, en formato JSON, en la carpeta raíz del proyecto:

- `canciones.json`: contiene las canciones registradas.
- `podcasts.json`: contiene los podcasts registrados.

El comportamiento esperado es el siguiente:

- Al iniciar, la aplicación carga automáticamente la información de ambos archivos y la muestra en la tabla.
- Si los archivos no existen (por ejemplo, la primera vez que se ejecuta), la aplicación inicia con la tabla vacía y sin mostrar errores.
- Cada vez que se registra o se elimina una pista, los archivos se actualizan automáticamente, sin que el usuario deba presionar un botón para guardar.
- Lo que muestra la tabla y lo guardado en los archivos debe coincidir en todo momento.
- Si no se puede leer o guardar un archivo, el área de mensajes lo informa en la misma ventana. Si falla un guardado, no se informa éxito y el cambio no se aplica, de modo que la tabla y los archivos siguen coincidiendo.

## 6. Datos de prueba y resultado esperado

Registra desde la aplicación las siguientes pistas:

| Tipo | Título | Artista | Duración (min) | Datos específicos | Licencia |
|---|---|---|---|---|---|
| Canción | Rutas del Sur | Los Andinos | 4 | Álbum: Camino Largo \| Estreno: sí | $3.640 |
| Canción | Marea Baja | Luz Carrasco | 3 | Álbum: Orilla \| Estreno: no | $2.100 |
| Podcast | Código y Café | Radio Campus | 45 | Episodio: 8 \| Sincronizado: sí | $13.500 |
| Podcast | Historias del Puerto | Voces del Sur | 30 | Episodio: 25 \| Sincronizado: no | $12.000 |

Luego de registrarlas, la ventana debe mostrar las cuatro pistas con sus licencias. Si se presiona Eliminar sin seleccionar una fila, el aviso aparece en la misma ventana.

Si un dato del formulario no es válido (por ejemplo, una duración `abc` o una duración `0`), el área de mensajes indica el campo con problemas y se mantienen los valores ingresados.

**Verificación de almacenamiento**

- Cierra la aplicación y vuelve a abrirla: la tabla debe mostrar las mismas cuatro pistas, y el podcast Código y Café debe seguir marcado como sincronizado (se comprueba en `podcasts.json`).
- Elimina Marea Baja, cierra la aplicación y vuelve a abrirla: Marea Baja ya no debe aparecer.
