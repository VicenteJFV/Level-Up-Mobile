package com.example.levelupmobile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupmobile.data.dto.OrderResponse
import com.example.levelupmobile.domain.repo.ShopRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OrderDetailUi(
    val order: OrderResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val canEdit: Boolean = false,
    val canConfirm: Boolean = false,
    val canCancel: Boolean = false,
    val hoursRemaining: Long = 0,
    val isEditing: Boolean = false,
    val editPhone: String = "",
    val editAddress: String = ""
)

sealed interface OrderEvent {
    data object OrderCancelled : OrderEvent
    data object OrderConfirmed : OrderEvent
    data object OrderUpdated : OrderEvent
    data class Error(val message: String) : OrderEvent
}

class OrderViewModel(
    private val repo: ShopRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(OrderDetailUi())
    val ui: StateFlow<OrderDetailUi> = _ui.asStateFlow()

    private val _events = MutableSharedFlow<OrderEvent>()
    val events: SharedFlow<OrderEvent> = _events.asSharedFlow()

    fun searchOrder(orderId: Long) = viewModelScope.launch {
        _ui.update { it.copy(isLoading = true, error = null) }

        val order = repo.getOrder(orderId)

        if (order == null) {
            _ui.update {
                it.copy(
                    isLoading = false,
                    error = "Orden no encontrada"
                )
            }
            return@launch
        }

        val hoursRemaining = calculateHoursRemaining(order.createdAt)
        val isCreated = order.status == "CREATED"
        val withinTime = hoursRemaining > 0

        _ui.update {
            it.copy(
                order = order,
                isLoading = false,
                error = null,
                canEdit = isCreated && withinTime,
                canConfirm = isCreated && withinTime,
                canCancel = isCreated && withinTime,
                hoursRemaining = hoursRemaining,
                editPhone = order.customerPhone,
                editAddress = order.deliveryAddress
            )
        }
    }

    fun startEditing() {
        _ui.update { it.copy(isEditing = true) }
    }

    fun cancelEditing() {
        val order = _ui.value.order ?: return
        _ui.update {
            it.copy(
                isEditing = false,
                editPhone = order.customerPhone,
                editAddress = order.deliveryAddress
            )
        }
    }

    fun onPhoneChange(phone: String) {
        _ui.update { it.copy(editPhone = phone) }
    }

    fun onAddressChange(address: String) {
        _ui.update { it.copy(editAddress = address) }
    }

    fun saveChanges() = viewModelScope.launch {
        val orderId = _ui.value.order?.id ?: return@launch
        val phone = _ui.value.editPhone
        val address = _ui.value.editAddress

        _ui.update { it.copy(isLoading = true) }

        val updated = repo.updateOrder(orderId, phone, address)

        if (updated != null) {
            _ui.update {
                it.copy(
                    order = updated,
                    isLoading = false,
                    isEditing = false,
                    editPhone = updated.customerPhone,
                    editAddress = updated.deliveryAddress
                )
            }
            _events.emit(OrderEvent.OrderUpdated)
        } else {
            _ui.update { it.copy(isLoading = false) }
            _events.emit(OrderEvent.Error("No se pudo actualizar la orden"))
        }
    }

    fun confirmOrder() = viewModelScope.launch {
        val orderId = _ui.value.order?.id ?: return@launch

        _ui.update { it.copy(isLoading = true) }

        val confirmed = repo.confirmOrder(orderId)

        if (confirmed != null) {
            _ui.update {
                it.copy(
                    order = confirmed,
                    isLoading = false,
                    canEdit = false,
                    canConfirm = false,
                    canCancel = false
                )
            }
            _events.emit(OrderEvent.OrderConfirmed)
        } else {
            _ui.update { it.copy(isLoading = false) }
            _events.emit(OrderEvent.Error("No se pudo confirmar la orden"))
        }
    }

    fun cancelOrder() = viewModelScope.launch {
        val orderId = _ui.value.order?.id ?: return@launch

        _ui.update { it.copy(isLoading = true) }

        val success = repo.cancelOrder(orderId)

        if (success) {
            _ui.update { it.copy(isLoading = false) }
            _events.emit(OrderEvent.OrderCancelled)
        } else {
            _ui.update { it.copy(isLoading = false) }
            _events.emit(OrderEvent.Error("No se pudo cancelar la orden"))
        }
    }

    private fun calculateHoursRemaining(createdAtStr: String): Long {
        return try {
            // Parsear la fecha ISO 8601: "2025-11-28T12:35:20.083947"
            // Formato: yyyy-MM-ddTHH:mm:ss.SSSSSS
            val parts = createdAtStr.split("T")
            if (parts.size != 2) return 0L

            val dateParts = parts[0].split("-")
            val timeParts = parts[1].split(":")

            if (dateParts.size != 3 || timeParts.size < 3) return 0L

            val year = dateParts[0].toInt()
            val month = dateParts[1].toInt()
            val day = dateParts[2].toInt()
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()
            val second = timeParts[2].split(".")[0].toInt()

            // Crear calendar para la fecha de creación
            val createdCalendar = java.util.Calendar.getInstance().apply {
                set(year, month - 1, day, hour, minute, second)
            }

            // Fecha actual
            val nowCalendar = java.util.Calendar.getInstance()

            // Calcular diferencia en milisegundos
            val diffMillis = nowCalendar.timeInMillis - createdCalendar.timeInMillis
            val hoursPassed = diffMillis / (1000 * 60 * 60)

            (24 - hoursPassed).coerceAtLeast(0)
        } catch (e: Exception) {
            0L
        }
    }
}