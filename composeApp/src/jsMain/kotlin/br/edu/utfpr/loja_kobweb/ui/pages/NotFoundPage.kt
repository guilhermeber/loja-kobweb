package br.edu.utfpr.loja_kobweb.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import br.edu.utfpr.loja_kobweb.ui.components.PageShell
import br.edu.utfpr.loja_kobweb.ui.components.SectionTitle
import br.edu.utfpr.loja_kobweb.ui.navigation.AppRoute

@Composable
fun NotFoundPage(onNavigate: (AppRoute) -> Unit) {
    PageShell(activeRoute = AppRoute.NotFound, onNavigate = onNavigate) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionTitle("Pagina nao encontrada")

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("A rota solicitada nao existe ou nao foi reconhecida.", fontWeight = FontWeight.SemiBold)
                    Text("Volte para a pagina inicial ou escolha uma rota valida.", style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = { onNavigate(AppRoute.Home) }) {
                        Text("Ir para a landing page")
                    }
                }
            }
        }
    }
}