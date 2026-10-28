package com.vitairos.mobile.ui.registro

data class RegistroUiState(
    val emojiSeleccionado: String = "😊",
    val emocionSeleccionada: String = "Feliz",
    val nivelAnimo: Int = 5,
    val nota: String = "",
    val consejoGenerado: String = "Aprovecha esta buena energía para avanzar en tus metas.",
    val mostrarProtocoloCrisis: Boolean = false,
    val errorNota: String? = null,
    val esExitoso: Boolean = false
)