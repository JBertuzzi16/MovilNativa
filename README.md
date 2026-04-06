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
  - Rojo
  - Negro
  - Par
  - Impar
- Introducción de cantidad de monedas a apostar
- Simulación de giro de ruleta
- Cálculo de premio, racha y bonus
- Persistencia de jugadores, partidas y sesiones
- Pantalla de historial con sesiones guardadas
- Barra superior con menú de navegación:
  - Menú
  - Historial
  - Ajustes
  - Salir

---

## Tecnologías utilizadas

- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **ViewModel**
- **StateFlow**
- **Navigation Compose**
- **RxJava**
- Persistencia con repositorio local

---

## Arquitectura

El proyecto sigue una estructura basada en separación de responsabilidades entre:

- **Screen**: conecta la UI con el ViewModel
- **UiState**: representa el estado de la pantalla
- **ViewModel**: contiene la lógica de negocio y manipulación del estado

### Ejemplo de estructura por feature

- `BienvenidaScreen / BienvenidaViewModel / BienvenidaUiState`
- `JuegoScreen / JuegoViewModel / JuegoUiState`
- `HistorialScreen / HistorialViewModel / HistorialUiState`

Esto permite una arquitectura más limpia, mantenible y fácil de escalar.

---

## Flujo de navegación

La aplicación contiene las siguientes pantallas:

1. **Bienvenida**
   - El usuario introduce su nombre
   - Se navega al menú principal

2. **Menú**
   - Muestra el nombre del jugador
   - Permite acceder al juego
   - Permite acceder al historial
   - Permite volver atrás

3. **Juego**
   - Se selecciona el tipo de apuesta
   - Se introduce la cantidad de monedas
   - Se ejecuta el giro
   - Se muestra el resultado
   - Desde la barra superior se puede navegar a:
     - Menú
     - Historial
     - Ajustes
     - Salir

4. **Historial**
   - Lista de sesiones guardadas
   - Botón para volver atrás

---

## Tipos de apuesta implementados

### 1. Pleno
Permite apostar a un número exacto entre `0` y `36`.

### 2. Docena
Permite apostar a una de las tres docenas:
- 1
- 2
- 3

### 3. Color
Permite apostar a:
- rojo
- negro

### 4. Paridad
Permite apostar a:
- par
- impar

---

## Estado del juego

El estado de la pantalla de juego se representa con `JuegoUiState`:

- nombre del jugador
- saldo actual
- racha actual
- tipo de apuesta
- valor de apuesta
- cantidad apostada
- resultado de la ruleta
- ganancia obtenida
- bonus por racha
- mensaje de resultado
- estado de carga
- errores

---

## Lógica principal del juego

La lógica de juego se encuentra en `JuegoViewModel` y se encarga de:

- cargar o crear el jugador
- iniciar sesión de juego
- validar la apuesta
- ejecutar el giro de ruleta
- calcular premio y bonus
- actualizar saldo y racha
- guardar partida y sesión
- cerrar la sesión actual al salir

---

## Estructura general del proyecto

```text
com.example.spin36
│
├── app
│   └── AppNavHost.kt
│
├── feature
│   ├── bienvenida
│   │   ├── BienvenidaScreen.kt
│   │   ├── BienvenidaViewModel.kt
│   │   └── BienvenidaUiState.kt
│   │
│   ├── menu
│   │   └── MenuScreen.kt
│   │
│   ├── juego
│   │   ├── JuegoScreen.kt
│   │   ├── JuegoViewModel.kt
│   │   └── JuegoUiState.kt
│   │
│   └── historial
│       ├── HistorialScreen.kt
│       ├── HistorialViewModel.kt
│       └── HistorialUiState.kt
│
├── data
│   ├── repository
│   ├── local
│   └── model
│
└── ui
    └── theme