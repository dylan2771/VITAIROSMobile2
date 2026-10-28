package com.vitairos.mobile.ui.registro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitairos.mobile.data.NotificationHelper
import com.vitairos.mobile.data.PreferenciasManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistroViewModel = viewModel()
) {
    val state = viewModel.uiState
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val notificationHelper = remember { NotificationHelper(context) }
    val prefsManager = remember { PreferenciasManager(context) }

    val emociones = listOf(
        Triple("😊", "Feliz", 5),
        Triple("⚡", "Motivado", 4),
        Triple("😰", "Ansioso", 3),
        Triple("😔", "Triste", 2),
        Triple("😡", "Enojado", 1)
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            notificationHelper.showNotification("VITAIROS-M", "¡Registro de estado '${state.emocionSeleccionada}' guardado!")
        }
    }

    fun ejecutarVibracion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(150)
        }
    }

    // Fondo degradado elegante traslúcido
    val fondoGradiente = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.background.copy(alpha = 0.98f)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Registro Emocional", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.background(brush = fondoGradiente)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    keyboardController?.hide()
                }
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "¿Cómo te sientes en este momento?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Chips con Scroll Horizontal
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(emociones) { (emoji, nombre, nivel) ->
                    val seleccionado = state.emocionSeleccionada == nombre
                    FilterChip(
                        selected = seleccionado,
                        onClick = {
                            keyboardController?.hide()
                            viewModel.seleccionarEmocion(emoji, nombre, nivel)
                        },
                        label = { Text("$emoji $nombre", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Alerta de Apoyo Inmediato si detecta crisis
            AnimatedVisibility(visible = state.mostrarProtocoloCrisis) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Línea de Apoyo y Prevención (24/7)",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "No estás solo/a. Si sientes que no puedes más o necesitas apoyo urgente, existen profesionales disponibles para escucharte de forma gratuita y confidencial.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:4141"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Llamar Gratis al *4141 (Salud Responde)")
                        }
                    }
                }
            }

            // Tarjeta de Orientación
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Orientación: ${state.emocionSeleccionada} ${state.emojiSeleccionado}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(state.consejoGenerado, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = state.nota,
                onValueChange = { viewModel.onNotaChanged(it) },
                label = { Text("Escribe un detalle de lo que estás viviendo...") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                trailingIcon = {
                    if (state.errorNota != null) Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                },
                isError = state.errorNota != null,
                supportingText = { state.errorNota?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    val registro = viewModel.validarYGuardar()
                    if (registro != null) {
                        prefsManager.guardarRegistro(registro)
                        ejecutarVibracion()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            notificationHelper.showNotification("VITAIROS-M", "¡Registro guardado correctamente!")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar y Recibir Acompañamiento", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            AnimatedVisibility(visible = state.esExitoso, enter = fadeIn(), exit = fadeOut()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("¡Registro de acompañamiento guardado con éxito!", fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}