# 🎮 Level-Up Mobile (Kotlin + Jetpack Compose)

> **E-Commerce Híbrido con Arquitectura Offline-First**

Aplicación móvil desarrollada en **Kotlin** utilizando **Jetpack Compose**.  
Su objetivo es ofrecer una experiencia moderna, fluida y visualmente atractiva para un entorno de compras *gamer*, integrando persistencia local y sincronización con servicios en la nube.

---

## ⚡ Características Principales

### 🛍️ Experiencia de Compra
* **Catálogo Híbrido (Offline-First):** Los productos se cargan desde una base de datos local (**Room**) para acceso instantáneo, mientras se sincronizan en segundo plano con una API REST.
* **Carrito Inteligente:** Cálculo en tiempo real de Subtotal, IVA (19%) y Total. Persistencia local para no perder el carrito si se cierra la app.
* **Checkout Transaccional:** Validación de formularios y envío de pedidos al servidor.

### 📦 Gestión de Pedidos (Post-Venta)
* **Búsqueda de Órdenes:** Los usuarios pueden buscar sus pedidos anteriores por ID.
* **Edición de Pedidos:** Permite editar datos de contacto y dirección dentro de las primeras 24 horas (`CREATED`).
* **Gestión de Estados:** Funcionalidad para **Confirmar** o **Cancelar** pedidos directamente desde la app.

### 🎨 UI/UX Avanzada
* **Imágenes Dinámicas:** Implementación con **Coil** que soporta tanto URLs remotas como recursos locales (`drawable`) con animaciones de *crossfade*.
* **Diseño Reactivo:** Interfaz construida 100% en Compose con animaciones de estado y retroalimentación visual (Snackbars, loaders).
* **Diseño Defensivo:** La UI previene errores bloqueando acciones inválidas (ej. ir al checkout con carrito vacío).

---

## 🧠 Tech Stack

### Arquitectura
* **MVVM (Model-View-ViewModel):** Separación estricta de responsabilidades.
* **Clean Architecture:** Capas de Data, Domain y UI claramente definidas.
* **Repository Pattern:** Implementación híbrida que orquesta datos entre Local (Room) y Remoto (Retrofit).

### Librerías y Herramientas
* **Jetpack Compose (Material 3):** Interfaz declarativa moderna.
* **Kotlin Flow & Coroutines:** Manejo de asincronía y estado reactivo.
* **Room Database:** Persistencia local SQLite.
* **Retrofit + GSON:** Cliente HTTP para consumo de API REST.
* **Coil:** Carga y caché de imágenes asíncronas.
* **Navigation Compose:** Gestión de rutas y argumentos.

### 🛡️ Calidad y Testing (QA)
* **JUnit 4:** Framework de pruebas.
* **Mockk:** Simulación de dependencias (Repositorios, APIs).
* **Coroutines Test:** Pruebas de lógica asíncrona en ViewModels.
* **Coverage:** Tests unitarios para ViewModels (`Cart`, `Order`, `Product`) y Mappers.

---

## 🧩 Estructura del Proyecto

```text
com.example.levelupmobile/
│
├── ui/                 # Capa de Presentación
│   ├── screens/        # Pantallas (Home, ProductDetail, Cart, Checkout, OrderDetail)
│   ├── components/     # Componentes reutilizables (Cards, Buttons, Inputs)
│   └── theme/          # Sistema de Diseño (Paleta Gamer, Tipografía Orbitron)
│
├── vm/                 # ViewModels y Factories (Lógica de Presentación)
│
├── nav/                # Grafo de Navegación (AppNavHost, Routes)
│
├── domain/             # Capa de Dominio (Agnóstica de Frameworks)
│   ├── model/          # Modelos puros de Kotlin
│   └── repo/           # Interfaces del Repositorio (Contratos)
│
├── data/               # Capa de Datos
│   ├── local/          # Room DB, DAOs, Entidades
│   ├── api/            # Retrofit Clients, Interfaces API
│   ├── dto/            # Data Transfer Objects (Red)
│   └── repository/     # Implementación real (ShopRepositoryImpl)
│
└── assets/             # Datos semilla (products.json)
---

# 🎮 Level-Up Mobile (Kotlin + Jetpack Compose)

> **E-Commerce Híbrido con Arquitectura Offline-First**

Aplicación móvil desarrollada en **Kotlin** utilizando **Jetpack Compose**.  
Su objetivo es ofrecer una experiencia moderna, fluida y visualmente atractiva para un entorno de compras *gamer*, integrando persistencia local y sincronización con servicios en la nube.

---

## ⚡ Características Principales

### 🛍️ Experiencia de Compra
* **Catálogo Híbrido (Offline-First):** Los productos se cargan desde una base de datos local (**Room**) para acceso instantáneo, mientras se sincronizan en segundo plano con una API REST.
* **Carrito Inteligente:** Cálculo en tiempo real de Subtotal, IVA (19%) y Total. Persistencia local para no perder el carrito si se cierra la app.
* **Checkout Transaccional:** Validación de formularios y envío de pedidos al servidor.

### 📦 Gestión de Pedidos (Post-Venta)
* **Búsqueda de Órdenes:** Los usuarios pueden buscar sus pedidos anteriores por ID.
* **Edición de Pedidos:** Permite editar datos de contacto y dirección dentro de las primeras 24 horas (`CREATED`).
* **Gestión de Estados:** Funcionalidad para **Confirmar** o **Cancelar** pedidos directamente desde la app.

### 🎨 UI/UX Avanzada
* **Imágenes Dinámicas:** Implementación con **Coil** que soporta tanto URLs remotas como recursos locales (`drawable`) con animaciones de *crossfade*.
* **Diseño Reactivo:** Interfaz construida 100% en Compose con animaciones de estado y retroalimentación visual (Snackbars, loaders).
* **Diseño Defensivo:** La UI previene errores bloqueando acciones inválidas (ej. ir al checkout con carrito vacío).

---

## 🧠 Tech Stack

### Arquitectura
* **MVVM (Model-View-ViewModel):** Separación estricta de responsabilidades.
* **Clean Architecture:** Capas de Data, Domain y UI claramente definidas.
* **Repository Pattern:** Implementación híbrida que orquesta datos entre Local (Room) y Remoto (Retrofit).



### Librerías y Herramientas
* **Jetpack Compose (Material 3):** Interfaz declarativa moderna.
* **Kotlin Flow & Coroutines:** Manejo de asincronía y estado reactivo.
* **Room Database:** Persistencia local SQLite.
* **Retrofit + GSON:** Cliente HTTP para consumo de API REST.
* **Coil:** Carga y caché de imágenes asíncronas.
* **Navigation Compose:** Gestión de rutas y argumentos.



### 🛡️ Calidad y Testing (QA)
* **JUnit 4:** Framework de pruebas.
* **Mockk:** Simulación de dependencias (Repositorios, APIs).
* **Coroutines Test:** Pruebas de lógica asíncrona en ViewModels.
* **Coverage:** Tests unitarios para ViewModels (`Cart`, `Order`, `Product`) y Mappers.

---

## 🧩 Estructura del Proyecto

```text
com.example.levelupmobile/
│
├── ui/                 # Capa de Presentación
│   ├── screens/        # Pantallas (Home, ProductDetail, Cart, Checkout, OrderDetail)
│   ├── components/     # Componentes reutilizables (Cards, Buttons, Inputs)
│   └── theme/          # Sistema de Diseño (Paleta Gamer, Tipografía Orbitron)
│
├── vm/                 # ViewModels y Factories (Lógica de Presentación)
│
├── nav/                # Grafo de Navegación (AppNavHost, Routes)
│
├── domain/             # Capa de Dominio (Agnóstica de Frameworks)
│   ├── model/          # Modelos puros de Kotlin
│   └── repo/           # Interfaces del Repositorio (Contratos)
│
├── data/               # Capa de Datos
│   ├── local/          # Room DB, DAOs, Entidades
│   ├── api/            # Retrofit Clients, Interfaces API
│   ├── dto/            # Data Transfer Objects (Red)
│   └── repository/     # Implementación real (ShopRepositoryImpl)
│
└── assets/             # Datos semilla (products.json)



🧪 Instalación y Configuración

Requisitos
Android Studio (Koala o superior recomendado).

JDK 17.

Android SDK (API 31+ recomendado).

Backend (API)
La aplicación está configurada para conectarse a un servidor local en http://10.0.2.2:8081/ (alias de localhost para el emulador Android).

Nota: Si el servidor no está disponible, la app funcionará en modo "Solo Local" usando los datos cacheados en Room.




Pasos
Clonar el proyecto:

Bash

git clone [https://github.com/VicenteJFV/Level-Up-Mobile.git](https://github.com/VicenteJFV/Level-Up-Mobile.git)
cd levelupmobile
Abrir en Android Studio: Esperar la sincronización de Gradle.

Ejecutar Tests (Opcional):

Ve a la carpeta src/test/java.

Haz clic derecho -> "Run 'Tests in 'java''" para verificar la integridad de la lógica de negocio.

Ejecutar App: Selecciona un emulador y presiona Run ▶️.

✅ Estado Actual
🎨 Theme personalizado estilo Level-Up Gamer.

🧭 Navegación completa (Home -> Detalle -> Carrito -> Checkout -> Orden).

💰 Lógica de negocio validada con Unit Tests.

🌐 Conexión Híbrida (API REST + Base de Datos Local).

📸 Manejo de imágenes remoto y local.

📦 Gestión de ciclo de vida de pedidos implementada.





👤 Autores
Vicente Farías Vera

Felipe Villalobos Villalobos

Emilio Jaramillo Luna

📍 DUOC UC – Ingeniería en Informática (2025)
