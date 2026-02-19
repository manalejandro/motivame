# Guía de Pruebas - Motívame

## 🧪 Casos de Prueba

### 1. Primera Ejecución
**Objetivo**: Verificar que las tareas predeterminadas se cargan correctamente

**Pasos**:
1. Instalar la app
2. Abrir la app por primera vez
3. Verificar que aparecen 3 tareas predeterminadas:
   - Hacer ejercicio
   - Estudiar inglés
   - Leer 30 minutos

**Resultado esperado**: ✅ Las 3 tareas se muestran con sus metas

---

### 2. Agregar Nueva Tarea
**Objetivo**: Crear una tarea personalizada

**Pasos**:
1. Presionar el botón flotante (+)
2. Escribir "Aprender programación" como título
3. Agregar meta: "Conseguir mejor trabajo"
4. Agregar meta: "Crear mis propios proyectos"
5. Presionar "Guardar Tarea"
6. Volver a la pantalla principal

**Resultado esperado**: ✅ Nueva tarea aparece en la lista

---

### 3. Pausar/Reanudar Tarea
**Objetivo**: Verificar el toggle de estado

**Pasos**:
1. En una tarea, presionar el icono de check (✓)
2. Observar que cambia a (✗) y aparece "⏸️ Pausada"
3. Presionar nuevamente para reactivar

**Resultado esperado**: ✅ El estado cambia correctamente

---

### 4. Eliminar Tarea
**Objetivo**: Borrar una tarea existente

**Pasos**:
1. Presionar el icono de eliminar (🗑️) en una tarea
2. Confirmar en el diálogo
3. Verificar que la tarea desaparece

**Resultado esperado**: ✅ Tarea eliminada de la lista

---

### 5. Configuración de Notificaciones
**Objetivo**: Activar/desactivar notificaciones

**Pasos**:
1. Ir a Configuración (⚙️)
2. Desactivar el switch de "Recordatorios diarios"
3. Activarlo nuevamente
4. Desactivar el switch de "Sonido"

**Resultado esperado**: ✅ Los switches responden correctamente

---

### 6. Notificación de Prueba
**Objetivo**: Verificar el sistema de notificaciones

**Pasos**:
1. Ir a Configuración
2. Asegurar que hay al menos una tarea activa
3. Presionar "Enviar notificación de prueba"
4. Verificar que aparece la notificación
5. Observar el título de la tarea
6. Observar que muestra una meta aleatoria
7. Verificar vibración (si está habilitada)
8. Verificar sonido (si está habilitado)
9. Presionar la notificación

**Resultado esperado**: 
- ✅ Notificación aparece en la barra de estado
- ✅ Muestra título de tarea y meta
- ✅ Vibra con patrón personalizado
- ✅ Emite sonido (si está activo)
- ✅ Al tocarla, abre la app

---

### 7. Permisos en Android 13+
**Objetivo**: Verificar solicitud de permisos

**Pasos**:
1. En Android 13 o superior
2. Primera instalación de la app
3. Ir a Configuración
4. Intentar activar notificaciones
5. Otorgar permiso en el diálogo del sistema

**Resultado esperado**: ✅ Diálogo de permisos aparece

---

### 8. Persistencia de Datos
**Objetivo**: Verificar que los datos se guardan

**Pasos**:
1. Agregar una nueva tarea
2. Cerrar completamente la app
3. Forzar cierre desde ajustes del sistema
4. Volver a abrir la app

**Resultado esperado**: ✅ La tarea agregada sigue ahí

---

### 9. Pantalla Vacía
**Objetivo**: Verificar estado sin tareas

**Pasos**:
1. Eliminar todas las tareas
2. Observar la pantalla principal

**Resultado esperado**: 
- ✅ Muestra mensaje "¡Comienza tu viaje!"
- ✅ Icono grande de estrella
- ✅ Mensaje motivacional

---

### 10. WorkManager - Recordatorio Diario
**Objetivo**: Verificar recordatorios automáticos

**Método A - Esperar**:
1. Dejar la app instalada
2. Esperar hasta las 9:00 AM del día siguiente
3. Verificar notificación automática

**Método B - Cambiar hora** (para desarrollo):
1. Modificar `MainActivity.kt` línea ~54:
   ```kotlin
   set(java.util.Calendar.HOUR_OF_DAY, 9) // Cambiar a hora actual + 1 minuto
   ```
2. Recompilar e instalar
3. Esperar el minuto

**Resultado esperado**: ✅ Notificación se envía automáticamente

---

## 🔍 Comandos ADB Útiles

### Ver Logs
```bash
adb logcat | grep -i motivame
```

### Ver Notificaciones
```bash
adb shell dumpsys notification | grep -A 10 motivame
```

### Ver WorkManager
```bash
adb shell dumpsys jobscheduler | grep motivame
```

### Limpiar Datos de la App
```bash
adb shell pm clear com.manalejandro.motivame
```

### Desinstalar
```bash
adb uninstall com.manalejandro.motivame
```

### Conceder Permisos Manualmente
```bash
adb shell pm grant com.manalejandro.motivame android.permission.POST_NOTIFICATIONS
```

### Simular Notificación (Debug)
```bash
adb shell am start -n com.manalejandro.motivame/.MainActivity
```

---

## 🐛 Solución de Problemas

### Problema: No aparecen notificaciones
**Soluciones**:
1. Verificar permisos en Ajustes > Apps > Motívame > Notificaciones
2. Asegurar que hay al menos una tarea activa
3. Verificar que las notificaciones están habilitadas en la app
4. Reiniciar el dispositivo

### Problema: Las tareas no se guardan
**Soluciones**:
1. Verificar que se presionó "Guardar Tarea"
2. Limpiar datos de la app e intentar nuevamente
3. Verificar logs con `adb logcat`

### Problema: WorkManager no funciona
**Soluciones**:
1. Verificar que la app no está en modo "Ahorro de batería"
2. Desactivar optimización de batería para la app
3. Verificar con `adb shell dumpsys jobscheduler`

### Problema: Compilación falla
**Soluciones**:
1. Ejecutar `./gradlew clean`
2. Invalidar cachés de Android Studio
3. Verificar conexión a Internet (para descargar dependencias)

---

## ✅ Checklist de Pruebas

- [ ] Instalación exitosa
- [ ] Tareas predeterminadas cargadas
- [ ] Agregar nueva tarea funciona
- [ ] Agregar múltiples metas funciona
- [ ] Eliminar metas funciona
- [ ] Pausar/reanudar tarea funciona
- [ ] Eliminar tarea funciona
- [ ] Configuración abre correctamente
- [ ] Toggle de notificaciones funciona
- [ ] Toggle de sonido funciona
- [ ] Notificación de prueba funciona
- [ ] Notificación muestra tarea y meta
- [ ] Vibración funciona
- [ ] Sonido funciona (cuando está activo)
- [ ] Click en notificación abre la app
- [ ] Datos persisten al cerrar app
- [ ] Pantalla vacía se muestra correctamente
- [ ] Permisos se solicitan correctamente (Android 13+)
- [ ] WorkManager programado correctamente
- [ ] UI se ve correctamente
- [ ] No hay crashes

---

## 📊 Resultados Esperados

**Tasa de éxito**: 100% en todas las pruebas
**Performance**: Fluido, sin lag
**Estabilidad**: Sin crashes
**UX**: Intuitivo y fácil de usar

---

## 📝 Notas Adicionales

1. **Primera vez**: Las tareas predeterminadas solo aparecen si no hay datos previos
2. **Notificaciones**: En algunos dispositivos Xiaomi/Huawei puede ser necesario configurar permisos adicionales
3. **WorkManager**: Los recordatorios pueden tener un margen de ±15 minutos dependiendo del sistema
4. **Batería**: En modo ahorro extremo, las notificaciones pueden retrasarse

