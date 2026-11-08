# Resultado de la prueba (reintento 2026-11-08, carpeta ya autorizada)

| Paso | Resultado | Detalle |
|---|---|---|
| 0 Carpeta montada | OK | connectedFolders incluye Semestre02_2026; device_bash ve `Semestre02_2026` en $HOME/mnt |
| 1 Lectura en el PC | OK | 9 carpetas ej0* (ej01..ej09); README, RUBRICA, ENUNCIADO.md, solucion/pom.xml y base/pom.xml existen |
| 2 Escritura en el PC | OK | _prueba/PRUEBA.txt creado y leído |
| 3 Borrado | FALLO (esperado) | rm: Operation not permitted; no se pidió permiso. PRUEBA.txt sigue ahí |
| 4 Contenedor | OK | java OpenJDK 21.0.12.1; Maven 3.9.11; xvfb-run en /usr/bin/xvfb-run |
| 5a Copia solucion | OK | cksum base64 igual en PC y contenedor (2490778400, 10852 bytes) |
| 5b Compilar/abrir | OK | mvn -q compile: 1 intento, 8 s, sin 429; javafx:run con xvfb salió con código 124 = la ventana abrió |
| 6 Retorno al PC | OK | DESDE_CONTENEDOR.txt escrito con device_commit_files y leído con coincidencia |

LISTO PARA LA TAREA REAL: SÍ

Reintento: paso 0 OK de nuevo; todos los pasos repetidos con el mismo resultado (compile rc=0 reutilizando la copia del contenedor, javafx:run rc=124, cksum PC 2490778400 igual).
