package com.example.levelupmobile.vm

import com.example.levelupmobile.data.dto.OrderItemResponse
import com.example.levelupmobile.data.dto.OrderResponse
import com.example.levelupmobile.domain.repo.ShopRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModelTest {

    private lateinit var repo: ShopRepository
    private lateinit var viewModel: OrderViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repo = mockk(relaxed = true)
        viewModel = OrderViewModel(repo)
    }

    // ==================== SEARCH ORDER ====================

    @Test
    fun `searchOrder con orden existente actualiza el ui correctamente`() = runTest {
        // Given
        val mockOrder = createMockOrder(
            id = 1L,
            status = "CREATED",
            createdAt = "2025-11-29T10:00:00.000000"
        )
        coEvery { repo.getOrder(1L) } returns mockOrder

        // When
        viewModel.searchOrder(1L)

        // Then
        val ui = viewModel.ui.value
        assertEquals(mockOrder, ui.order)
        assertFalse(ui.isLoading)
        assertEquals(null, ui.error)
        assertTrue(ui.canEdit)
        assertTrue(ui.canConfirm)
        assertTrue(ui.canCancel)
    }

    @Test
    fun `searchOrder con orden no encontrada muestra error`() = runTest {
        // Given
        coEvery { repo.getOrder(999L) } returns null

        // When
        viewModel.searchOrder(999L)

        // Then
        val ui = viewModel.ui.value
        assertEquals(null, ui.order)
        assertFalse(ui.isLoading)
        assertEquals("Orden no encontrada", ui.error)
    }

    @Test
    fun `searchOrder con orden CONFIRMED deshabilita acciones`() = runTest {
        // Given
        val mockOrder = createMockOrder(
            id = 2L,
            status = "CONFIRMED",
            createdAt = "2025-11-29T10:00:00.000000"
        )
        coEvery { repo.getOrder(2L) } returns mockOrder

        // When
        viewModel.searchOrder(2L)

        // Then
        val ui = viewModel.ui.value
        assertFalse(ui.canEdit)
        assertFalse(ui.canConfirm)
        assertFalse(ui.canCancel)
    }

    @Test
    fun `searchOrder con orden expirada deshabilita acciones`() = runTest {
        // Given - Orden creada hace más de 24 horas
        val mockOrder = createMockOrder(
            id = 3L,
            status = "CREATED",
            createdAt = "2023-01-01T10:00:00.000000"
        )
        coEvery { repo.getOrder(3L) } returns mockOrder

        // When
        viewModel.searchOrder(3L)

        // Then
        val ui = viewModel.ui.value
        assertFalse(ui.canEdit)
        assertFalse(ui.canConfirm)
        assertFalse(ui.canCancel)
        assertEquals(0L, ui.hoursRemaining)
    }

    // ==================== EDITING ====================

    @Test
    fun `startEditing habilita modo edicion`() = runTest {
        // When
        viewModel.startEditing()

        // Then
        assertTrue(viewModel.ui.value.isEditing)
    }

    @Test
    fun `cancelEditing restaura valores originales`() = runTest {
        // Given
        val mockOrder = createMockOrder(
            id = 4L,
            status = "CREATED",
            createdAt = "2025-11-29T10:00:00.000000",
            phone = "123456789",
            address = "Original Address"
        )
        coEvery { repo.getOrder(4L) } returns mockOrder
        viewModel.searchOrder(4L)

        // When
        viewModel.startEditing()
        viewModel.onPhoneChange("999999999")
        viewModel.onAddressChange("New Address")
        viewModel.cancelEditing()

        // Then
        val ui = viewModel.ui.value
        assertFalse(ui.isEditing)
        assertEquals("123456789", ui.editPhone)
        assertEquals("Original Address", ui.editAddress)
    }

    @Test
    fun `onPhoneChange actualiza campo de telefono`() = runTest {
        // When
        viewModel.onPhoneChange("987654321")

        // Then
        assertEquals("987654321", viewModel.ui.value.editPhone)
    }

    @Test
    fun `onAddressChange actualiza campo de direccion`() = runTest {
        // When
        viewModel.onAddressChange("New Street 123")

        // Then
        assertEquals("New Street 123", viewModel.ui.value.editAddress)
    }

    // ==================== SAVE CHANGES ====================

    @Test
    fun `saveChanges actualiza orden exitosamente`() = runTest {
        // Given
        val originalOrder = createMockOrder(
            id = 5L,
            status = "CREATED",
            createdAt = "2025-11-29T10:00:00.000000",
            phone = "111111111",
            address = "Old Address"
        )
        val updatedOrder = originalOrder.copy(
            customerPhone = "222222222",
            deliveryAddress = "New Address"
        )

        coEvery { repo.getOrder(5L) } returns originalOrder
        coEvery { repo.updateOrder(5L, "222222222", "New Address") } returns updatedOrder

        viewModel.searchOrder(5L)
        viewModel.startEditing()
        viewModel.onPhoneChange("222222222")
        viewModel.onAddressChange("New Address")

        // When
        viewModel.saveChanges()

        // Then
        val ui = viewModel.ui.value
        assertEquals(updatedOrder, ui.order)
        assertFalse(ui.isEditing)
        assertFalse(ui.isLoading)
        coVerify { repo.updateOrder(5L, "222222222", "New Address") }
    }

    @Test
    fun `saveChanges con error no actualiza la orden`() = runTest {
        // Given
        val mockOrder = createMockOrder(
            id = 6L,
            status = "CREATED",
            createdAt = "2025-11-29T10:00:00.000000"
        )
        coEvery { repo.getOrder(6L) } returns mockOrder
        coEvery { repo.updateOrder(any(), any(), any()) } returns null

        viewModel.searchOrder(6L)
        viewModel.onPhoneChange("333333333")
        viewModel.onAddressChange("Some Address")

        // When
        viewModel.saveChanges()

        // Then
        assertFalse(viewModel.ui.value.isLoading)
    }

    // ==================== CONFIRM ORDER ====================

    @Test
    fun `confirmOrder actualiza orden y deshabilita acciones`() = runTest {
        // Given
        val createdOrder = createMockOrder(
            id = 7L,
            status = "CREATED",
            createdAt = "2025-11-29T10:00:00.000000"
        )
        val confirmedOrder = createdOrder.copy(status = "CONFIRMED")

        coEvery { repo.getOrder(7L) } returns createdOrder
        coEvery { repo.confirmOrder(7L) } returns confirmedOrder

        viewModel.searchOrder(7L)

        // When
        viewModel.confirmOrder()

        // Then
        val ui = viewModel.ui.value
        assertEquals(confirmedOrder, ui.order)
        assertFalse(ui.canEdit)
        assertFalse(ui.canConfirm)
        assertFalse(ui.canCancel)
        assertFalse(ui.isLoading)
    }

    @Test
    fun `confirmOrder con error no actualiza la orden`() = runTest {
        // Given
        val mockOrder = createMockOrder(
            id = 8L,
            status = "CREATED",
            createdAt = "2025-11-29T10:00:00.000000"
        )
        coEvery { repo.getOrder(8L) } returns mockOrder
        coEvery { repo.confirmOrder(8L) } returns null

        viewModel.searchOrder(8L)

        // When
        viewModel.confirmOrder()

        // Then
        assertFalse(viewModel.ui.value.isLoading)
    }

    // ==================== CANCEL ORDER ====================

    @Test
    fun `cancelOrder elimina orden exitosamente`() = runTest {
        // Given
        val mockOrder = createMockOrder(
            id = 9L,
            status = "CREATED",
            createdAt = "2025-11-29T10:00:00.000000"
        )
        coEvery { repo.getOrder(9L) } returns mockOrder
        coEvery { repo.cancelOrder(9L) } returns true

        viewModel.searchOrder(9L)

        // When
        viewModel.cancelOrder()

        // Then
        assertFalse(viewModel.ui.value.isLoading)
        coVerify { repo.cancelOrder(9L) }
    }

    @Test
    fun `cancelOrder con error no elimina la orden`() = runTest {
        // Given
        val mockOrder = createMockOrder(
            id = 10L,
            status = "CREATED",
            createdAt = "2025-11-29T10:00:00.000000"
        )
        coEvery { repo.getOrder(10L) } returns mockOrder
        coEvery { repo.cancelOrder(10L) } returns false

        viewModel.searchOrder(10L)

        // When
        viewModel.cancelOrder()

        // Then
        assertFalse(viewModel.ui.value.isLoading)
    }

    // ==================== HELPER ====================

    private fun createMockOrder(
        id: Long,
        status: String,
        createdAt: String,
        phone: String = "123456789",
        address: String = "Test Address"
    ): OrderResponse {
        return OrderResponse(
            id = id,
            customerName = "Test Customer",
            customerPhone = phone,
            deliveryAddress = address,
            paymentMethod = "Cash",
            status = status,
            createdAt = createdAt,
            totalAmount = 100000.0,
            items = listOf(
                OrderItemResponse(
                    productId = "TEST001",
                    quantity = 1,
                    productName = "Test Product",
                    unitPrice = 100000.0
                )
            )
        )
    }
}