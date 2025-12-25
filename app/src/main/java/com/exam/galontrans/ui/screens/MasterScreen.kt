package com.exam.galontrans.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.exam.galontrans.ui.GalonViewModel
import com.exam.galontrans.ui.theme.Blue50
import com.exam.galontrans.ui.theme.Blue600

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Sales : Screen("sales", "Sales", Icons.Default.ShoppingCart)
    object Transactions : Screen("transactions", "Transactions", Icons.Default.Receipt)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterScreen(viewModel: GalonViewModel) {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Sales) }
    val uiState by viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle success messages
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    // Handle errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedScreen.title,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue600,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Blue600
            ) {
                NavigationBarItem(
                    icon = { Icon(Screen.Sales.icon, contentDescription = Screen.Sales.title) },
                    label = { Text(Screen.Sales.title) },
                    selected = selectedScreen == Screen.Sales,
                    onClick = { selectedScreen = Screen.Sales },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Blue600,
                        selectedTextColor = Blue600,
                        indicatorColor = Blue50
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Screen.Transactions.icon, contentDescription = Screen.Transactions.title) },
                    label = { Text(Screen.Transactions.title) },
                    selected = selectedScreen == Screen.Transactions,
                    onClick = { selectedScreen = Screen.Transactions },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Blue600,
                        selectedTextColor = Blue600,
                        indicatorColor = Blue50
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (selectedScreen) {
            Screen.Sales -> SalesScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues)
            )
            Screen.Transactions -> TransactionScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}