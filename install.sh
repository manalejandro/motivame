#!/bin/bash

# Script de instalación y prueba para Motívame
# Uso: ./install.sh

echo "🚀 Motívame - Instalación y Prueba"
echo "===================================="
echo ""

# Verificar que existe adb
if ! command -v adb &> /dev/null; then
    echo "❌ Error: adb no está instalado o no está en el PATH"
    echo "   Instala Android SDK Platform Tools"
    exit 1
fi

# Verificar dispositivos conectados
echo "📱 Verificando dispositivos conectados..."
DEVICES=$(adb devices | grep -v "List" | grep "device$" | wc -l)

if [ $DEVICES -eq 0 ]; then
    echo "❌ Error: No hay dispositivos Android conectados"
    echo "   Conecta un dispositivo por USB o inicia un emulador"
    exit 1
fi

echo "✅ Dispositivo encontrado"
echo ""

# Compilar el proyecto
echo "🔨 Compilando el proyecto..."
./gradlew --no-daemon clean assembleDebug

if [ $? -ne 0 ]; then
    echo "❌ Error al compilar el proyecto"
    exit 1
fi

echo "✅ Compilación exitosa"
echo ""

# Instalar APK
echo "📦 Instalando APK en el dispositivo..."
adb install -r app/build/outputs/apk/debug/app-debug.apk

if [ $? -ne 0 ]; then
    echo "❌ Error al instalar el APK"
    exit 1
fi

echo "✅ APK instalado correctamente"
echo ""

# Conceder permisos (Android 13+)
echo "🔐 Concediendo permisos..."
adb shell pm grant com.manalejandro.motivame android.permission.POST_NOTIFICATIONS 2>/dev/null
echo "✅ Permisos configurados"
echo ""

# Iniciar la aplicación
echo "🎉 Iniciando Motívame..."
adb shell am start -n com.manalejandro.motivame/.MainActivity

echo ""
echo "✅ ¡Listo! La aplicación debería estar ejecutándose"
echo ""
echo "📝 Próximos pasos:"
echo "   1. Explora las tareas predeterminadas"
echo "   2. Agrega tu propia tarea"
echo "   3. Ve a Configuración y prueba las notificaciones"
echo "   4. Los recordatorios se enviarán diariamente a las 9:00 AM"
echo ""
echo "🐛 Para ver logs en tiempo real:"
echo "   adb logcat | grep Motivame"
echo ""

