package com.vitairos.mobile.ui.registro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.vitairos.mobile.model.RegistroEmocional

class RegistroViewModel : ViewModel() {
    var uiState by mutableStateOf(RegistroUiState())
        private set

    fun seleccionarEmocion(emoji: String, emocion: String, nivel: Int) {
        val esCrisis = emocion in listOf("Triste", "Enojado", "Ansioso") || verificarPalabrasClaveCrisis(uiState.nota)
        val consejo = obtenerConsejoPorEmocion(emocion)

        uiState = uiState.copy(
            emojiSeleccionado = emoji,
            emocionSeleccionada = emocion,
            nivelAnimo = nivel,
            consejoGenerado = consejo,
            mostrarProtocoloCrisis = esCrisis
        )
    }

    fun onNotaChanged(nuevaNota: String) {
        val esCrisis = uiState.emocionSeleccionada in listOf("Triste", "Enojado", "Ansioso") || verificarPalabrasClaveCrisis(nuevaNota)
        uiState = uiState.copy(
            nota = nuevaNota,
            errorNota = null,
            mostrarProtocoloCrisis = esCrisis
        )
    }

    private fun verificarPalabrasClaveCrisis(texto: String): Boolean {
        val palabrasCrisis = listOf("matar", "matarme", "morir", "suicidio", "no quiero vivir", "ayuda", "desaparecer")
        return palabrasCrisis.any { texto.lowercase().contains(it) }
    }

    private fun obtenerConsejoPorEmocion(emocion: String): String {
        return when (emocion) {
            "Triste" -> "Recuerda darte un respiro. Un paso a la vez, no estás solo y es válido sentirse así."
            "Ansioso" -> "Tómate 2 minutos para respirar profundo: inhala en 4 segundos, mantén 4 y exhala en 4."
            "Enojado" -> "Intenta pausar un momento antes de responder. Dar una caminata corta o escuchar música ayuda."
            "Motivado" -> "¡Excelente momento! Canaliza esta gran energía en tus proyectos o metas importantes."
            else -> "Aprovecha esta buena energía para conectar con tus seres queridos y disfrutar tu día."
        }
    }

    fun validarYGuardar(): RegistroEmocional? {
        if (uiState.nota.isBlank()) {
            uiState = uiState.copy(errorNota = "Por favor escribe un detalle de cómo te sientes.")
            return null
        }
        val nuevoRegistro = RegistroEmocional(
            emoji = uiState.emojiSeleccionado,
            emocion = uiState.emocionSeleccionada,
            nivelAnimo = uiState.nivelAnimo,
            nota = uiState.nota,
            consejo = uiState.consejoGenerado
        )
        uiState = uiState.copy(esExitoso = true)
        return nuevoRegistro
    }
}