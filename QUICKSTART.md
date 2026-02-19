# 🚀 Inicio Rápido - Motívame

## Instalación Rápida

### Opción 1: Script Automático (Recomendado)
```bash
./install.sh
```

### Opción 2: Manual
```bash
# 1. Compilar
./gradlew assembleDebug

# 2. Instalar
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 3. Abrir
adb shell am start -n com.manalejandro.motivame/.MainActivity
```

## 📱 Uso Básico

### Al abrir por primera vez:
1. ✨ Verás 3 tareas de ejemplo
2. ➕ Presiona el botón + para agregar tu tarea
3. ⚙️ Ve a Configuración para probar notificaciones

### Agregar una tarea:
1. Toca el botón flotante (+)
2. Escribe qué quieres recordar
3. Agrega tus metas (el "por qué")
4. Guarda

### Probar notificaciones:
1. Ve a Configuración (⚙️)
2. Presiona "Enviar notificación de prueba"
3. Verás la notificación con vibración y sonido

## 🔔 Recordatorios Diarios

- Se envían automáticamente a las **9:00 AM**
- Funcionan aunque la app esté cerrada
- Muestran una tarea activa con una meta aleatoria
- Incluyen vibración y sonido (configurable)

## ⚙️ Configuración

### Activar/Desactivar:
- **Notificaciones**: Toggle en Configuración
- **Sonido**: Toggle en Configuración

### Permisos (Android 13+):
- La app solicitará permisos al intentar activar notificaciones
- Acepta para recibir recordatorios

## 📋 Funciones

| Función | Descripción |
|---------|-------------|
| ➕ Agregar | Crea nuevas tareas con metas |
| ✓/✗ Estado | Activa/pausa tareas |
| 🗑️ Eliminar | Borra tareas (con confirmación) |
| ⚙️ Config | Ajusta notificaciones y sonido |
| 🔔 Prueba | Envía notificación inmediata |

## 🎯 Tips

- **Meta motivadora**: Escribe "por qué" quieres hacer la tarea
- **Múltiples metas**: Agrega varias razones para más motivación
- **Pausar tareas**: Desactiva temporalmente sin eliminar
- **Probar primero**: Usa el botón de prueba antes de esperar al día siguiente

## 🐛 Problemas Comunes

**No aparecen notificaciones:**
- Verifica permisos en Ajustes del sistema
- Asegura que las notificaciones están activas en la app
- Verifica que hay al menos una tarea activa

**Las tareas no se guardan:**
- Presiona el botón "Guardar Tarea"
- No uses el botón atrás del sistema

**WorkManager no funciona:**
- Desactiva optimización de batería para la app
- En Ajustes > Apps > Motívame > Batería > Sin restricciones

## 📚 Más Información

- **README.md**: Documentación completa
- **TESTING.md**: Guía de pruebas detallada
- **RESUMEN.md**: Características implementadas

## 🎉 ¡Listo!

Ya puedes usar **Motívame** para mantener tus metas en mente y motivarte a completar tus tareas diarias.

**¿Dudas?** Revisa los archivos de documentación incluidos en el proyecto.

