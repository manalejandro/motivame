# 📂 Estructura del Proyecto Motívame

```
Motivame/
│
├── 📄 README.md                          # Documentación completa del proyecto
├── 📄 QUICKSTART.md                      # Guía de inicio rápido
├── 📄 TESTING.md                         # Guía de pruebas detallada
├── 📄 RESUMEN.md                         # Resumen de implementación
├── 🔧 build.gradle.kts                   # Configuración del proyecto
├── 🔧 settings.gradle.kts                # Configuración de módulos
├── 🔧 gradle.properties                  # Propiedades de Gradle
├── 🔧 gradlew                           # Script Gradle (Linux/Mac)
├── 🔧 gradlew.bat                       # Script Gradle (Windows)
├── 🔧 local.properties                  # Configuración local
├── 📜 install.sh                        # Script de instalación automática
│
├── gradle/                              # Configuración de Gradle
│   ├── libs.versions.toml               # Versiones centralizadas
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
│
└── app/                                 # Módulo principal de la aplicación
    ├── 🔧 build.gradle.kts              # Configuración del módulo app
    ├── 🔧 proguard-rules.pro            # Reglas de ofuscación
    │
    ├── src/
    │   ├── main/
    │   │   ├── 📄 AndroidManifest.xml   # Manifest con permisos
    │   │   │
    │   │   ├── java/com/manalejandro/motivame/
    │   │   │   │
    │   │   │   ├── 📱 MainActivity.kt                    # Actividad principal
    │   │   │   │                                          # - Navegación entre pantallas
    │   │   │   │                                          # - Configuración de WorkManager
    │   │   │   │                                          # - Cálculo de delay inicial
    │   │   │   │
    │   │   │   ├── data/                                 # Capa de datos
    │   │   │   │   ├── 📦 Task.kt                        # Modelo de tarea
    │   │   │   │   │                                      # - id, title, goals, isActive
    │   │   │   │   └── 💾 TaskRepository.kt              # Repositorio de datos
    │   │   │   │                                          # - DataStore para persistencia
    │   │   │   │                                          # - CRUD de tareas
    │   │   │   │                                          # - Tareas predeterminadas
    │   │   │   │
    │   │   │   ├── notifications/                        # Sistema de notificaciones
    │   │   │   │   └── 🔔 NotificationHelper.kt          # Helper de notificaciones
    │   │   │   │                                          # - Crear canal
    │   │   │   │                                          # - Enviar notificaciones
    │   │   │   │                                          # - Vibración y sonido
    │   │   │   │
    │   │   │   ├── ui/                                   # Interfaz de usuario
    │   │   │   │   │
    │   │   │   │   ├── screens/                          # Pantallas
    │   │   │   │   │   ├── 📱 MainScreen.kt              # Pantalla principal
    │   │   │   │   │   │                                  # - Lista de tareas
    │   │   │   │   │   │                                  # - TaskCard componente
    │   │   │   │   │   │                                  # - EmptyState
    │   │   │   │   │   │                                  # - FAB agregar
    │   │   │   │   │   │
    │   │   │   │   │   ├── ➕ AddTaskScreen.kt           # Pantalla agregar tarea
    │   │   │   │   │   │                                  # - Formulario de tarea
    │   │   │   │   │   │                                  # - Agregar metas
    │   │   │   │   │   │                                  # - Lista de metas
    │   │   │   │   │   │                                  # - Validación
    │   │   │   │   │   │
    │   │   │   │   │   └── ⚙️ SettingsScreen.kt          # Pantalla configuración
    │   │   │   │   │                                      # - Toggle notificaciones
    │   │   │   │   │                                      # - Toggle sonido
    │   │   │   │   │                                      # - Botón prueba
    │   │   │   │   │                                      # - Permisos runtime
    │   │   │   │   │
    │   │   │   │   ├── theme/                            # Tema de la app
    │   │   │   │   │   ├── 🎨 Color.kt                   # Paleta de colores
    │   │   │   │   │   │                                  # - Colores primarios
    │   │   │   │   │   │                                  # - Modo claro/oscuro
    │   │   │   │   │   │
    │   │   │   │   │   ├── 🎨 Theme.kt                   # Tema Material 3
    │   │   │   │   │   │                                  # - ColorScheme
    │   │   │   │   │   │                                  # - MotivameTheme composable
    │   │   │   │   │   │
    │   │   │   │   │   └── 📝 Type.kt                    # Tipografía
    │   │   │   │   │
    │   │   │   │   └── viewmodel/                        # ViewModels
    │   │   │   │       └── 🧠 TaskViewModel.kt           # ViewModel principal
    │   │   │   │                                          # - Estado de tareas
    │   │   │   │                                          # - Operaciones CRUD
    │   │   │   │                                          # - Configuración
    │   │   │   │
    │   │   │   └── worker/                               # Workers
    │   │   │       └── ⏰ DailyReminderWorker.kt         # Worker de recordatorios
    │   │   │                                              # - Ejecución diaria
    │   │   │                                              # - Envío de notificaciones
    │   │   │                                              # - Verificación de config
    │   │   │
    │   │   └── res/                                      # Recursos
    │   │       ├── drawable/                             # Drawables
    │   │       │   ├── ic_launcher_background.xml
    │   │       │   └── ic_launcher_foreground.xml
    │   │       │
    │   │       ├── mipmap-*/                            # Iconos de launcher
    │   │       │   ├── ic_launcher.webp
    │   │       │   └── ic_launcher_round.webp
    │   │       │
    │   │       ├── values/                              # Valores
    │   │       │   ├── colors.xml
    │   │       │   ├── strings.xml                      # Textos de la app
    │   │       │   └── themes.xml
    │   │       │
    │   │       └── xml/                                 # XMLs varios
    │   │           ├── backup_rules.xml
    │   │           └── data_extraction_rules.xml
    │   │
    │   ├── androidTest/                                 # Tests instrumentados
    │   │   └── java/com/manalejandro/motivame/
    │   │       └── ExampleInstrumentedTest.kt
    │   │
    │   └── test/                                        # Tests unitarios
    │       └── java/com/manalejandro/motivame/
    │           └── ExampleUnitTest.kt
    │
    └── build/                                           # Archivos generados
        └── outputs/
            └── apk/
                └── debug/
                    └── app-debug.apk                    # 📦 APK compilado
```

---

## 📊 Resumen de Componentes

### 🎯 Archivos Principales de Código (12)

| Archivo | Líneas | Responsabilidad |
|---------|--------|-----------------|
| MainActivity.kt | ~90 | Navegación y WorkManager |
| Task.kt | ~10 | Modelo de datos |
| TaskRepository.kt | ~140 | Persistencia DataStore |
| TaskViewModel.kt | ~80 | Lógica de negocio |
| MainScreen.kt | ~230 | UI pantalla principal |
| AddTaskScreen.kt | ~220 | UI agregar tarea |
| SettingsScreen.kt | ~220 | UI configuración |
| NotificationHelper.kt | ~90 | Sistema notificaciones |
| DailyReminderWorker.kt | ~30 | Worker recordatorios |
| Color.kt | ~30 | Paleta de colores |
| Theme.kt | ~80 | Tema Material 3 |
| Type.kt | ~15 | Tipografía |

### 📚 Documentación (4 archivos)

| Archivo | Propósito |
|---------|-----------|
| README.md | Documentación técnica completa |
| QUICKSTART.md | Guía de inicio rápido |
| TESTING.md | Casos de prueba |
| RESUMEN.md | Características implementadas |

### 🔧 Configuración (5 archivos)

| Archivo | Contenido |
|---------|-----------|
| build.gradle.kts | Dependencias del módulo app |
| libs.versions.toml | Versiones centralizadas |
| AndroidManifest.xml | Permisos y componentes |
| strings.xml | Recursos de texto |
| install.sh | Script de instalación |

---

## 🎨 Flujo de Navegación

```
MainActivity
    ↓
MotivameApp (Composable)
    ↓
    ├─→ MainScreen ──┬─→ AddTaskScreen
    │                └─→ SettingsScreen
    │
    ├─→ AddTaskScreen ──→ MainScreen
    │
    └─→ SettingsScreen ──→ MainScreen
```

---

## 🔄 Flujo de Datos

```
User Action
    ↓
UI Screen (Compose)
    ↓
TaskViewModel
    ↓
TaskRepository
    ↓
DataStore
    ↓
StateFlow (reactive)
    ↓
UI Update
```

---

## 📦 Dependencias Clave

```kotlin
// Core
androidx.core:core-ktx:1.10.1
androidx.lifecycle:lifecycle-runtime-ktx:2.6.1

// Compose
androidx.compose:compose-bom:2024.09.00
androidx.compose.material3:material3
androidx.compose.material:material-icons-extended:1.5.4

// Architecture
androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1
androidx.work:work-runtime-ktx:2.9.0
androidx.datastore:datastore-preferences:1.0.0
```

---

## 🎯 Puntos de Entrada

1. **Aplicación**: `MainActivity.onCreate()`
2. **UI**: `MotivameApp()` composable
3. **Datos**: `TaskRepository` initialization
4. **Notificaciones**: `NotificationHelper.sendTaskReminder()`
5. **Worker**: `DailyReminderWorker.doWork()`

---

## 🔐 Permisos Declarados

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

---

## ⚙️ Configuración de WorkManager

```kotlin
Periodicidad: 1 día (24 horas)
Horario: 9:00 AM
Política: KEEP (no duplicar)
Constraints: Sin red requerida
```

---

## 💾 Estructura de Datos

### Task (JSON en DataStore)
```json
{
  "id": "uuid",
  "title": "string",
  "goals": ["string", "string"],
  "isActive": boolean,
  "createdAt": long
}
```

---

## 🎨 Paleta de Colores

| Color | Hex | Uso |
|-------|-----|-----|
| Primary | #6366F1 | Acciones principales |
| Secondary | #EC4899 | Acentos motivadores |
| Tertiary | #8B5CF6 | Elementos terciarios |
| Success | #10B981 | Estados positivos |
| Error | #EF4444 | Alertas y errores |

---

## 📱 Compatibilidad

- **Mínimo**: Android 7.0 (API 24)
- **Target**: Android 14 (API 36)
- **Recomendado**: Android 9.0+ (API 28+)

---

## 🚀 Build Variants

- **debug**: Versión de desarrollo con logs
- **release**: Versión optimizada para producción

---

Esta estructura proporciona:
✅ Separación clara de responsabilidades
✅ Fácil mantenimiento y escalabilidad
✅ Código organizado y legible
✅ Arquitectura MVVM bien definida
✅ Documentación completa

