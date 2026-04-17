package br.edu.utfpr.loja_kobweb.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.utfpr.loja_kobweb.components.CartItem
import br.edu.utfpr.loja_kobweb.dao.CartDao
import br.edu.utfpr.loja_kobweb.dao.PurchaseDao
import br.edu.utfpr.loja_kobweb.ui.components.EmptyStateCard
import br.edu.utfpr.loja_kobweb.ui.components.PageShell
import br.edu.utfpr.loja_kobweb.ui.components.SectionTitle
import br.edu.utfpr.loja_kobweb.ui.navigation.AppRoute

private enum class PaymentMethod(val label: String) {
    BOLETO("Boleto"),
    PIX("Pix"),
    CREDITO("Cartao de credito")
}

@Composable
fun CartPage(onNavigate: (AppRoute) -> Unit) {
    var feedback by remember { mutableStateOf<String?>(null) }
    var selectedPayment by remember { mutableStateOf(PaymentMethod.PIX) }

    PageShell(activeRoute = AppRoute.Cart, onNavigate = onNavigate) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionTitle("Carrinho")

            if (CartDao.cartLines().isEmpty()) {
                EmptyStateCard("Seu carrinho esta vazio.")
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 320.dp)) {
                            items(CartDao.cartLines(), key = { it.product.id }) { line ->
                                CartItem(
                                    line = line,
                                    onRemove = { CartDao.removeFromCart(line.product) }
                                )
                            }
                        }
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
                            onNavigate(AppRoute.Purchases)
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
                androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 220.dp)) {
                    items(PurchaseDao.purchases.reversed(), key = { it.id }) { purchase ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(purchase.summary, fontWeight = FontWeight.SemiBold)
                                Text("Pagamento: ${purchase.paymentMethod}")
                                Text(purchase.total.formatMoney())
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