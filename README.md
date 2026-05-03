# SPIN36

SPIN36 es una aplicación Android desarrollada en **Kotlin** con **Jetpack Compose** que simula una experiencia de ruleta de casino.
El usuario puede iniciar sesión con su nombre, acceder a un menú principal, realizar apuestas en la ruleta y consultar el historial de sesiones jugadas.

---

## Características principales

- Pantalla de bienvenida con entrada del nombre del jugador
- Pantalla de menú con acceso al juego e historial
- Pantalla de juego con diferentes tipos de apuesta:
  - Pleno
  - Docena
  - Rojo / Negro
  - Par / Impar
- Introducción de cantidad de monedas a apostar
- Simulación de giro de ruleta con animación y sonido
- Cálculo de premio, racha y bonus
- Persistencia de jugadores, partidas y sesiones con Room
- Pantalla de historial con sesiones guardadas
- Música de fondo con soporte para canción personalizada
- Captura de imagen de la victoria guardada en el dispositivo
- Victorias guardadas en el calendario del dispositivo
- Notificaciones de victoria
- Pantalla de ayuda con WebView
- Ubicación GPS almacenada en la base de datos por partida
- Multiidioma: Español, Inglés, Catalán y Euskera
- Sonido en todos los botones de la app
- Barra superior con menú de navegación:
  - Menú
  - Juego / Apuesta
  - Historial
  - Ajustes
  - Ayuda
  - Salir

---

## Tecnologías utilizadas

- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **ViewModel + StateFlow**
- **Navigation Compose**
- **Room + RxJava 3** — persistencia reactiva con SQLite
- **MediaPlayer** — música de fondo con Audio Focus y Ducking
- **SoundPool** — efectos de sonido cortos (botones, ruleta, victoria, captura)
- **FusedLocationProviderClient** — ubicación GPS (Google Play Services)
- **CalendarContract** — inserción de eventos en el calendario del sistema
- **NotificationCompat** — notificaciones de victoria
- **WebView** — pantalla de ayuda con HTML local multiidioma
- **AppCompatDelegate** — cambio de idioma en tiempo de ejecución
- **ActivityResultContracts.CreateDocument** — selector de ubicación para guardar imágenes
- **BlurMaskFilter + Canvas** — animación de estrella en la ruleta
- **rememberInfiniteTransition** — animaciones continuas en Compose

---

## Arquitectura

El proyecto sigue una arquitectura **feature-based MVVM** con separación estricta de responsabilidades:

- **Screen**: conecta la UI con el ViewModel
- **UiState**: representa el estado inmutable de la pantalla
- **ViewModel**: contiene la lógica de negocio y manipulación del estado

### Estructura por feature

- `BienvenidaScreen / BienvenidaViewModel / BienvenidaUiState`
- `JuegoScreen / JuegoViewModel / JuegoUiState`
- `HistorialScreen / HistorialViewModel / HistorialUiState`
- `AjustesScreen / AjustesViewModel / AjustesUiState`
- `AyudaScreen`

---

## Flujo de navegación

1. **Bienvenida** — el usuario introduce su nombre y accede al menú
2. **Menú** — acceso al juego, historial, ajustes y ayuda
3. **Juego** — selección de apuesta, giro de ruleta, resultado con animación
4. **Historial** — lista de sesiones guardadas con saldo final y racha máxima
5. **Ajustes** — control de música, volumen y cambio de idioma
6. **Ayuda** — WebView con guía del juego en el idioma activo

---

## Animación de la ruleta

Al pulsar GIRAR aparece un overlay oscuro al 92% que cubre la pantalla. Contiene dos fases:

**Fase 1 — Números rodando:** bucle con intervalos crecientes (70ms → 400ms) que simulan el frenado. Sonido de tick sincronizado en cada cambio.

**Fase 2 — Número final:** si la jugada fue ganadora, aparece una estrella de 8 puntas dibujada con `Canvas` y trigonometría, con `BlurMaskFilter` para el difuminado dorado y dos animaciones infinitas: rotación continua y pulso de escala. El número final se muestra en dorado. Si perdió, sin estrella y número en negro.

El overlay no se puede cerrar hasta que termina la animación. Al ganar, `win_sound.mp3` suena en bucle hasta que el usuario cierra el overlay.

---

## Música de fondo

Gestionada por `MusicaManager.kt` con `MediaPlayer`. Incluye:

- Melodía oficial del juego (`musica_oficial.mp3`)
- Soporte para canción personalizada del dispositivo vía URI
- Control de volumen en tiempo real
- **Audio Focus**: pausa automática al recibir llamada u otro audio prioritario
- **Ducking**: baja el volumen a 0.2 al recibir notificaciones en lugar de parar
- Pausa/reanudación automática al ir al background y volver

---

## Captura de imagen de la victoria

Al ganar, el usuario puede guardar una imagen de la victoria mediante `ActivityResultContracts.CreateDocument`, que abre el selector de archivos nativo del sistema para elegir la ubicación. El bitmap se genera con los datos de la partida traducidos al idioma activo y se guarda en formato **WebP** de alta calidad.

---

## Guardar victorias en el calendario

Gestionado desde `CalendarioManager.kt` usando el Content Provider nativo `CalendarContract`. Inserta un evento en el calendario principal del dispositivo con el nombre del jugador, tipo de apuesta, número ganador y monto ganado, en el día y hora exactos de la jugada. La operación se ejecuta en un hilo secundario con `Dispatchers.IO`.

---

## Notificaciones

`NotificacionHelper.kt` muestra una notificación al ganar indicando el tiempo que tardó el jugador en conseguir la victoria. Compatible con Android 8+ (API 26). Solicita permiso `POST_NOTIFICATIONS` en Android 13+.

---

## Ubicación GPS en la base de datos

`UbicacionManager.kt` usa `FusedLocationProviderClient` para obtener las coordenadas del jugador en el momento de cada jugada. La latitud y longitud se almacenan en `PartidaEntity` en Room.

---

## Multiidioma

La app soporta 4 idiomas seleccionables manualmente desde Ajustes o automáticamente según el idioma del dispositivo:

| Idioma | Recursos |
|---|---|
| Español (defecto) | `values/strings.xml` |
| Inglés | `values-en/strings.xml` |
| Catalán | `values-ca/strings.xml` |
| Euskera | `values-eu/strings.xml` |

La pantalla de ayuda (WebView) también tiene su HTML traducido en los 4 idiomas. El cambio de idioma usa `AppCompatDelegate.setApplicationLocales()`.

---

## Sonido en botones

Todos los botones de la app reproducen `button_click.mp3` mediante el helper composable `rememberSoundClick(onClick)`, que envuelve cualquier `onClick` y reproduce el sonido antes de ejecutarlo.

---

## Tipos de apuesta

| Tipo | Multiplicador | Descripción |
|---|---|---|
| Pleno | x36 | Número exacto (0–36) |
| Docena | x3 | Primera (1–12), Segunda (13–24), Tercera (25–36) |
| Color | x2 | Rojo o Negro (el 0 no cuenta) |
| Par / Impar | x2 | El 0 no cuenta |

**Bonus de racha:** al encadenar 5 victorias seguidas se otorgan +100 monedas y la racha se reinicia.

---

## Estructura general del proyecto

```text
com.example.spin36
│
├── app
│   ├── MainActivity.kt
│   └── AppNavHost.kt
│
├── feature
│   ├── bienvenida
│   ├── menu
│   ├── juego
│   ├── historial
│   ├── ajustes
│   ├── ayuda
│   ├── galeria       ← GaleriaManager.kt
│   ├── calendario    ← CalendarioManager.kt
│   ├── ubicacion     ← UbicacionManager.kt
│   ├── musica        ← MusicaManager.kt
│   ├── notificacion  ← NotificacionHelper.kt
│   └── components    ← Spin36TopBar, SoundClickHelper
│
├── data
│   ├── database      ← Room (entities, DAO, database)
│   ├── repository    ← CasinoRepository
│   ├── model         ← Jugador, Apuesta, Sesion
│   ├── mapper
│   └── preferences   ← AjustesPreferences
│
├── assets
│   ├── ayuda.html
│   ├── ayuda_en.html
│   ├── ayuda_ca.html
│   └── ayuda_eu.html
│
└── ui
    └── theme         ← Color, Theme, Type
```

---

## Recursos de audio

| Archivo | Uso |
|---|---|
| `musica_oficial.mp3` | Música de fondo oficial |
| `button_click.mp3` | Sonido de botones |
| `tick_ruleta.mp3` | Tick durante la animación de la ruleta |
| `win_sound.mp3` | Sonido de victoria en bucle |
| `captura_sound.mp3` | Sonido al guardar la imagen de victoria |

---

## Permisos requeridos

| Permiso | Uso |
|---|---|
| `READ_MEDIA_AUDIO` | Selección de música personalizada |
| `READ_EXTERNAL_STORAGE` (≤ API 32) | Selección de música en Android antiguo |
| `ACCESS_FINE_LOCATION` | Ubicación GPS de alta precisión |
| `ACCESS_COARSE_LOCATION` | Ubicación GPS aproximada |
| `READ_CALENDAR` | Lectura del calendario del dispositivo |
| `WRITE_CALENDAR` | Escritura de eventos en el calendario |
| `POST_NOTIFICATIONS` (API 33+) | Notificaciones de victoria |
