# 🎮 Level-Up Mobile (Kotlin + Jetpack Compose)

Aplicación móvil desarrollada en **Kotlin** utilizando **Jetpack Compose**.  
Su objetivo es ofrecer una experiencia moderna, fluida y visualmente atractiva para un entorno de compras gamer.

---

🧪 Instalación y pruebas locales
✅ Requisitos
Android Studio (Giraffe o superior recomendado)
JDK 17 (el que instala Android Studio está bien)
Android SDK con al menos una imagen de sistema (Android 12 o superior recomendado)
Gradle Wrapper incluido en el proyecto (usa ./gradlew)

Nota: La app se inicializa con datos desde assets/products.json y persiste en Room. Si quieres “recargar” datos, borra el almacenamiento de la app desde Ajustes.

---

1) Clonar y abrir el proyecto
git clone https://github.com/VicenteJFV/Level-Up-Mobile.git
cd levelupmobile
 

Abre la carpeta en Android Studio.
Espera a que sincronice dependencias (Gradle Sync).

2) Ejecutar en emulador
En Android Studio, abre Device Manager → crea un Virtual Device (Small Phone recomendado).
Selecciona una imagen de sistema (API 31+ recomendado).
Presiona Run ▶️ en app.


## 🧠 Tech Stack

- **Jetpack Compose (Material 3)** – interfaz moderna y declarativa  
- **Navigation Compose** – flujo de pantallas  
- **MVVM + StateFlow + Coroutines** – arquitectura limpia y reactiva  
- **Room Database** – persistencia local del catálogo y carrito  
- **JSON (products.json)** – carga inicial de productos  

---

## 🧩 Estructura del Proyecto

com.example.levelupmobile/

│

├── ui/

│ ├── screens/ → Home, ProductDetail, Cart, Checkout

│ ├── components/ → ProductCard, AppButton, InputField

│

└── theme/ → Paleta Level-Up (negro, azul eléctrico, verde neón)

│

├── nav/ → Rutas y AppNavHost

├── vm/ → ViewModels (CartVM, ProductDetailVM)

├── domain/ → Modelos y contratos de repositorio

├── data/ → Room (entities, dao, db, impl)

│

├── assets/

│ └── products.json

│

└── MainActivity.kt

## ✅ Estado Actual
- 🎨 **Theme personalizado** estilo *Level-Up Gamer*  
- 🧭 **Pantallas funcionales** con navegación fluida  
- 💰 **Cálculo automático** de subtotal, IVA y total  
- 💾 **Carga inicial de productos** desde JSON + persistencia con Room  
- ⚡ **UI reactiva** mediante StateFlow  
- 🌀 **Animaciones suaves** y componentes reutilizables  

---

## 🚀 Próximos Pasos

- ⭐ Reseñas y puntuación de productos  
- 🔐 Login y registro de usuarios  
- 🗺️ Integración con Google Maps y WhatsApp Share  
- 🌐 Sincronización con API externa  
- 🏆 Sistema de referidos o puntos  

---

## 👤 Autores

- **Vicente Farías Vera**  
- **Felipe Villalobos Villalobos**  
- **Emilio Jaramillo Luna**  

📍 *DUOC UC – Ingeniería en Informática (2025)*

---
