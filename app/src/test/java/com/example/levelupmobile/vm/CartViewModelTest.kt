package com.example.levelupmobile.vm

import com.example.levelupmobile.domain.model.Product
import com.example.levelupmobile.domain.model.CartLine   // 👈 ajusta el nombre/paquete si es distinto
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.vm.models.CartItemUi
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private lateinit var repo: ShopRepository
    private lateinit var cartFlow: MutableStateFlow<List<CartLine>>
    private lateinit var productsFlow: MutableStateFlow<List<Product>>
    private lateinit var viewModel: CartViewModel

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repo = mockk(relaxed = true)

        cartFlow = MutableStateFlow(emptyList())
        productsFlow = MutableStateFlow(emptyList())

        every { repo.observeCart() } returns cartFlow
        every { repo.observeProducts() } returns productsFlow

        viewModel = CartViewModel(repo)
    }

    @Test
    fun `ui calcula subtotal iva y total correctamente`() = runTest {
        // Given
        val p1 = Product(
            id = "1",
            name = "PlayStation 5",
            description = "Consola",
            priceNeto = 500_000L,
            ivaRate = 0.19,
            imageUrl = null,
            stock = 10,
            categoryId = 1
        )
        val p2 = Product(
            id = "2",
            name = "Mouse gamer",
            description = "RGB",
            priceNeto = 50_000L,
            ivaRate = 0.19,
            imageUrl = null,
            stock = 20,
            categoryId = 1
        )

        productsFlow.value = listOf(p1, p2)
        cartFlow.value = listOf(
            CartLine(productId = "1", qty = 2),
            CartLine(productId = "2", qty = 1)
        )

        // When
        val ui = viewModel.ui.first()

        // Then
        // items
        assertEquals(2, ui.items.size)
        assertEquals(CartItemUi("1", "PlayStation 5", 500_000L, 2, null), ui.items[0])
        assertEquals(CartItemUi("2", "Mouse gamer", 50_000L, 1, null), ui.items[1])

        // totales
        val expectedSubtotal = 2 * 500_000L + 1 * 50_000L
        val expectedIva = (expectedSubtotal * 0.19).toLong()
        val expectedTotal = expectedSubtotal + expectedIva

        assertEquals(expectedSubtotal, ui.subtotal)
        assertEquals(expectedIva, ui.iva)
        assertEquals(expectedTotal, ui.total)
    }

    @Test
    fun `inc llama a addToCart con qty 1`() = runTest {
        // When
        viewModel.inc("1")

        // Then
        coVerify { repo.addToCart("1", 1) }
    }

    @Test
    fun `dec con qty 1 elimina el item del carrito`() = runTest {
        // Given: un producto en el carrito con qty = 1
        val p = Product(
            id = "1",
            name = "PS5",
            description = "Consola",
            priceNeto = 500_000L,
            ivaRate = 0.19,
            imageUrl = null,
            stock = 10,
            categoryId = 1
        )
        productsFlow.value = listOf(p)
        cartFlow.value = listOf(
            CartLine(productId = "1", qty = 1)
        )

        // Aseguramos que el uiState ya vea ese item
        viewModel.ui.first()

        // When
        viewModel.dec("1")

        // Then: debe eliminar, no setear qty 0
        coVerify { repo.removeFromCart("1") }
    }
}
