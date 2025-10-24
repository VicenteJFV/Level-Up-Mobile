# LevelUp Mobile (Kotlin + Compose)

Esqueleto base de app e-commerce sin login (Evaluación Parcial 2).

## Tech
- Jetpack Compose (Material 3)
- Navigation Compose
- MVVM (stubs de ViewModel y Repository)

## Estructura
- `ui/` pantallas y componentes
- `nav/` rutas de navegación
- `vm/` viewmodels (stubs)
- `domain/repo/` contrato del repositorio
- (próximo) `data/` Room + DataStore

## Próximas tareas
- [ ] Implementar Room (entities/daos/db) + KSP 2.0.21-1.0.27
- [ ] ShopRepository (impl) y ViewModels
- [ ] UI real: catálogo, carrito, checkout validado
- [ ] Notificación local y compartir pedido
