package com.vitairos.mobile.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitairos.mobile.data.PreferenciasManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRegistro: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefsManager = remember { PreferenciasManager(context) }
    var listaRegistros by remember { mutableStateOf(prefsManager.obtenerRegistros()) }

    val totalRegistros = listaRegistros.size
    val ahora = System.currentTimeMillis()
    val unDiaMs = 86400000L

    val registrosSemana = listaRegistros.filter { (ahora - it.fechaTimestamp) <= (7 * unDiaMs) }
    val registrosMes = listaRegistros.filter { (ahora - it.fechaTimestamp) <= (30 * unDiaMs) }

    val promSemanaNum = if (registrosSemana.isNotEmpty()) registrosSemana.map { it.nivelAnimo }.average() else 0.0
    val promSemana = if (registrosSemana.isNotEmpty()) String.format("%.1f", promSemanaNum) else "N/A"
    val promMes = if (registrosMes.isNotEmpty()) String.format("%.1f", registrosMes.map { it.nivelAnimo }.average()) else "N/A"

    val fondoGradiente = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.background.copy(alpha = 0.98f)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = "Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("VITAIROS-M", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:4141"))
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Línea de Crisis", tint = MaterialTheme.colorScheme.error)
                    }
                    if (listaRegistros.isNotEmpty()) {
                        IconButton(onClick = {
                            prefsManager.limpiarTodo()
                            listaRegistros = emptyList()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar todo", tint = MaterialTheme.colorScheme.outline)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToRegistro,
                icon = { Icon(Icons.Default.Add, contentDescription = "Nuevo") },
                text = { Text("Nuevo Registro", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.background(brush = fondoGradiente)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Dashboard Métricas
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Métricas de Estado Emocional", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Registros", style = MaterialTheme.typography.bodySmall)
                                Text("$totalRegistros", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Prom. Semanal", style = MaterialTheme.typography.bodySmall)
                                Text(promSemana, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Prom. Mensual", style = MaterialTheme.typography.bodySmall)
                                Text(promMes, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Menú de Opciones Adaptativas
            item {
                Text(
                    text = "Opciones y Herramientas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Botón para Compartir Informe
                    OutlinedCard(
                        onClick = {
                            val textoInforme = "Resumen de Bienestar VITAIROS-M:\nTotal Registros: $totalRegistros\nPromedio Semanal: $promSemana / 5.0\nÚltimo estado registrado: ${listaRegistros.firstOrNull()?.emocion ?: "Sin registros"}"
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, textoInforme)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Compartir informe emocional"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Compartir Datos", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Botón de Guía / Ayuda Rápida
                    OutlinedCard(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://saludresponde.minsal.cl/"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Portal Saludable", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Recomendación Profesional según Promedio Emocional
            item {
                val (profesional, descripcion) = when {
                    promSemanaNum == 0.0 -> "Monitoreo Inicial" to "Realiza tus primeros registros para obtener una recomendación adaptada a tu perfil."
                    promSemanaNum < 2.5 -> "Psicología Clínica / Psiquiatría" to "Tu promedio semanal refleja un nivel de malestar elevado. Te sugerimos acudir a un/a Psicólogo/a para acompañamiento emocional o evaluar apoyo médico especializado."
                    promSemanaNum < 3.8 -> "Acompañamiento / Terapia Breve" to "Presentas fluctuaciones moderadas. Te recomendamos consultar un/a Psicólogo/a o practicar actividades de mindfulness y regulación emocional."
                    else -> "Mantenimiento / Bienestar" to "Tus índices muestran un estado estable. Te sugerimos mantener tus hábitos de autocuidado, deporte y conexión social."
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Orientación Recomendada:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                            Text(profesional, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(descripcion, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Historial de Acompañamiento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (listaRegistros.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Aún no has guardado registros. Toca el botón de abajo para comenzar tu acompañamiento.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(listaRegistros, key = { it.id }) { registro ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = registro.emoji, fontSize = 30.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = registro.emocion, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "Nº Reg: #${registro.id.toString().takeLast(4)} • ${registro.fechaFormateada()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                                IconButton(onClick = {
                                    prefsManager.borrarRegistro(registro.id)
                                    listaRegistros = prefsManager.obtenerRegistros()
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "\"${registro.nota}\"", style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.tertiary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = registro.consejo, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}