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
import br.edu.utfpr.loja_kobweb.dao.CartDao
import br.edu.utfpr.loja_kobweb.dao.ProductDao
import br.edu.utfpr.loja_kobweb.dao.PurchaseDao
import br.edu.utfpr.loja_kobweb.model.Purchase
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
        var showRestrictedProductsMenu by remember { mutableStateOf(false) }
        var searchText by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("Todas") }
        var onlyInStock by remember { mutableStateOf(false) }

        val maxCatalogPrice = (ProductDao.products.maxOfOrNull { it.price } ?: 0.0).coerceAtLeast(1.0)
        var selectedMaxPrice by remember(maxCatalogPrice) { mutableStateOf(maxCatalogPrice) }

        val categories = remember(ProductDao.products.size) {
            listOf("Todas") + ProductDao.products.map { it.category }.distinct().sorted()
        }

        val filteredProducts = ProductDao.products.filter { product ->
            val categoryMatches = selectedCategory == "Todas" || product.category == selectedCategory
            val nameMatches = product.name.contains(searchText.trim(), ignoreCase = true)
            val priceMatches = product.price <= selectedMaxPrice
            val stockMatches = !onlyInStock || product.stock > 0
            categoryMatches && nameMatches && priceMatches && stockMatches
        }

        val contentItemCount = 4 +
            if (filteredProducts.isEmpty()) 1 else filteredProducts.size +
            if (PurchaseDao.purchases.isEmpty()) 1 else minOf(5, PurchaseDao.purchases.size)

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
                                onAddToCart = { CartDao.addToCart(product) }
                            )
                        }
                    }

                    item {
                        SectionTitle("Compras recentes")
                    }
                    if (PurchaseDao.purchases.isEmpty()) {
                        item { EmptyStateCard("Nenhuma compra finalizada ainda.") }
                    } else {
                        items(PurchaseDao.purchases.takeLast(5).reversed(), key = { it.id }) { purchase ->
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
                    onGoToRestictedProductsMenu = { showRestrictedProductsMenu = true },
                    onOpenCart = { showCart = true }
                )

                if (showRestrictedProductsMenu) {
                    ProductRegistrationDialog(onDismiss = { showRestrictedProductsMenu = false })
                }

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
    onGoToRestictedProductsMenu: () -> Unit,
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

            Button(onClick = onGoToRestictedProductsMenu) {
                Text("Cadastrar Produtos")
            }

            Button(onClick = onGoToPurchases) {
                Text("Compras Recentes")
            }

            Button(onClick = onOpenCart) {
                Text("Carrinho (${CartDao.cart.size})")
            }
        }
    }
}

@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        StatCard(label = "Produtos", value = ProductDao.products.size.toString())
        StatCard(label = "Itens no carrinho", value = CartDao.cart.size.toString())
        StatCard(label = "Estoque total", value = ProductDao.products.sumOf { it.stock }.toString())
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

                if (CartDao.cartLines().isEmpty()) {
                    EmptyStateCard("Seu carrinho esta vazio.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 260.dp)) {
                        items(CartDao.cartLines(), key = { it.product.id }) { line ->
                            CartItem(
                                line = line,
                                onRemove = { CartDao.removeFromCart(line.product) }
                            )
                        }
                    }
                }

                HorizontalDivider()

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", style = MaterialTheme.typography.titleMedium)
                    Text(CartDao.cartTotal().formatMoney(), fontWeight = FontWeight.Bold)
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
                        enabled = CartDao.cartLines().isNotEmpty(),
                        onClick = {
                            val purchase = PurchaseDao.checkout(selectedPayment.label)
                            feedback = purchase?.let {
                                "Compra finalizada via ${it.paymentMethod}: ${it.summary}"
                            } ?: "Carrinho vazio ou estoque insuficiente."
                        }
                    ) {
                        Text("Finalizar compra")
                    }

                    TextButton(
                        enabled = CartDao.cart.isNotEmpty(),
                        onClick = {
                            CartDao.clearCart()
                            feedback = "Carrinho limpo."
                        }
                    ) {
                        Text("Limpar carrinho")
                    }
                }

                feedback?.let { Text(it) }

                HorizontalDivider()

                Text("Compras salvas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (PurchaseDao.purchases.isEmpty()) {
                    EmptyStateCard("Nenhuma compra finalizada ainda.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 180.dp)) {
                        items(PurchaseDao.purchases.reversed(), key = { it.id }) { purchase ->
                            PurchaseRow(purchase)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductRegistrationDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }

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
                    title = "Cadastrar produto",
                    subtitle = "Preencha os campos para adicionar um novo produto ao catalogo.",
                    onDismiss = onDismiss
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nome") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Categoria") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Preco") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Estoque") },
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            val normalizedPrice = priceText.replace(',', '.')
                            val price = normalizedPrice.toDoubleOrNull()
                            val stock = stockText.toIntOrNull()

                            if (price == null || stock == null) {
                                feedback = "Preco e estoque devem ser numericos."
                                return@Button
                            }

                            val product = ProductDao.addProduct(
                                name = name,
                                category = category,
                                price = price,
                                stock = stock
                            )

                            feedback = if (product != null) {
                                name = ""
                                category = ""
                                priceText = ""
                                stockText = ""
                                "Produto cadastrado com sucesso."
                            } else {
                                "Preencha os campos corretamente. Preco > 0 e estoque >= 0."
                            }
                        }
                    ) {
                        Text("Cadastrar")
                    }

                    TextButton(
                        onClick = {
                            name = ""
                            category = ""
                            priceText = ""
                            stockText = ""
                            feedback = null
                        }
                    ) {
                        Text("Limpar")
                    }
                }

                feedback?.let { Text(it) }
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
