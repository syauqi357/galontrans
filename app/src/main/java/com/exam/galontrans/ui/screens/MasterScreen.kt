package com.exam.galontrans.ui.screens

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.exam.galontrans.data.model.Product
import com.exam.galontrans.ui.GalonViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterScreen(
    viewModel: GalonViewModel,
    onNavigateToTransaction: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Master Produk") },
                actions = {
                    Button(onClick = onNavigateToTransaction) {
                        Text("Transaksi")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingProduct = null
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Produk")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        errorMessage?.let { message ->
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(message)
                viewModel.clearError()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading && products.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (products.isEmpty()) {
                Text(
                    text = "Belum ada produk. Tambah produk dulu!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                ProductList(
                    products = products,
                    onEdit = { product ->
                        editingProduct = product
                        showDialog = true
                    },
                    onDelete = { product ->
                        viewModel.deleteProduct(product.id) { message ->
                            scope.launch { snackbarHostState.showSnackbar(message) }
                        }
                    }
                )
            }
        }
    }

    if (showDialog) {
        ProductDialog(
            product = editingProduct,
            onDismiss = { showDialog = false },
            onSave = { name, price ->
                if (editingProduct == null) {
                    viewModel.addProduct(name, price) { message ->
                        scope.launch { snackbarHostState.showSnackbar(message) }
                    }
                } else {
                    editingProduct?.let { prod ->
                        viewModel.updateProduct(prod.id, name, price) { message ->
                            scope.launch { snackbarHostState.showSnackbar(message) }
                        }
                    }
                }
                showDialog = false
            }
        )
    }
}

@Composable
private fun ProductList(
    products: List<Product>,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        items(products, key = { it.id }) { product ->
            ProductItem(
                product = product,
                onEdit = { onEdit(product) },
                onDelete = { onDelete(product) }
            )
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rp ${product.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Produk")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Hapus Produk")
            }
        }
    }
}

@Composable
fun ProductDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    var name by remember(product) { mutableStateOf(product?.name ?: "") }
    var price by remember(product) { mutableStateOf(product?.price?.toString() ?: "") }
    var isError by remember { mutableStateOf(false) }

    fun validate(text: String) {
        isError = text.isNotEmpty() && text.toIntOrNull() == null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Tambah Produk" else "Edit Produk") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Produk") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        price = it
                        validate(it)
                    },
                    label = { Text("Harga") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text(
                                text = "Harga harus berupa angka",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceInt = price.toIntOrNull()
                    if (name.isNotBlank() && priceInt != null) {
                        onSave(name, priceInt)
                    }
                },
                enabled = name.isNotBlank() && !isError && price.isNotBlank()
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
