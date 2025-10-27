package com.example.levelupmobile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.domain.model.CheckoutForm
import com.example.levelupmobile.domain.model.OrderSummary
import com.example.levelupmobile.domain.repo.ShopRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CheckoutUi(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val delivery: String = "Retiro en tienda",   // o "Envío a domicilio"
    val payment: String = "Efectivo",            // o "Tarjeta / Transferencia"
    val errors: Map<String, String> = emptyMap(),
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val result: OrderSummary? = null
)

sealed interface CheckoutEvent {
    data class Success(val summary: OrderSummary) : CheckoutEvent
    data class Error(val message: String) : CheckoutEvent
}

class CheckoutViewModel(
    private val repo: ShopRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(CheckoutUi())
    val ui: StateFlow<CheckoutUi> = _ui.asStateFlow()

    private val _events = MutableSharedFlow<CheckoutEvent>()
    val events: SharedFlow<CheckoutEvent> = _events.asSharedFlow()

    // ---- inputs ----
    fun onName(v: String)    = update { copy(name = v) }.revalidate()
    fun onPhone(v: String)   = update { copy(phone = v) }.revalidate()
    fun onAddress(v: String) = update { copy(address = v) }.revalidate()
    fun onDelivery(v: String)= update { copy(delivery = v) }.revalidate()
    fun onPayment(v: String) = update { copy(payment = v) }.revalidate()

    // ---- acción principal ----
    fun submit() = viewModelScope.launch {
        val form = currentFormOrNull() ?: return@launch
        _ui.update { it.copy(isSubmitting = true) }

        runCatching { repo.checkout(form) }
            .onSuccess { summary ->
                _ui.update { it.copy(isSubmitting = false, result = summary) }
                _events.emit(CheckoutEvent.Success(summary))
            }
            .onFailure { e ->
                _ui.update { it.copy(isSubmitting = false) }
                _events.emit(CheckoutEvent.Error(e.message ?: "Error al procesar el pago"))
            }
    }

    // ---- helpers ----
    private fun update(block: CheckoutUi.() -> CheckoutUi): CheckoutViewModel {
        _ui.update { it.block() }; return this
    }
    private fun CheckoutViewModel.revalidate(): CheckoutViewModel {
        _ui.update { it.validate() }; return this
    }

    private fun CheckoutUi.validate(): CheckoutUi {
        val errs = buildMap {
            if (name.isBlank()) put("name", "Requerido")
            if (phone.length != 9) put("phone", "Teléfono inválido")
            if (address.isBlank()) put("address", "Requerido")
        }
        return copy(errors = errs, canSubmit = errs.isEmpty())
    }

    private fun currentFormOrNull(): CheckoutForm? {
        val s = _ui.value.validate()
        _ui.value = s
        return if (s.canSubmit) {
            CheckoutForm(
                name = s.name,
                phone = s.phone,
                address = s.address,
                deliveryMethod = s.delivery,
                paymentMethod = s.payment
            )
        } else null
    }
}
