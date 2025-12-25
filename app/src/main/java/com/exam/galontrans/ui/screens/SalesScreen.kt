package com.exam.galontrans.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.exam.galontrans.data.model.Product
import com.exam.galontrans.data.model.StockStatus
import com.exam.galontrans.ui.GalonUiState
import com.exam.galontrans.ui.GalonViewModel
import com.exam.galontrans.ui.theme.*

@Composable
fun SalesScreen(viewModel: GalonViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState
    
    SalesScreenContent(
        uiState = uiState,
        onRefresh = { viewModel.loadProducts() },
        onPurchase = { product, quantity -> viewModel.createTransaction(product.id, quantity) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreenContent(
    uiState: GalonUiState,
    onRefresh: () -> Unit,
    onPurchase: (Product, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val refreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh,
        state = refreshState,
        modifier = modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.products.isEmpty() && !uiState.isLoading) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No products available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                // Product Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.products) { product ->
                        ProductCard(
                            product = product,
                            onBuyClick = { selectedProduct = it }
                        )
                    }
                }
            }
        }
    }

    // Purchase Dialog
    selectedProduct?.let { product ->
        PurchaseDialog(
            product = product,
            onDismiss = { selectedProduct = null },
            onConfirm = { quantity ->
                onPurchase(product, quantity)
                selectedProduct = null
            }
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onBuyClick: (Product) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Product Image
            AsyncImage(
                model = product.getImageUrl(),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .width(135.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Product Name
            Text(
                text = product.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 2,
                minLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Product Price
            Text(
                text = product.getFormattedPrice(),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Blue600
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Stock Badge
            StockBadge(
                stockStatus = product.getStockStatus(),
                stock = product.stock
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Buy Button
            Button(
                onClick = { onBuyClick(product) },
                modifier = Modifier.fillMaxWidth(),
                enabled = product.isAvailable(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue600,
                    disabledContainerColor = Gray300
                ),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text(
                    text = if (product.isAvailable()) "Beli" else "Barangnya Habis :(",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
@Composable
fun StockBadge(stockStatus: StockStatus, stock: Int) {
    val backgroundColor = when (stockStatus) {
        StockStatus.LOW -> Red600
        StockStatus.MEDIUM -> Yellow600
        StockStatus.HIGH -> Green600
    }

    val textColor = when (stockStatus) {
        StockStatus.MEDIUM -> Yellow700
        StockStatus.HIGH -> Green700
        else -> Color.White
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = "Stok: $stock",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
@Composable
fun PurchaseDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    val totalPrice = product.price * quantity

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Beli Produk",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Product Image
                AsyncImage(
                    model = product.getImageUrl(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Product Name
                Text(
                    text = product.name,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Product Price
                Text(
                    text = product.getFormattedPrice(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue600,
                    textAlign = TextAlign.Center
                )

                // Available Stock
                Text(
                    text = "Stok Tersedia: ${product.stock}",
                    fontSize = 14.sp,
                    color = Gray700,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quantity Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Decrease Button
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Gray600,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Quantity Display
                    Text(
                        text = quantity.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.width(60.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Increase Button
                    IconButton(
                        onClick = { if (quantity < product.stock) quantity++ },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Blue600,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total Price
                Text(
                    text = "Total: Rp ${String.format("%,.0f", totalPrice)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    // Confirm Button
                    Button(
                        onClick = { onConfirm(quantity) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("konfirmasi", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SalesScreenPreview() {
    GalonTransTheme {
        SalesScreenContent(
            uiState = GalonUiState(
                products = listOf(
                    Product(1, "Aqua Galon", 20000.0, "", 50),
                    Product(2, "Le Minerale", 22000.0, "", 5),
                    Product(3, "Cleo", 19500.0, "", 0)
                )
            ),
            onRefresh = {},
            onPurchase = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PurchaseDialogPreview() {
    GalonTransTheme {
        PurchaseDialog(
            product = Product(1, "Aqua Galon", 20000.0, "", 50),
            onDismiss = {},
            onConfirm = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    GalonTransTheme {
        ProductCard(
            product = Product(1, "Aqua Galon", 20000.0, "", 50),
            onBuyClick = {}
        )
    }
}