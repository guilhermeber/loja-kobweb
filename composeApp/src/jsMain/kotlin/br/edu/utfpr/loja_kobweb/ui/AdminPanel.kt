package br.edu.utfpr.loja_kobweb.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import br.edu.utfpr.loja_kobweb.model.Product
import br.edu.utfpr.loja_kobweb.store.Store
// IMPORTANTE: Certifique-se de que o AuthStore está acessível para ler a lista de usuários
import br.edu.utfpr.loja_kobweb.store.AuthStore

@Composable
fun AdminPanel(onClose: () -> Unit) {
    var showAddProduct by remember { mutableStateOf(false) }
    var showEditProduct by remember { mutableStateOf<Product?>(null) }
    var showUserRoles by remember { mutableStateOf(false)}
    var feedback by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 1000.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Painel de Administração", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Gerencie seus produtos", style = MaterialTheme.typography.bodyMedium)
                    }
                    Button(onClick = onClose) { Text("Fechar") }
                }

                HorizontalDivider()

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { showAddProduct = true }) {
                        Text("+ Adicionar Produto")
                    }
                    Button(onClick = { showUserRoles = true }) {
                        Text("Gerenciar Usuários")
                    }
                    feedback?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Text("Produtos cadastrados (${Store.products.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 400.dp)) {
                    items(Store.products, key = { it.id }) { product ->
                        AdminProductCard(
                            product = product,
                            onEdit = { showEditProduct = product },
                            onDelete = {
                                Store.deleteProduct(product.id)
                                feedback = "Produto deletado!"
                            }
                        )
                    }
                }

                Text("Total em estoque: ${Store.products.sumOf { it.stock }} unidades", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    if (showAddProduct) {
        AddEditProductDialog(
            product = null,
            onDismiss = { showAddProduct = false },
            onSave = { name, category, price, stock ->
                Store.addProduct(name, category, price, stock)
                showAddProduct = false
                feedback = "Produto adicionado!"
            }
        )
    }

    if (showUserRoles) {
        UserRolesDialog(
            onDismiss = { showUserRoles = false },
            onSave = { idDoUsuario, listaDeFuncoes ->
                Store.updateUserRoles(idDoUsuario, listaDeFuncoes)
                showUserRoles = false
                feedback = "Funções de usuário atualizadas!"
            }
        )
    }

    showEditProduct?.let { product ->
        AddEditProductDialog(
            product = product,
            onDismiss = { showEditProduct = null },
            onSave = { name, category, price, stock ->
                Store.updateProduct(product.id, name, category, price, stock)
                showEditProduct = null
                feedback = "Produto atualizado!"
            }
        )
    }
}

@Composable
private fun AdminProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(product.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Categoria: ${product.category}", style = MaterialTheme.typography.bodySmall)
                Text("Preço: ${product.price.formatMoney()} | Estoque: ${product.stock}", style = MaterialTheme.typography.bodySmall)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onEdit) {
                    Text("Editar")
                }
                TextButton(onClick = onDelete) {
                    Text("Deletar", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun AddEditProductDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, Int) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier.widthIn(max = 500.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    if (product == null) "Adicionar Produto" else "Editar Produto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nome do Produto") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Categoria") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Preço (R$)") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Estoque (unidades)") },
                    singleLine = true
                )

                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            val priceNum = price.toDoubleOrNull()
                            val stockNum = stock.toIntOrNull()

                            when {
                                name.isBlank() -> error = "Nome é obrigatório"
                                category.isBlank() -> error = "Categoria é obrigatória"
                                priceNum == null || priceNum < 0 -> error = "Preço inválido"
                                stockNum == null || stockNum < 0 -> error = "Estoque inválido"
                                else -> {
                                    onSave(name, category, priceNum, stockNum)
                                    onDismiss()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (product == null) "Adicionar" else "Atualizar")
                    }
                }
            }
        }
    }
}

@Composable
private fun UserRolesDialog(
    onDismiss: () -> Unit,
    onSave: (Int, List<String>) -> Unit
) {
    // Guarda qual ID de usuário foi selecionado na lista externa
    var selectedUserId by remember { mutableStateOf<Int?>(null) }
    var selectedRoles by remember { mutableStateOf(setOf<String>()) }
    var error by remember { mutableStateOf<String?>(null) }

    val availableRoles = listOf("Admin", "Editor", "Viewer")
    val registeredUsers = AuthStore.users

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier.widthIn(max = 450.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Gerenciar Funções de Usuário", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                Text("1. Selecione o Usuário:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

                // Lista contendo os usuários cadastrados para seleção em tempo real
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(registeredUsers, key = { it.id }) { user ->
                        val isSelected = selectedUserId == user.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedUserId = user.id },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedUserId = user.id }
                                )
                                Column {
                                    Text("ID: ${user.id} | ${user.email}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text("Funções atuais: ${user.role}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                Text("2. Atribuir novas funções:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    availableRoles.forEach { role ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = selectedRoles.contains(role),
                                onCheckedChange = { isChecked ->
                                    selectedRoles = if (isChecked) {
                                        selectedRoles + role
                                    } else {
                                        selectedRoles - role
                                    }
                                }
                            )
                            Text(role, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            val currentSelectedId = selectedUserId
                            if (currentSelectedId == null) {
                                error = "Você precisa selecionar um usuário na lista acima"
                            } else {
                                onSave(currentSelectedId, selectedRoles.toList())
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Salvar")
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