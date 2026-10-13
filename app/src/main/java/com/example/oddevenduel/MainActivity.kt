package com.example.oddevenduel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.oddevenduel.ui.OddEvenViewModel
import com.example.oddevenduel.ui.navigation.AppNavigation
import com.example.oddevenduel.ui.theme.OddEvenDuelTheme

class MainActivity : ComponentActivity() {

    private val viewModel: OddEvenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsState()

            OddEvenDuelTheme(
                appTheme = settings.theme,
                dynamicColor = settings.dynamicColor
            ) {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}