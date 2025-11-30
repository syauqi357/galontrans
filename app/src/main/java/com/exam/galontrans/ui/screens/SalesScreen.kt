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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exam.galontrans.ui.GalonViewModel

// Data class to hold aggregated sales data for a product
data class ProductSales(
    val productName: String,
    val totalQuantity: Int,
    val totalRevenue: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    viewModel: GalonViewModel,
    onBack: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val totalSales by viewModel.totalSales.collectAsState()
    val transactionCount by viewModel.transactionCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Calculate sales per product, now safely handling deleted products
    val productSales by remember(transactions) {
        mutableStateOf(
            transactions
                .groupBy { it.productName } // This can result in a null key
                .map { (name, trans) ->
                    ProductSales(
                        productName = name ?: "[Produk Dihapus]", // Handle null name
                        totalQuantity = trans.sumOf { it.quantity },
                        totalRevenue = trans.sumOf { it.totalPrice }
                    )
                }
                .sortedByDescending { it.totalRevenue }
        )
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Laporan Penjualan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
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
                        text = "Belum ada penjualan untuk ditampilkan",
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

                    item {
                        Text(
                            "Rincian per Produk",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }

                    items(productSales) { sale ->
                        ProductSalesItem(sale)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductSalesItem(sale: ProductSales) {
    val isProductDeleted = sale.productName == "[Produk Dihapus]"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sale.productName,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isProductDeleted) MaterialTheme.colorScheme.error else Color.Unspecified
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${sale.totalQuantity} unit terjual",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Rp ${sale.totalRevenue}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
