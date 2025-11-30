package com.example.levelupmobile.vm

import androidx.lifecycle.SavedStateHandle
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.nav.Routes
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModelTest {

    private lateinit var repo: ShopRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: ProductDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repo = mockk(relaxed = true)
        savedStateHandle = mockk(relaxed = true)

        every { savedStateHandle.get<String>(Routes.ProductDetail.ARG) } returns "1"

        viewModel = ProductDetailViewModel(repo, savedStateHandle)
    }

    @Test
    fun `addToCart llama al repositorio con el id correcto`() = runTest {
        viewModel.addToCart()
        coVerify { repo.addToCart("1", 1) }
    }
}