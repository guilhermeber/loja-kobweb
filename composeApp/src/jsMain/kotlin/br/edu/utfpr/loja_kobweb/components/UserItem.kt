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
import br.edu.utfpr.loja_kobweb.model.User

@Composable
fun UserItem(
	user: User,
	onToggleActive: (Boolean) -> Unit
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
			Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
				Text(user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
				Text(user.email)
				Text("Perfil: ${user.role.name}")
				Text(if (user.isActive) "Status: ativo" else "Status: inativo")
			}

			Button(onClick = { onToggleActive(!user.isActive) }) {
				Text(if (user.isActive) "Desativar" else "Ativar")
			}
		}
	}
}
