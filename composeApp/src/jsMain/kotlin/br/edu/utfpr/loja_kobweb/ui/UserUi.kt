package br.edu.utfpr.loja_kobweb.ui

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
import br.edu.utfpr.loja_kobweb.components.UserItem
import br.edu.utfpr.loja_kobweb.dao.UserDao
import br.edu.utfpr.loja_kobweb.model.User
import br.edu.utfpr.loja_kobweb.model.UserRole

const val USERS_ADMIN_ROUTE = "/admin/usuarios"

@Composable
fun UserUi() {
	var currentUser by remember { mutableStateOf<User?>(null) }

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.widthIn(max = 1000.dp)
			.padding(20.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text("Gestao de usuarios", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
		Text("Rota sugerida no plano: $USERS_ADMIN_ROUTE")

		LoginSection(
			onLogin = { email, password ->
				val auth = UserDao.authenticate(email, password)
				currentUser = auth.user
				auth.error
			}
		)

		RegistrationSection(currentUser)

		if (UserDao.canAccessAdminArea(currentUser)) {
			UsersListSection()
		} else {
			Card(modifier = Modifier.fillMaxWidth()) {
				Text(
					"Somente administradores ativos podem gerenciar usuarios.",
					modifier = Modifier.padding(16.dp)
				)
			}
		}
	}
}

@Composable
private fun LoginSection(onLogin: (String, String) -> String?) {
	var email by remember { mutableStateOf("") }
	var password by remember { mutableStateOf("") }
	var feedback by remember { mutableStateOf<String?>(null) }

	Card(modifier = Modifier.fillMaxWidth()) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
			Text("Entrar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

			OutlinedTextField(
				value = email,
				onValueChange = { email = it },
				modifier = Modifier.fillMaxWidth(),
				label = { Text("E-mail") },
				singleLine = true
			)

			OutlinedTextField(
				value = password,
				onValueChange = { password = it },
				modifier = Modifier.fillMaxWidth(),
				label = { Text("Senha") },
				singleLine = true
			)

			Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				Button(onClick = { feedback = onLogin(email, password) ?: "Login realizado com sucesso." }) {
					Text("Entrar")
				}
				TextButton(onClick = {
					email = ""
					password = ""
					feedback = null
				}) {
					Text("Limpar")
				}
			}

			feedback?.let { Text(it) }
		}
	}
}

@Composable
private fun RegistrationSection(currentUser: User?) {
	var name by remember { mutableStateOf("") }
	var email by remember { mutableStateOf("") }
	var password by remember { mutableStateOf("") }
	var confirmPassword by remember { mutableStateOf("") }
	var adminProfile by remember { mutableStateOf(false) }
	var feedback by remember { mutableStateOf<List<String>>(emptyList()) }

	Card(modifier = Modifier.fillMaxWidth()) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
			Text("Cadastrar usuario", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

			OutlinedTextField(
				value = name,
				onValueChange = { name = it },
				modifier = Modifier.fillMaxWidth(),
				label = { Text("Nome") },
				singleLine = true
			)

			OutlinedTextField(
				value = email,
				onValueChange = { email = it },
				modifier = Modifier.fillMaxWidth(),
				label = { Text("E-mail") },
				singleLine = true
			)

			OutlinedTextField(
				value = password,
				onValueChange = { password = it },
				modifier = Modifier.fillMaxWidth(),
				label = { Text("Senha") },
				singleLine = true
			)

			OutlinedTextField(
				value = confirmPassword,
				onValueChange = { confirmPassword = it },
				modifier = Modifier.fillMaxWidth(),
				label = { Text("Confirmar senha") },
				singleLine = true
			)

			if (UserDao.canAccessAdminArea(currentUser)) {
				Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
					Text("Criar como admin")
					Button(onClick = { adminProfile = !adminProfile }) {
						Text(if (adminProfile) "Sim" else "Nao")
					}
				}
			}

			Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				Button(
					onClick = {
						val result = UserDao.registerUser(
							name = name,
							email = email,
							password = password,
							confirmPassword = confirmPassword,
							role = if (adminProfile && UserDao.canAccessAdminArea(currentUser)) {
								UserRole.ADMIN
							} else {
								UserRole.CUSTOMER
							}
						)

						feedback = if (result.valid) {
							name = ""
							email = ""
							password = ""
							confirmPassword = ""
							adminProfile = false
							listOf("Usuario cadastrado com sucesso.")
						} else {
							result.errors
						}
					}
				) {
					Text("Cadastrar")
				}

				TextButton(onClick = {
					name = ""
					email = ""
					password = ""
					confirmPassword = ""
					adminProfile = false
					feedback = emptyList()
				}) {
					Text("Limpar")
				}
			}

			feedback.forEach { Text(it) }
		}
	}
}

@Composable
private fun UsersListSection() {
	Card(modifier = Modifier.fillMaxWidth()) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
			Text("Usuarios cadastrados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

			LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				items(UserDao.users, key = { it.id }) { user ->
					UserItem(
						user = user,
						onToggleActive = { active -> UserDao.setUserActive(user.id, active) }
					)
				}
			}
		}
	}
}
