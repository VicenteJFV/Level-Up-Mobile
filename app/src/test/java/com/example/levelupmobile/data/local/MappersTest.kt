package com.example.levelupmobile.data.local

import com.example.levelupmobile.data.dto.ProductDto
import org.junit.Test
import org.junit.Assert.*

class MappersTest {

    @Test
    fun `ProductDto con valores válidos se convierte a Product correctamente`() {
        // Given: un ProductDto de la API
        val dto = ProductDto(
            id = 1L,
            name = "PlayStation 5",
            description = "Consola de última generación",
            price = 549990.0,
            imageUrl = "http://example.com/ps5.jpg",
            stock = 10,
            platform = "PS5"
        )

        // When: se convierte a Product
        val product = dto.toDomain()

        // Then: los valores se mapean correctamente
        assertEquals("1", product.id)
        assertEquals("PlayStation 5", product.name)
        assertEquals("Consola de última generación", product.description)
        assertEquals(549990L, product.priceNeto)
        assertEquals(0.19, product.ivaRate, 0.001)
        assertEquals("http://example.com/ps5.jpg", product.imageUrl)
        assertEquals(10, product.stock)
    }

    @Test
    fun `ProductDto con description null se convierte con string vacío`() {
        val dto = ProductDto(
            id = 2L,
            name = "Mouse Gamer",
            description = null,
            price = 49990.0,
            imageUrl = null,
            stock = null,
            platform = null
        )

        val product = dto.toDomain()

        assertEquals("", product.description)
        assertNull(product.imageUrl)
        assertEquals(0, product.stock)
    }

    @Test
    fun `ProductDto con precio decimal se convierte a Long correctamente`() {
        val dto = ProductDto(
            id = 3L,
            name = "Teclado",
            description = "Mecánico",
            price = 89990.99,
            imageUrl = null,
            stock = 5,
            platform = "PC"
        )

        val product = dto.toDomain()

        assertEquals(89990L, product.priceNeto)
    }

    @Test
    fun `Product se convierte a ProductEntity correctamente`() {
        val product = com.example.levelupmobile.domain.model.Product(
            id = "123",
            name = "Auriculares",
            description = "HyperX",
            priceNeto = 79990L,
            ivaRate = 0.19,
            imageUrl = "http://example.com/hyperx.jpg",
            stock = 25,
            categoryId = 1
        )

        val entity = product.toEntity()

        assertEquals("123", entity.id)
        assertEquals("Auriculares", entity.name)
        assertEquals(79990L, entity.priceNeto)
        assertEquals("http://example.com/hyperx.jpg", entity.imageUrl)
    }

    @Test
    fun `ProductEntity se convierte a Product correctamente`() {
        val entity = com.example.levelupmobile.data.entity.ProductEntity(
            id = "456",
            name = "Silla Gamer",
            description = "Ergonómica",
            priceNeto = 389990L,
            ivaRate = 0.19,
            imageUrl = null,
            stock = 20,
            categoryId = 2
        )

        val product = entity.toDomain()

        assertEquals("456", product.id)
        assertEquals("Silla Gamer", product.name)
        assertEquals(389990L, product.priceNeto)
        assertNull(product.imageUrl)
    }
}