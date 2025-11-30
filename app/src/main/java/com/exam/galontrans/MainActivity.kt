package com.exam.galontrans

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exam.galontrans.ui.GalonViewModel
import com.exam.galontrans.ui.screens.MasterScreen
import com.exam.galontrans.ui.screens.SalesScreen
import com.exam.galontrans.ui.screens.TransactionScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GalonWaterApp()
        }
    }
}

@Composable
fun GalonWaterApp() {
    val navController = rememberNavController()
    val viewModel: GalonViewModel = viewModel()

    NavHost(navController = navController, startDestination = "master") {
        composable("master") {
            MasterScreen(
                viewModel = viewModel,
                onNavigateToTransaction = { navController.navigate("transaction") }
            )
        }
        composable("transaction") {
            TransactionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToSales = { navController.navigate("sales") }
            )
        }
        composable("sales") {
            SalesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
