🎮 Level-Up Mobile (Kotlin + Jetpack Compose)

🧠 Tech Stack
-Jetpack Compose (Material 3) – interfaz moderna y declarativa
-Navigation Compose – flujo de pantallas
-MVVM + StateFlow + Coroutines – arquitectura limpia y reactiva
-Room Database – persistencia local del catálogo y carrito
-JSON (products.json) – carga inicial de productos

🧩 Estructura del Proyecto
com.example.levelupmobile/
ui/
screens/        → Home, ProductDetail, Cart, Checkout
components/     → ProductCard, AppButton, InputField
theme/          → Paleta Level-Up (negro, azul eléctrico, verde neón)
nav/                → Rutas y AppNavHost
vm/                 → ViewModels (CartVM, ProductDetailVM)
domain/             → Modelos y contratos de repositorio
data/               → Room (entities, dao, db, impl)
assets/products.json
MainActivity.kt

✅ Estado Actual
-Theme personalizado Level-Up Gamer
-Pantallas funcionales con navegación fluida
-Cálculo automático de subtotal, IVA y total
-Carga inicial de productos desde JSON + persistencia Room
-UI reactiva con StateFlow
-Animaciones suaves y componentes reutilizables

🚀 Próximos Pasos
-Reseñas y puntuación de productos
-Login y registro de usuarios
-Integración con Google Maps y WhatsApp Share
-Sincronización con API externa
-Sistema de referidos o puntos

👤 Autores
Vicente Farias Vera
Felipe Villalobos Villalobos
Emilio Jaramillo Luna
DUOC UC – Ingeniería en Informática (2025)
