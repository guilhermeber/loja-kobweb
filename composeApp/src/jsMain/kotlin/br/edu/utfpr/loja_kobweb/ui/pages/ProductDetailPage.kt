package br.edu.utfpr.loja_kobweb.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.utfpr.loja_kobweb.dao.CartDao
import br.edu.utfpr.loja_kobweb.dao.ProductDao
import br.edu.utfpr.loja_kobweb.ui.components.EmptyStateCard
import br.edu.utfpr.loja_kobweb.ui.components.PageShell
import br.edu.utfpr.loja_kobweb.ui.components.SectionTitle
import br.edu.utfpr.loja_kobweb.ui.navigation.AppRoute

@Composable
fun ProductDetailPage(productId: Int, onNavigate: (AppRoute) -> Unit) {
    val product = ProductDao.findById(productId)

    PageShell(activeRoute = AppRoute.ProductDetail(productId), onNavigate = onNavigate) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionTitle("Detalhes do produto")

            if (product == null) {
                EmptyStateCard("Produto nao encontrado.")
                Button(onClick = { onNavigate(AppRoute.Products) }) {
                    Text("Voltar para produtos")
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(product.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Categoria: ${product.category}")
                        Text(product.price.formatMoney())
                        Text("Estoque: ${product.stock}")

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(enabled = product.stock > 0, onClick = { CartDao.addToCart(product) }) {
                                Text(if (product.stock > 0) "Adicionar ao carrinho" else "Sem estoque")
                            }
                            Button(onClick = { onNavigate(AppRoute.Products) }) {
                                Text("Voltar")
                            }
                        }
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