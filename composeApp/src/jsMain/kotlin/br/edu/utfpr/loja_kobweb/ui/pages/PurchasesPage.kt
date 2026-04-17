package br.edu.utfpr.loja_kobweb.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.utfpr.loja_kobweb.dao.PurchaseDao
import br.edu.utfpr.loja_kobweb.ui.components.EmptyStateCard
import br.edu.utfpr.loja_kobweb.ui.components.PageShell
import br.edu.utfpr.loja_kobweb.ui.components.SectionTitle
import br.edu.utfpr.loja_kobweb.ui.navigation.AppRoute

@Composable
fun PurchasesPage(onNavigate: (AppRoute) -> Unit) {
    PageShell(activeRoute = AppRoute.Purchases, onNavigate = onNavigate) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionTitle("Historico de compras")

            if (PurchaseDao.purchases.isEmpty()) {
                EmptyStateCard("Nenhuma compra finalizada ainda.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PurchaseDao.purchases.reversed(), key = { it.id }) { purchase ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(purchase.summary, fontWeight = FontWeight.SemiBold)
                                Text("Pagamento: ${purchase.paymentMethod}")
                                Text(purchase.total.formatMoney(), style = MaterialTheme.typography.titleSmall)
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