package br.edu.utfpr.loja_kobweb.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import br.edu.utfpr.loja_kobweb.components.CartItem
import br.edu.utfpr.loja_kobweb.components.ProductItem
import br.edu.utfpr.loja_kobweb.model.Purchase
import br.edu.utfpr.loja_kobweb.store.Store
import kotlinx.coroutines.launch

private enum class PaymentMethod(val label: String) {
    BOLETO("Boleto"),
    PIX("Pix"),
    CREDITO("Cartao de credito")
}

@Composable
fun App() {
    MaterialTheme {
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        var showCart by remember { mutableStateOf(false) }
        var searchText by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("Todas") }
        var onlyInStock by remember { mutableStateOf(false) }

        val maxCatalogPrice = (Store.products.maxOfOrNull { it.price } ?: 0.0).coerceAtLeast(1.0)
        var selectedMaxPrice by remember(maxCatalogPrice) { mutableStateOf(maxCatalogPrice) }

        val categories = remember(Store.products.size) {
            listOf("Todas") + Store.products.map { it.category }.distinct().sorted()
        }

        val filteredProducts = Store.products.filter { product ->
            val categoryMatches = selectedCategory == "Todas" || product.category == selectedCategory
            val nameMatches = product.name.contains(searchText.trim(), ignoreCase = true)
            val priceMatches = product.price <= selectedMaxPrice
            val stockMatches = !onlyInStock || product.stock > 0
            categoryMatches && nameMatches && priceMatches && stockMatches
        }

        val contentItemCount = 4 +
            if (filteredProducts.isEmpty()) 1 else filteredProducts.size +
            if (Store.purchases.isEmpty()) 1 else minOf(5, Store.purchases.size)

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 1200.dp)
                        .padding(top = 72.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
//                    item {
//                        HeroSection(onOpenCart = { showCart = true })
//                    }
                    item {
                        StatsRow()
                    }
                    item {
                        FilterSection(
                            searchText = searchText,
                            onSearchChange = { searchText = it },
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onSelectCategory = { selectedCategory = it },
                            selectedMaxPrice = selectedMaxPrice,
                            maxCatalogPrice = maxCatalogPrice,
                            onPriceChange = { selectedMaxPrice = it },
                            onlyInStock = onlyInStock,
                            onOnlyInStockChange = { onlyInStock = it }
                        )
                    }
                    item {
                        SectionTitle("Produtos")
                    }

                    if (filteredProducts.isEmpty()) {
                        item {
                            EmptyStateCard("Nenhum produto encontrado com os filtros aplicados.")
                        }
                    } else {
                        items(filteredProducts, key = { it.id }) { product ->
                            ProductItem(
                                product = product,
                                onAddToCart = { Store.addToCart(product) }
                            )
                        }
                    }

                    item {
                        SectionTitle("Compras recentes")
                    }
                    if (Store.purchases.isEmpty()) {
                        item { EmptyStateCard("Nenhuma compra finalizada ainda.") }
                    } else {
                        items(Store.purchases.takeLast(5).reversed(), key = { it.id }) { purchase ->
                            PurchaseRow(purchase)
                        }
                    }
                }

                TopBar(
                    modifier = Modifier.align(Alignment.TopCenter),
                    onGoHome = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    onGoToProducts = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(2)
                        }
                    },
                    onGoToPurchases = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(contentItemCount - 1)
                        }
                    },
                    onOpenCart = { showCart = true }
                )

                if (showCart) {
                    CartDialog(onDismiss = { showCart = false })
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onGoHome: () -> Unit,
    onGoToProducts: () -> Unit,
    onGoToPurchases: () -> Unit,
    onOpenCart: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onGoHome) {
                Text("Home")
            }

            Button(onClick = onGoToProducts) {
                Text("Produtos")
            }

            Button(onClick = onGoToPurchases) {
                Text("Compras Recentes")
            }

            Button(onClick = onOpenCart) {
                Text("Carrinho (${Store.cart.size})")
            }
        }
    }
}

//@Composable
//private fun HeroSection(onOpenCart: () -> Unit) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
//    ) {
//        Column(
//            modifier = Modifier.padding(28.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Text(
//                "Monte seu pedido em minutos",
//                style = MaterialTheme.typography.displaySmall,
//                fontWeight = FontWeight.Black
//            )
//            Text(
//                "Explore o catalogo completo, aplique filtros por categoria e preco e finalize com boleto, pix ou cartao de credito.",
//                style = MaterialTheme.typography.bodyLarge
//            )
//            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//                Button(onClick = onOpenCart) { Text("Abrir carrinho") }
//            }
//        }
//    }
//}

@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        StatCard(label = "Produtos", value = Store.products.size.toString())
        StatCard(label = "Itens no carrinho", value = Store.cart.size.toString())
        StatCard(label = "Estoque total", value = Store.products.sumOf { it.stock }.toString())
    }
}

@Composable
private fun FilterSection(
    searchText: String,
    onSearchChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    selectedMaxPrice: Double,
    maxCatalogPrice: Double,
    onPriceChange: (Double) -> Unit,
    onlyInStock: Boolean,
    onOnlyInStockChange: (Boolean) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionTitle("Filtros")

            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar por nome") },
                singleLine = true
            )

            Text("Categoria", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                categories.forEach { category ->
                    val selected = selectedCategory == category
                    if (selected) {
                        Button(onClick = { onSelectCategory(category) }) { Text(category) }
                    } else {
                        TextButton(onClick = { onSelectCategory(category) }) { Text(category) }
                    }
                }
            }

            Text("Preco maximo: ${selectedMaxPrice.formatMoney()}")
            Slider(
                value = selectedMaxPrice.toFloat(),
                onValueChange = { onPriceChange(it.toDouble()) },
                valueRange = 0f..maxCatalogPrice.toFloat()
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Switch(checked = onlyInStock, onCheckedChange = onOnlyInStockChange)
                Text("Exibir somente com estoque")
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(modifier = Modifier.widthIn(min = 150.dp)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(message, modifier = Modifier.padding(18.dp))
    }
}

@Composable
private fun CartDialog(onDismiss: () -> Unit) {
    var feedback by remember { mutableStateOf<String?>(null) }
    var selectedPayment by remember { mutableStateOf(PaymentMethod.PIX) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 980.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ModalHeader(
                    title = "Carrinho",
                    subtitle = "Escolha o pagamento e finalize sua compra.",
                    onDismiss = onDismiss
                )

                if (Store.cartLines().isEmpty()) {
                    EmptyStateCard("Seu carrinho esta vazio.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 260.dp)) {
                        items(Store.cartLines(), key = { it.product.id }) { line ->
                            CartItem(
                                line = line,
                                onRemove = { Store.removeFromCart(line.product) }
                            )
                        }
                    }
                }

                HorizontalDivider()

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", style = MaterialTheme.typography.titleMedium)
                    Text(Store.cartTotal().formatMoney(), fontWeight = FontWeight.Bold)
                }

                Text("Forma de pagamento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    PaymentMethod.entries.forEach { method ->
                        if (selectedPayment == method) {
                            Button(onClick = { selectedPayment = method }) { Text(method.label) }
                        } else {
                            TextButton(onClick = { selectedPayment = method }) { Text(method.label) }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        enabled = Store.cartLines().isNotEmpty(),
                        onClick = {
                            val purchase = Store.checkout(selectedPayment.label)
                            feedback = purchase?.let {
                                "Compra finalizada via ${it.paymentMethod}: ${it.summary}"
                            } ?: "Carrinho vazio ou estoque insuficiente."
                        }
                    ) {
                        Text("Finalizar compra")
                    }

                    TextButton(
                        enabled = Store.cart.isNotEmpty(),
                        onClick = {
                            Store.clearCart()
                            feedback = "Carrinho limpo."
                        }
                    ) {
                        Text("Limpar carrinho")
                    }
                }

                feedback?.let { Text(it) }

                HorizontalDivider()

                Text("Compras salvas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (Store.purchases.isEmpty()) {
                    EmptyStateCard("Nenhuma compra finalizada ainda.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 180.dp)) {
                        items(Store.purchases.reversed(), key = { it.id }) { purchase ->
                            PurchaseRow(purchase)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PurchaseRow(purchase: Purchase) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(purchase.summary, fontWeight = FontWeight.SemiBold)
            Text("Pagamento: ${purchase.paymentMethod}")
            Text(purchase.total.formatMoney())
        }
    }
}

@Composable
private fun ModalHeader(title: String, subtitle: String, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
        Button(onClick = onDismiss) { Text("Fechar") }
    }
}

private fun Double.formatMoney(): String {
    val cents = kotlin.math.round(this * 100).toLong()
    val reais = cents / 100
    val centavos = kotlin.math.abs((cents % 100).toInt()).toString().padStart(2, '0')
    return "R$ $reais,$centavos"
}
