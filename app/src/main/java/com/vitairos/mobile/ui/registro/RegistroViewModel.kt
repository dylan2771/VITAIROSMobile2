package com.vitairos.mobile.ui.registro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegistroViewModel : ViewModel() {
    var uiState by mutableStateOf(RegistroUiState())
        private set

    fun onNotaChanged(nuevaNota: String) {
        uiState = uiState.copy(nota = nuevaNota, errorNota = null)
    }

    fun validarYGuardar(): Boolean {
        if (uiState.nota.isBlank()) {
            uiState = uiState.copy(errorNota = "La nota no puede estar vacía")
            return false
        }
        uiState = uiState.copy(esExitoso = true)
        return true
    }
}