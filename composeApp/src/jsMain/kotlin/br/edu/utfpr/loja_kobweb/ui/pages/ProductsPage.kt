package br.edu.utfpr.loja_kobweb.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.utfpr.loja_kobweb.components.ProductItem
import br.edu.utfpr.loja_kobweb.dao.CartDao
import br.edu.utfpr.loja_kobweb.dao.ProductDao
import br.edu.utfpr.loja_kobweb.ui.components.EmptyStateCard
import br.edu.utfpr.loja_kobweb.ui.components.PageShell
import br.edu.utfpr.loja_kobweb.ui.components.SectionTitle
import br.edu.utfpr.loja_kobweb.ui.navigation.AppRoute

@Composable
fun ProductsPage(onNavigate: (AppRoute) -> Unit) {
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

    PageShell(activeRoute = AppRoute.Products, onNavigate = onNavigate) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionTitle("Catalogo de produtos")

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Buscar por nome") },
                        singleLine = true
                    )

                    Text("Categoria", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        categories.forEach { category ->
                            val selected = selectedCategory == category
                            if (selected) {
                                Button(onClick = { selectedCategory = category }) { Text(category) }
                            } else {
                                TextButton(onClick = { selectedCategory = category }) { Text(category) }
                            }
                        }
                    }

                    Text("Preco maximo: ${selectedMaxPrice.formatMoney()}")
                    Slider(
                        value = selectedMaxPrice.toFloat(),
                        onValueChange = { selectedMaxPrice = it.toDouble() },
                        valueRange = 0f..maxCatalogPrice.toFloat()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Switch(checked = onlyInStock, onCheckedChange = { onlyInStock = it })
                        Text("Exibir somente com estoque")
                    }
                }
            }

            if (filteredProducts.isEmpty()) {
                EmptyStateCard("Nenhum produto encontrado com os filtros aplicados.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductItem(
                            product = product,
                            onAddToCart = { CartDao.addToCart(product) },
                            onViewDetails = { onNavigate(AppRoute.ProductDetail(product.id)) }
                        )
                    }
                }
            }
        }
    }
}

private fun Double.formatMoney(): String {
    val cents = kotlin.math.round(this * 100).toLong()
    val reais = cents / 100
    val centavos = kotlin.math.abs((cents % 100).toInt()).toString().padStart(2, '0')
    return "R$ $reais,$centavos"
}