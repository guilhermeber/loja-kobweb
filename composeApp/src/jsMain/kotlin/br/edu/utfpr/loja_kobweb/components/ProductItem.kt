package br.edu.utfpr.loja_kobweb.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.utfpr.loja_kobweb.model.Product

@Composable
fun ProductItem(
    product: Product,
    onAddToCart: () -> Unit,
    onViewDetails: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Categoria: ${product.category}", style = MaterialTheme.typography.bodyMedium)
                Text(product.price.formatMoney(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("Estoque: ${product.stock}", style = MaterialTheme.typography.bodySmall)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAddToCart, enabled = product.stock > 0) {
                    Text(if (product.stock > 0) "Adicionar" else "Sem estoque")
                }

                if (onViewDetails != null) {
                    Button(onClick = onViewDetails) {
                        Text("Detalhes")
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
