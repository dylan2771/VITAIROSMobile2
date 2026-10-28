package com.vitairos.mobile.data

import android.content.Context
import android.content.SharedPreferences

class PreferenciasManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("vitairos_prefs", Context.MODE_PRIVATE)

    fun guardarUsuario(nombre: String) {
        prefs.edit().putString("KEY_NOMBRE", nombre).apply()
    }
}
