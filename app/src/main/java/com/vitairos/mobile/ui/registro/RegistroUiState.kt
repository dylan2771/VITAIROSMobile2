package com.vitairos.mobile.ui.registro

data class RegistroUiState(
    val nota: String = "",
    val errorNota: String? = null,
    val esExitoso: Boolean = false
)