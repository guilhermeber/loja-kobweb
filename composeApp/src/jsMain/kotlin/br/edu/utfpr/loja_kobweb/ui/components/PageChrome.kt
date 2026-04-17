package br.edu.utfpr.loja_kobweb.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.utfpr.loja_kobweb.ui.navigation.AppRoute

@Composable
fun PageShell(
    activeRoute: AppRoute,
    onNavigate: (AppRoute) -> Unit,
    content: @Composable () -> Unit
) {
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppNavigationBar(activeRoute = activeRoute, onNavigate = onNavigate)
            content()
        }
    }
}

@Composable
fun AppNavigationBar(
    activeRoute: AppRoute,
    onNavigate: (AppRoute) -> Unit
) {
    val routes = listOf(
        AppRoute.Home to "Home",
        AppRoute.Products to "Produtos",
        AppRoute.Cart to "Carrinho",
        AppRoute.Purchases to "Compras",
        AppRoute.ProductCreate to "Novo Produto",
        AppRoute.Users to "Usuarios"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                routes.forEach { (route, label) ->
                    if (route::class == activeRoute::class && route.path == activeRoute.path) {
                        Button(onClick = { onNavigate(route) }) { Text(label) }
                    } else {
                        TextButton(onClick = { onNavigate(route) }) { Text(label) }
                    }
                }
            }

            Text(
                text = when (activeRoute) {
                    AppRoute.Home -> "Landing page"
                    AppRoute.Products -> "Catalogo de produtos"
                    AppRoute.Cart -> "Itens do carrinho e checkout"
                    AppRoute.Purchases -> "Historico de compras"
                    AppRoute.ProductCreate -> "Cadastro de produto"
                    AppRoute.Users -> "Validacao e gestao de usuarios"
                    is AppRoute.ProductDetail -> "Detalhes do produto"
                    AppRoute.NotFound -> "Pagina nao encontrada"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
}

@Composable
fun StatCard(label: String, value: String) {
    Card(modifier = Modifier.widthIn(min = 150.dp)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(message, modifier = Modifier.padding(18.dp))
    }
}