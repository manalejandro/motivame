# Motívame - Tu Compañero de Motivación Diaria

## 📱 Descripción

**Motívame** es una aplicación Android moderna diseñada para ayudarte a mantener la motivación en tus tareas pendientes. La app te permite configurar recordatorios diarios personalizados con tus metas específicas, ayudándote a visualizar el "por qué" detrás de cada tarea.

## ✨ Características Principales

- **📝 Gestión de Tareas**: Crea, edita y elimina tareas pendientes fácilmente
- **🎯 Definición de Metas**: Asocia múltiples objetivos a cada tarea para recordar por qué es importante
- **🔔 Notificaciones Diarias**: Recibe recordatorios automáticos todos los días a las 9:00 AM
- **🔊 Alertas Personalizables**: Configura sonido y vibración según tus preferencias
- **⏯️ Control de Tareas**: Activa o pausa tareas según tu conveniencia
- **🎨 Diseño Moderno**: Interfaz Material Design 3 con colores vibrantes y motivadores
- **📊 Tareas Predeterminadas**: Comienza con ejemplos inspiradores o crea las tuyas propias

## 🚀 Funcionalidades Técnicas

### Arquitectura
- **MVVM (Model-View-ViewModel)**: Separación clara de responsabilidades
- **Jetpack Compose**: UI moderna y declarativa
- **WorkManager**: Tareas programadas en segundo plano confiables
- **DataStore**: Persistencia de datos ligera y eficiente
- **Kotlin Coroutines**: Programación asíncrona fluida

### Componentes Principales

#### 1. Pantalla Principal
- Lista de tareas activas y pausadas
- Tarjetas visuales con gradientes
- Indicadores de estado (activo/pausado)
- Navegación rápida a configuración y agregar tareas

#### 2. Agregar Tareas
- Campo de título de tarea
- Agregar múltiples metas personalizadas
- Validación de campos
- Interfaz intuitiva con iconos descriptivos

#### 3. Configuración
- Activar/desactivar notificaciones
- Control de sonido
- Probar notificaciones en tiempo real
- Solicitud de permisos en Android 13+

### Sistema de Notificaciones

La aplicación utiliza un sistema de notificaciones inteligente:

- **Canal de Alta Prioridad**: Garantiza que las notificaciones sean visibles
- **Vibración Personalizada**: Patrón distintivo para llamar la atención
- **Mensajes Motivacionales**: Cada notificación muestra una meta aleatoria de la tarea
- **Sonido Configurable**: Opción de activar/desactivar sonido de notificación

### WorkManager - Recordatorios Diarios

- Ejecuta tareas diarias a las 9:00 AM
- Persiste incluso después de reiniciar el dispositivo
- Optimizado para el consumo de batería
- No requiere conexión a Internet

## 📦 Dependencias

```kotlin
// Core Android
androidx.core:core-ktx:1.10.1
androidx.lifecycle:lifecycle-runtime-ktx:2.6.1
androidx.activity:activity-compose:1.8.0

// Compose
androidx.compose:compose-bom:2024.09.00
androidx.compose.material3:material3
androidx.compose.material:material-icons-extended:1.5.4

// Architecture Components
androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1
androidx.work:work-runtime-ktx:2.9.0
androidx.datastore:datastore-preferences:1.0.0
```

## 🔧 Requisitos

- **Android SDK 24+** (Android 7.0 Nougat o superior)
- **Target SDK 36**
- **Kotlin 2.0.21**
- **Gradle 9.0.1**

## 🎨 Diseño

### Paleta de Colores

- **Primary**: Indigo vibrante (#6366F1)
- **Secondary**: Rosa motivador (#EC4899)
- **Tertiary**: Púrpura (#8B5CF6)
- **Success**: Verde (#10B981)
- **Error**: Rojo (#EF4444)

### Tipografía
- Fuentes Material Design 3
- Énfasis en títulos grandes y legibles
- Texto secundario con contraste óptimo

## 📱 Permisos

La aplicación solicita los siguientes permisos:

- `POST_NOTIFICATIONS` (Android 13+): Para mostrar recordatorios
- `VIBRATE`: Para alertas con vibración
- `RECEIVE_BOOT_COMPLETED`: Para mantener recordatorios después de reiniciar

## 🔄 Flujo de la Aplicación

1. **Inicio**: Pantalla principal con tareas predeterminadas
2. **Agregar Tarea**: El usuario crea una nueva tarea con sus metas
3. **Configuración**: Personaliza notificaciones y sonido
4. **Recordatorios Automáticos**: WorkManager envía notificaciones diarias
5. **Interacción**: Usuario puede pausar, reanudar o eliminar tareas

## 🏗️ Estructura del Proyecto

```
app/src/main/java/com/manalejandro/motivame/
├── data/
│   ├── Task.kt                  # Modelo de datos
│   └── TaskRepository.kt        # Repositorio de persistencia
├── notifications/
│   └── NotificationHelper.kt    # Gestión de notificaciones
├── ui/
│   ├── screens/
│   │   ├── MainScreen.kt        # Pantalla principal
│   │   ├── AddTaskScreen.kt     # Pantalla agregar tarea
│   │   └── SettingsScreen.kt    # Pantalla configuración
│   ├── theme/
│   │   ├── Color.kt             # Definición de colores
│   │   ├── Theme.kt             # Tema de la aplicación
│   │   └── Type.kt              # Tipografía
│   └── viewmodel/
│       └── TaskViewModel.kt     # ViewModel principal
├── worker/
│   └── DailyReminderWorker.kt   # Worker para recordatorios
└── MainActivity.kt              # Actividad principal
```

## 🚀 Compilación

```bash
# Compilar versión de depuración
./gradlew assembleDebug

# Compilar versión de lanzamiento
./gradlew assembleRelease

# Ejecutar tests
./gradlew test

# Compilar e instalar
./gradlew installDebug
```

## 💡 Casos de Uso

1. **Estudiante**: Recordatorios para estudiar materias específicas con metas académicas
2. **Fitness**: Mantener rutina de ejercicio con objetivos de salud
3. **Desarrollo Personal**: Hábitos diarios como lectura, meditación, etc.
4. **Productividad**: Tareas profesionales con objetivos de carrera

## 📝 Tareas Predeterminadas

La app incluye 3 tareas de ejemplo:

1. **Hacer ejercicio**
   - Mejorar salud cardiovascular
   - Sentirse más energético
   - Alcanzar peso ideal

2. **Estudiar inglés**
   - Mejores oportunidades laborales
   - Viajar sin limitaciones
   - Expandir conocimiento

3. **Leer 30 minutos**
   - Desarrollar hábito de lectura
   - Aprender cosas nuevas
   - Reducir tiempo en redes sociales

## 🎯 Roadmap Futuro

- [ ] Estadísticas de cumplimiento
- [ ] Múltiples recordatorios por día
- [ ] Widgets de pantalla de inicio
- [ ] Compartir progreso
- [ ] Temas personalizables
- [ ] Backup en la nube
- [ ] Recordatorios inteligentes basados en ubicación

## 👨‍💻 Autor

Desarrollado con ❤️ para ayudar a las personas a mantener su motivación

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

---

**¡Mantente motivado y alcanza tus metas! 🚀**

