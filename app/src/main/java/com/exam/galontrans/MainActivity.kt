package com.exam.galontrans

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exam.galontrans.data.repo.GalonRepository
import com.exam.galontrans.ui.GalonViewModel
import com.exam.galontrans.ui.GalonVmFactory
import com.exam.galontrans.ui.screens.MasterScreen
import com.exam.galontrans.ui.screens.SalesScreen
import com.exam.galontrans.ui.screens.TransactionScreen
import com.exam.galontrans.ui.theme.GalonTransTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GalonTransTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val repository = GalonRepository()
                    val viewModel: GalonViewModel = viewModel(
                        factory = GalonVmFactory(repository)
                    )

                    MasterScreen(viewModel = viewModel)
                }
            }
        }
    }
}
