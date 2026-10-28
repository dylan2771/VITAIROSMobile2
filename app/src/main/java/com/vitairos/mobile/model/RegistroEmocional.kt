package com.vitairos.mobile.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RegistroEmocional(
    val id: Long = System.currentTimeMillis(),
    val emoji: String,
    val emocion: String,
    val nivelAnimo: Int, // 1 (Enojado) a 5 (Motivado/Feliz)
    val nota: String,
    val consejo: String,
    val fechaTimestamp: Long = System.currentTimeMillis()
) {
    fun fechaFormateada(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(fechaTimestamp))
    }
}