package com.vitairos.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vitairos.mobile.ui.home.HomeScreen
import com.vitairos.mobile.ui.registro.RegistroScreen
import com.vitairos.mobile.ui.theme.VITAIROSMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VITAIROSMobileTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("home") {
                        HomeScreen(
                            onNavigateToRegistro = { navController.navigate("registro") }
                        )
                    }
                    composable("registro") {
                        RegistroScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}