# 🎮 LevelUp Mobile (Kotlin + Compose)

Esqueleto base de app e-commerce sin login (**Evaluación Parcial 2**).

## 🧠 Tech
- Jetpack Compose (Material 3)
- Navigation Compose
- MVVM (stubs de ViewModel y Repository)
- Coil (carga de imágenes)

## 🧩 Estructura
- `ui/` pantallas y componentes
    - **screens:** Home, ProductDetail, Cart, Checkout
    - **components:** ProductCard, AppButton, InputField
    - **theme:** paleta Level-Up (negro, azul eléctrico, verde neón)
- `nav/` rutas de navegación
- `vm/` viewmodels (stubs)
- `domain/repo/` contrato del repositorio
- (próximo) `data/` Room + DataStore

## ✅ Avance Actual
- Theme personalizado **Level-Up Gamer**
- Pantallas funcionales con navegación y diseño Material 3
- Formularios **validados visualmente** (Checkout)
- Componentes reutilizables (botones, tarjetas, inputs)
- **Animaciones**: entrada en Detalle + “pop” al agregar productos

## 📌 Próximas tareas
- [ ] Implementar Room (entities/daos/db) + KSP 2.0.21-1.0.27
- [ ] ShopRepository (impl) y ViewModels
- [ ] Conectar UI con datos reales (catálogo y carrito persistente)
- [ ] Recursos nativos: Maps y compartir por WhatsApp
- [ ] Reseñas, referidos y puntos

---
