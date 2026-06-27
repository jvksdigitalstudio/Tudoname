# Tudoname — Android App v1.0 Beta

App nativa Android para encontrar nombres de bebés y mascotas. Basada en el proyecto web Tudoname.

## 📱 Pantallas

| Pantalla | Descripción |
|----------|-------------|
| **MainActivity** | Selección de género (Niño/Niña/Perro/Perra/Gato/Gata) |
| **ResultadosActivity** | Explorador de nombres con navegación y modo aleatorio |

## 🏗️ Estructura del proyecto

```
TudonameApp/
├── .github/workflows/android.yml   ← GitHub Actions CI/CD
├── app/
│   ├── build.gradle
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/tudoname/app/
│   │   │   ├── MainActivity.java
│   │   │   └── ResultadosActivity.java
│   │   └── res/
│   │       ├── layout/
│   │       ├── drawable/
│   │       ├── values/
│   │       ├── font/
│   │       └── anim/
├── build.gradle
├── settings.gradle
├── gradle.properties
└── gradlew
```

## 🚀 Compilar con GitHub Actions

### Paso 1 — Subir a GitHub

```bash
git init
git add .
git commit -m "feat: Tudoname Android v1.0 beta"
git remote add origin https://github.com/TU_USUARIO/tudoname-android.git
git push -u origin main
```

### Paso 2 — Obtener el gradle-wrapper.jar

Antes de hacer push, ejecuta esto en la carpeta del proyecto:

```bash
gradle wrapper --gradle-version=8.4
```

O descarga manualmente:
```
https://github.com/gradle/gradle/raw/v8.4.0/gradle/wrapper/gradle-wrapper.jar
→ colócalo en: gradle/wrapper/gradle-wrapper.jar
```

### Paso 3 — GitHub Actions construye automáticamente

Al hacer push a `main`, el workflow `.github/workflows/android.yml` se ejecuta y produce:
- `app-debug.apk` — descargable desde Actions → Artifacts

### Paso 4 — APK firmado (opcional, para Play Store)

Genera un keystore:
```bash
keytool -genkey -v -keystore tudoname.jks -keyalg RSA -keysize 2048 -validity 10000 -alias tudoname
```

Configura estos Secrets en GitHub (Settings → Secrets → Actions):
| Secret | Valor |
|--------|-------|
| `KEYSTORE_BASE64` | `base64 tudoname.jks` |
| `KEYSTORE_PASSWORD` | Tu contraseña del keystore |
| `KEY_ALIAS` | `tudoname` |
| `KEY_PASSWORD` | Tu contraseña de la clave |

## 🛠️ Compilar localmente

```bash
# Debug
./gradlew assembleDebug

# El APK queda en:
# app/build/outputs/apk/debug/app-debug.apk
```

## 📦 Requisitos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Android SDK 34
- minSdk: 24 (Android 7.0+)

## ✨ Features v1.0

- Selección de 6 géneros (Niño, Niña, Perro, Perra, Gato, Gata)
- +20 nombres por categoría
- Navegación animada entre nombres (swipe visual)
- Botón aleatorio 🎲
- Animaciones de entrada con overshoot + flotación
- Transiciones entre pantallas
- Diseño premium con gradientes y tipografía Fredoka One
- Soporte edge-to-edge
- minSdk 24 (cubre 94%+ de dispositivos Android)
