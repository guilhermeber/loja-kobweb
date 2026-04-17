package br.edu.utfpr.loja_kobweb.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import br.edu.utfpr.loja_kobweb.dao.ProductDao
import br.edu.utfpr.loja_kobweb.ui.components.EmptyStateCard
import br.edu.utfpr.loja_kobweb.ui.components.PageShell
import br.edu.utfpr.loja_kobweb.ui.components.SectionTitle
import br.edu.utfpr.loja_kobweb.ui.navigation.AppRoute

@Composable
fun ProductCreatePage(onNavigate: (AppRoute) -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }

    PageShell(activeRoute = AppRoute.ProductCreate, onNavigate = onNavigate) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SectionTitle("Cadastrar produto")

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nome") }, singleLine = true)
                    OutlinedTextField(value = category, onValueChange = { category = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Categoria") }, singleLine = true)
                    OutlinedTextField(value = priceText, onValueChange = { priceText = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Preco") }, singleLine = true)
                    OutlinedTextField(value = stockText, onValueChange = { stockText = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Estoque") }, singleLine = true)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                val normalizedPrice = priceText.replace(',', '.')
                                val price = normalizedPrice.toDoubleOrNull()
                                val stock = stockText.toIntOrNull()

                                feedback = when {
                                    price == null || stock == null -> "Preco e estoque devem ser numericos."
                                    else -> {
                                        val product = ProductDao.addProduct(name, category, price, stock)
                                        if (product != null) {
                                            name = ""
                                            category = ""
                                            priceText = ""
                                            stockText = ""
                                            "Produto cadastrado com sucesso."
                                        } else {
                                            "Preencha os campos corretamente. Preco > 0 e estoque >= 0."
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("Cadastrar")
                        }

                        TextButton(onClick = {
                            name = ""
                            category = ""
                            priceText = ""
                            stockText = ""
                            feedback = null
                        }) {
                            Text("Limpar")
                        }
                    }

                    feedback?.let { Text(it) }
                }
            }

            EmptyStateCard("A validacao do cadastro de produto continua centralizada no DAO.")
        }
    }
}