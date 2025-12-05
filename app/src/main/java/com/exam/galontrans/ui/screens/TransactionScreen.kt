package com.exam.galontrans.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
    onBack: () -> Unit,
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

//    pop up dialog to allow
    var showDialog by remember { mutableStateOf(false) }

//    topappbar
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Always load products when this screen is shown
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

//    navbar
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {

//            top site content : back button, button to access laporan on line :
            TopAppBar(
                title = { Text("Transaksi Penjualan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {

//                    laporan button accessible on code line :
                    Button(onClick = onNavigateToSales) {
                        Text("Laporan")
                    }

//                    set scroll behavior
                },
                scrollBehavior = scrollBehavior
            )
        },

//        floating button accessing dialog on line : 81
        floatingActionButton = {
            FloatingActionButton(
//                variable : showDialog on 81 declaration
                onClick = { showDialog = true },
//                idk
//                enabled = products.isNotEmpty()
            ) {
//                icon element for FAB
                Icon(Icons.Filled.Add, contentDescription = "Tambah Transaksi")
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading && transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Belum ada transaksi",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    item {
                        TransactionSummary(
                            totalSales = totalSales,
                            transactionCount = transactionCount,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(transactions, key = { it.id }) { transaction ->
                        TransactionItem(transaction)
                    }
                }
            }
        }
    }

    if (showDialog) {
        key(showDialog) {
            TransactionDialog(
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
fun TransactionSummary(totalSales: Int, transactionCount: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Penjualan", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rp $totalSales",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Jumlah Transaksi", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$transactionCount",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
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
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = transaction.productName ?: "[Produk Dihapus]",
                style = MaterialTheme.typography.titleLarge,
                color = if (isProductDeleted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "${transaction.quantity} x Rp ${transaction.productPrice ?: 0}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Rp ${transaction.totalPrice}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = transaction.date ?:"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDialog(
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
        isError = text.isNotEmpty() && text.toIntOrNull() == null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Transaksi") },
// TO THIS (A more robust structure):
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // First, check if there are products. This is the primary success case.
                if (products.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,

//                        this shit is false logic
//                        fuck hancok
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedProduct?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Produk") },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            products.forEach { product ->
                                DropdownMenuItem(
                                    text = { Text("${product.name} - Rp ${product.price}") },
                                    onClick = {
                                        selectedProduct = product
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                        supportingText = {
                            if (isError) {
                                Text(
                                    text = "Jumlah harus berupa angka",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    val total = selectedProduct?.price?.let { price ->
                        quantity.toIntOrNull()?.let { qty ->
                            price * qty
                        }
                    }
                    if (total != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Total: Rp $total",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                    // If there are no products, check if we are still loading.
                } else if (isLoading) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Memuat produk...")
                    // If not loading and still no products, then it's truly empty.
                } else {
                    Text("Tidak ada produk tersedia. Silakan tambah produk terlebih dahulu di halaman Master.")
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
                enabled = selectedProduct != null && quantity.isNotBlank() && !isError && !isLoading
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
