package com.exam.galontrans.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.exam.galontrans.data.model.Product
import com.exam.galontrans.data.model.Transaction
import com.exam.galontrans.ui.GalonViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable

//screen element
fun TransactionScreen(
    viewModel: GalonViewModel,
    onNavigateToSales: () -> Unit
) {

//    value of items
    val products by viewModel.products.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val totalSales by viewModel.totalSales.collectAsState()
    val transactionCount by viewModel.transactionCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

//    snackbar as response
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

//    pop up dialog to allow
    var showDialog by remember { mutableStateOf(false) }

    // Always load products when this screen is shown
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
        viewModel.loadTransactions()
    }

//    navbar
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {

//            top site content : back button, button to access laporan on line :
            TopAppBar(
                title = { Text("Riwayat Pembelian") },
                actions = {
                    IconButton(onClick = {
                        viewModel.loadProducts()
                        viewModel.loadTransactions()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }

//                    set scroll behavior
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Beli Galon")
            }
        },

//        snackbar host
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        errorMessage?.let { message ->
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(message)
                viewModel.clearError()
            }
        }

        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = {
                viewModel.loadProducts()
                viewModel.loadTransactions()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading && transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Belum ada riwayat pembelian",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tekan tombol + untuk membeli galon",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    item {
                        TransactionSummary(
                            totalSales = totalSales,
                            transactionCount = transactionCount,
                            modifier = Modifier.padding(vertical = 12.dp),
                            onClick = onNavigateToSales
                        )
                    }

                    item {
                        Text(
                            text = "Riwayat Pembelian",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(transactions, key = { it.id }) { transaction ->
                        TransactionItem(transaction)
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showDialog) {
        key(showDialog) {
            BuyProductDialog(
                products = products,
                isLoading = isLoading,
                onDismiss = { showDialog = false },
                onSave = { productId, quantity ->
                    viewModel.addTransaction(productId, quantity) { message ->
                        scope.launch { snackbarHostState.showSnackbar(message) }
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun TransactionSummary(
    totalSales: Int,
    transactionCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Belanja",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rp ${"%,d".format(totalSales)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Pembelian",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$transactionCount",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val isProductDeleted = transaction.productName == null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.productName ?: "[Produk Tidak Tersedia]",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isProductDeleted) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Rp ${"%,d".format(transaction.totalPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${transaction.quantity} galon × Rp ${"%,d".format(transaction.productPrice ?: 0)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = transaction.date ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyProductDialog(
    products: List<Product>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSave: (Int, Int) -> Unit
) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    fun validate(text: String) {
        isError = text.isNotEmpty() && (text.toIntOrNull() == null || text.toInt() <= 0)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Beli Galon") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (products.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedProduct?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Produk") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            products.forEach { product ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(product.name, style = MaterialTheme.typography.bodyLarge)
                                            Text(
                                                "Rp ${"%,d".format(product.price)}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedProduct = product
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = quantity,
                        onValueChange = {
                            quantity = it
                            validate(it)
                        },
                        label = { Text("Jumlah Galon") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = isError,
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            if (isError) {
                                Text(
                                    text = "Jumlah harus berupa angka lebih dari 0",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    val total = selectedProduct?.price?.let { price ->
                        quantity.toIntOrNull()?.let { qty ->
                            if (qty > 0) price * qty else null
                        }
                    }

                    if (total != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Bayar:",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Rp ${"%,d".format(total)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                } else if (isLoading) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Memuat produk...")
                } else {
                    Text(
                        "Tidak ada produk tersedia. Silakan hubungi admin.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val quantityInt = quantity.toIntOrNull()
                    if (selectedProduct != null && quantityInt != null && quantityInt > 0) {
                        onSave(selectedProduct!!.id, quantityInt)
                    }
                },
                enabled = selectedProduct != null &&
                        quantity.isNotBlank() &&
                        !isError &&
                        !isLoading &&
                        (quantity.toIntOrNull() ?: 0) > 0
            ) {
                Text("Beli Sekarang")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
