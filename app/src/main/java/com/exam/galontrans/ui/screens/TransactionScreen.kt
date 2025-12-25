package com.exam.galontrans.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.exam.galontrans.data.model.Transaction
import com.exam.galontrans.ui.GalonUiState
import com.exam.galontrans.ui.GalonViewModel
import com.exam.galontrans.ui.theme.*

@Composable
fun TransactionScreen(viewModel: GalonViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState
    
    TransactionScreenContent(
        uiState = uiState,
        formattedTotalRevenue = viewModel.getFormattedTotalRevenue(),
        onRefresh = { viewModel.loadTransactions() },
        onDeleteTransaction = { viewModel.deleteTransaction(it) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreenContent(
    uiState: GalonUiState,
    formattedTotalRevenue: String,
    onRefresh: () -> Unit,
    onDeleteTransaction: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val refreshState = rememberPullToRefreshState()

    Column(modifier = modifier.fillMaxSize()) {
        // Total Revenue Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF2196F3), Color(0xFF243484))
                        )
                    )
                    .padding(24.dp)
            ) {
                Text(
                    text = "TOTAL PEMBELIAN",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formattedTotalRevenue,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Transactions List
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh,
            state = refreshState,
            modifier = Modifier.weight(1f)
        ) {
            if (uiState.transactions.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.transactions) { transaction ->
                        TransactionCard(
                            transaction = transaction,
                            onDelete = { onDeleteTransaction(transaction.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = transaction.getImageUrl(),
                contentDescription = transaction.productName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Transaction Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.productName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${transaction.quantity}x @ ${transaction.getFormattedPrice()}",
                    fontSize = 14.sp,
                    color = Gray700
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.getFormattedTotal(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue600
                )
            }

            // Delete Button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Blue600
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    GalonTransTheme {
        TransactionScreenContent(
            uiState = GalonUiState(
                transactions = listOf(
                    Transaction(1, 1, 2, "Aqua Galon", 20000.0, ""),
                    Transaction(2, 2, 1, "Le Minerale", 22000.0, "")
                )
            ),
            formattedTotalRevenue = "Rp 62.000",
            onRefresh = {},
            onDeleteTransaction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionCardPreview() {
    GalonTransTheme {
        TransactionCard(
            transaction = Transaction(1, 1, 2, "Aqua Galon", 20000.0, ""),
            onDelete = {}
        )
    }
}