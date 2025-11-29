package com.example.levelupmobile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.domain.model.CheckoutForm
import com.example.levelupmobile.domain.repo.ShopRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CheckoutUi(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val delivery: String = "Retiro en tienda",
    val payment: String = "Efectivo",
    val errors: Map<String, String> = emptyMap(),
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false
)

sealed interface CheckoutEvent {
    data class Success(val orderId: Long) : CheckoutEvent
    data class Error(val message: String) : CheckoutEvent
}

data class DeliveryLocation(val lat: Double, val lng: Double)

class CheckoutViewModel(
    private val repo: ShopRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(CheckoutUi())
    val ui: StateFlow<CheckoutUi> = _ui.asStateFlow()

    private val _events = MutableSharedFlow<CheckoutEvent>()
    val events: SharedFlow<CheckoutEvent> = _events.asSharedFlow()

    private val _location = MutableStateFlow<DeliveryLocation?>(null)
    val location: StateFlow<DeliveryLocation?> = _location

    // ---- inputs ----
    fun onName(v: String) = update { copy(name = v) }.revalidate()
    fun onPhone(v: String) = update { copy(phone = v) }.revalidate()
    fun onAddress(v: String) = update { copy(address = v) }.revalidate()
    fun onDelivery(v: String) = update { copy(delivery = v) }.revalidate()
    fun onPayment(v: String) = update { copy(payment = v) }.revalidate()

    fun setLocation(lat: Double, lng: Double) {
        _location.value = DeliveryLocation(lat, lng)
    }

    // ---- acción principal ----
    fun submit() = viewModelScope.launch {
        val form = currentFormOrNull() ?: return@launch
        _ui.update { it.copy(isSubmitting = true) }

        runCatching { repo.checkout(form) }
            .onSuccess { summary ->
                _ui.update { it.copy(isSubmitting = false) }
                // Emite solo el orderId para navegar a OrderSuccessScreen
                _events.emit(CheckoutEvent.Success(summary.orderId))
            }
            .onFailure { e ->
                _ui.update { it.copy(isSubmitting = false) }
                _events.emit(CheckoutEvent.Error(e.message ?: "Error al procesar el pago"))
            }
    }

    // ---- helpers ----
    private fun update(block: CheckoutUi.() -> CheckoutUi): CheckoutViewModel {
        _ui.update { it.block() }
        return this
    }

    private fun CheckoutViewModel.revalidate(): CheckoutViewModel {
        _ui.update { it.validate() }
        return this
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