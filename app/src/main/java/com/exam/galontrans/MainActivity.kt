package com.exam.galontrans

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exam.galontrans.data.AppDatabase
import com.exam.galontrans.data.repo.GalonRepository
import com.exam.galontrans.ui.GalonViewModel
import com.exam.galontrans.ui.GalonVmFactory
import com.exam.galontrans.ui.screens.MasterScreen
import com.exam.galontrans.ui.screens.TransactionScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup database and repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = GalonRepository(
            database.productDao(),
            database.transactionDao()
        )
        val viewModelFactory = GalonVmFactory(repository)

        setContent {
            GalonWaterApp(viewModelFactory)
        }
    }
}

@Composable
fun GalonWaterApp(viewModelFactory: GalonVmFactory) {
    val navController = rememberNavController()
    val viewModel: GalonViewModel = viewModel(factory = viewModelFactory)

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
                onBack = { navController.popBackStack() }
            )
        }
    }
}
