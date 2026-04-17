package br.edu.utfpr.loja_kobweb.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.utfpr.loja_kobweb.dao.CartDao
import br.edu.utfpr.loja_kobweb.dao.ProductDao
import br.edu.utfpr.loja_kobweb.dao.PurchaseDao
import br.edu.utfpr.loja_kobweb.dao.UserDao
import br.edu.utfpr.loja_kobweb.ui.components.EmptyStateCard
import br.edu.utfpr.loja_kobweb.ui.components.PageShell
import br.edu.utfpr.loja_kobweb.ui.components.SectionTitle
import br.edu.utfpr.loja_kobweb.ui.components.StatCard
import br.edu.utfpr.loja_kobweb.ui.navigation.AppRoute

@Composable
fun App(onNavigate: (AppRoute) -> Unit = {}) {
    PageShell(activeRoute = AppRoute.Home, onNavigate = onNavigate) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Loja Kobweb", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                        Text(
                            "Uma landing page para acessar produtos, carrinho, compras, cadastro e usuarios.",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onNavigate(AppRoute.Products) }) { Text("Explorar produtos") }
                            Button(onClick = { onNavigate(AppRoute.Cart) }) { Text("Abrir carrinho") }
                            Button(onClick = { onNavigate(AppRoute.Users) }) { Text("Usuarios") }
                        }
                    }
                }

                SectionTitle("Resumo da loja")

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    StatCard("Produtos", ProductDao.products.size.toString())
                    StatCard("Carrinho", CartDao.cart.size.toString())
                    StatCard("Compras", PurchaseDao.purchases.size.toString())
                    StatCard("Usuarios", UserDao.users.size.toString())
                }

                SectionTitle("Atalhos")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickLinkCard("/produtos", "Catalogo com filtros", { onNavigate(AppRoute.Products) })
                    QuickLinkCard("/carrinho", "Itens do carrinho e checkout", { onNavigate(AppRoute.Cart) })
                    QuickLinkCard("/compras", "Historico de compras", { onNavigate(AppRoute.Purchases) })
                    QuickLinkCard("/admin/usuarios", "Validacao e gestao de usuarios", { onNavigate(AppRoute.Users) })
                }

                EmptyStateCard("As demais interfaces foram separadas em rotas dedicadas fora da landing page.")
            }
        }
    }
}

@Composable
private fun QuickLinkCard(title: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(description)
            Button(onClick = onClick) { Text("Abrir") }
        }
    }
}