# Calculadora

Una calculadora sencilla para Android escrita en Java. Suma, resta, multiplica y divide números con decimales.

## Teclado

```
 C   CA   ÷   ×
 1   2    3   -
 4   5    6   +
 7   8    9   =
   0      .   =
```

| Botón | Qué hace |
|---|---|
| `0`–`9` | Escribe un dígito. Hasta 12 dígitos por número. |
| `.` | Añade el punto decimal. Si empiezas un número nuevo, escribe `0.`; un segundo punto en el mismo número se ignora. |
| `+` `-` `×` `÷` | Elige una operación. Si ya había una pendiente, se resuelve primero (`5 + 3 ×` muestra `8`). |
| `=` | Muestra el resultado de la operación pendiente. |
| `C` | Borra el último carácter escrito. No hace nada sobre un resultado. |
| `CA` | Lo borra todo y vuelve a `0`. |

## Funcionamiento

- **De izquierda a derecha:** las operaciones se hacen en el orden en que se escriben, como en una calculadora de bolsillo básica. `2 + 3 × 4 =` da `20`, no `14`.
- **Decimales exactos:** los números se guardan como `BigDecimal` en lugar de `double`, así que `0.1 + 0.2` da `0.3` y no `0.30000000000000004`.
- **Redondeo:** los resultados se redondean a 12 cifras significativas y se quitan los ceros sobrantes (`1 ÷ 3` muestra `0.333333333333`). Los resultados demasiado largos para la pantalla se muestran en notación científica, por ejemplo `1.23E+15`.
- **División entre cero:** muestra `Error`. Escribe un número nuevo o pulsa `CA` para seguir.
- **Pantalla:** el texto se hace más pequeño automáticamente para que los números largos quepan.

## Cómo funciona el código

Toda la lógica está en [`MainActivity.java`](app/src/main/java/com/example/calculadora/MainActivity.java). Los botones no tienen ID: en `onCreate` se asigna un mismo listener a todos los hijos de la cuadrícula de botones, y este pasa el texto del botón a `pulsar(texto)`.

La calculadora guarda tres datos de estado:

| Campo | Significado |
|---|---|
| `acumulado` | El primer operando, que se guarda al pulsar una operación. |
| `operacion` | La operación pendiente (`+`, `-`, `×`, `÷`) o `null`. |
| `nuevoNumero` | `true` cuando el siguiente dígito debe sustituir lo que hay en pantalla en vez de añadirse (después de una operación, `=`, `CA` o un error). |

El número que se está escribiendo se lee directamente de la pantalla (`tvPantalla`).

- `pulsar()` decide qué hacer con cada botón.
- `calcular()` aplica una operación y devuelve `null` si es una división entre cero.
- `formatear()` redondea el resultado y lo convierte en el texto que se muestra.
- `mostrarError()` muestra `Error` y reinicia el estado.

El diseño está en [`activity_main.xml`](app/src/main/res/layout/activity_main.xml): un `TextView` para la pantalla encima de un `GridLayout` de 4×5 botones. El `0` ocupa dos columnas y el `=` ocupa dos filas.

Para añadir una operación nueva, añade un botón a la cuadrícula y un `case` en `pulsar()` y en `calcular()`.

## Compilar

Abre el proyecto en Android Studio y ejecútalo, o compílalo desde la terminal:

```
gradlew assembleDebug
```

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`. Necesita Android 7.0 (API 24) o superior.
