package com.example.levelupmobile.vm

import com.example.levelupmobile.domain.repo.ShopRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest {

    private lateinit var repo: ShopRepository
    private lateinit var viewModel: CheckoutViewModel

    @Before
    fun setup() {
        // Dispatcher de prueba para viewModelScope
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repo = mockk(relaxed = true)
        viewModel = CheckoutViewModel(repo)
    }

    @Test
    fun `form con campos vacios es invalido`() = runBlocking {
        // When
        viewModel.onName("")
        viewModel.onPhone("123")      // menos de 9 dígitos
        viewModel.onAddress("")

        val ui = viewModel.ui.value

        // Then
        assertFalse(ui.canSubmit)
        assertEquals("Requerido", ui.errors["name"])
        assertEquals("Teléfono inválido", ui.errors["phone"])
        assertEquals("Requerido", ui.errors["address"])
    }

    @Test
    fun `form valido no tiene errores y puede enviarse`() = runBlocking {
        viewModel.onName("Felipe")
        viewModel.onPhone("987654321")   // 9 dígitos
        viewModel.onAddress("Calle 123")

        val ui = viewModel.ui.value

        assertTrue(ui.errors.isEmpty())
        assertTrue(ui.canSubmit)
    }

    @Test
    fun `submit con form invalido no llama a checkout`() = runBlocking {
        viewModel.onName("")
        viewModel.onPhone("123")
        viewModel.onAddress("")

        viewModel.submit()

        coVerify(exactly = 0) { repo.checkout(any()) }
    }

    @Test
    fun `submit con error en repo igual llama a checkout`() = runBlocking {
        // Given: form válido
        viewModel.onName("Felipe")
        viewModel.onPhone("987654321")
        viewModel.onAddress("Calle 123")

        // El backend falla
        coEvery { repo.checkout(any()) } throws RuntimeException("Network error")

        // When: llamamos a submit (la corrutina de viewModelScope se ejecuta con Main = Unconfined)
        viewModel.submit()

        // Then: al menos verificamos que sí se intentó llamar al repositorio
        coVerify(exactly = 1) { repo.checkout(any()) }
    }
}
