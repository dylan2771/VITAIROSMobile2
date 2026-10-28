package com.vitairos.mobile.data

import android.content.Context
import android.content.SharedPreferences
import com.vitairos.mobile.model.RegistroEmocional
import org.json.JSONArray
import org.json.JSONObject

class PreferenciasManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("vitairos_prefs", Context.MODE_PRIVATE)

    fun guardarRegistro(registro: RegistroEmocional) {
        val lista = obtenerRegistros().toMutableList()
        lista.add(0, registro) // Agregar al inicio (más reciente primero)
        guardarLista(lista)
    }

    fun borrarRegistro(id: Long) {
        val lista = obtenerRegistros().filter { it.id != id }
        guardarLista(lista)
    }

    fun limpiarTodo() {
        prefs.edit().remove("KEY_HISTORIAL_JSON").apply()
    }

    fun obtenerRegistros(): List<RegistroEmocional> {
        val jsonString = prefs.getString("KEY_HISTORIAL_JSON", null) ?: return emptyList()
        val lista = mutableListOf<RegistroEmocional>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                lista.add(
                    RegistroEmocional(
                        id = obj.getLong("id"),
                        emoji = obj.getString("emoji"),
                        emocion = obj.getString("emocion"),
                        nivelAnimo = obj.getInt("nivelAnimo"),
                        nota = obj.getString("nota"),
                        consejo = obj.getString("consejo"),
                        fechaTimestamp = obj.getLong("fechaTimestamp")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lista
    }

    private fun guardarLista(lista: List<RegistroEmocional>) {
        val jsonArray = JSONArray()
        lista.forEach { reg ->
            val obj = JSONObject().apply {
                put("id", reg.id)
                put("emoji", reg.emoji)
                put("emocion", reg.emocion)
                put("nivelAnimo", reg.nivelAnimo)
                put("nota", reg.nota)
                put("consejo", reg.consejo)
                put("fechaTimestamp", reg.fechaTimestamp)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("KEY_HISTORIAL_JSON", jsonArray.toString()).apply()
    }
}