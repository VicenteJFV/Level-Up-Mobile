package com.example.levelupmobile.vm

import com.example.levelupmobile.domain.model.Product
import com.example.levelupmobile.domain.repo.ShopRepository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    @Test
    fun `products expone la lista entregada por el repositorio`() = runTest {
        val dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)

        // Given
        val expectedProducts = listOf(
            Product(
                id = "1",
                name = "PlayStation 5",
                description = "Consola de última generación",
                priceNeto = 549_990L,
                ivaRate = 0.19,
                imageUrl = null,
                stock = 10,
                categoryId = 1
            ),
            Product(
                id = "2",
                name = "Xbox Series X",
                description = "Consola de Microsoft",
                priceNeto = 529_990L,
                ivaRate = 0.19,
                imageUrl = null,
                stock = 5,
                categoryId = 1
            )
        )

        val repo = mockk<ShopRepository>()
        every { repo.observeProducts() } returns flowOf(expectedProducts)

        // When
        val viewModel = ProductViewModel(repo)

        // Esperamos la primera emisión real del StateFlow
        val actual = viewModel.products.first()

        // Then
        assertEquals(expectedProducts, actual)
    }

    @Test
    fun `products parte como lista vacia cuando el repo emite lista vacia`() = runTest {
        val dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)

        val repo = mockk<ShopRepository>()
        every { repo.observeProducts() } returns flowOf(emptyList())

        val viewModel = ProductViewModel(repo)

        val actual = viewModel.products.first()

        assertEquals(emptyList<Product>(), actual)
    }
}
