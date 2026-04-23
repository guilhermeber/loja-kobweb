package br.edu.utfpr.loja_kobweb.dao

import androidx.compose.runtime.mutableStateListOf
import br.edu.utfpr.loja_kobweb.dao.percistence.InitialDataPersistence
import br.edu.utfpr.loja_kobweb.model.Product
import br.edu.utfpr.loja_kobweb.model.Purchase
import br.edu.utfpr.loja_kobweb.model.User
import kotlinx.browser.window

internal object DaoState {
    private const val STORAGE_KEY = "loja-kobweb-store-v3"

    val products = mutableStateListOf<Product>()
    val cart = mutableStateListOf<Product>()
    val purchases = mutableStateListOf<Purchase>()
    val users = mutableStateListOf<User>()
    var nextProductId = 1
    var nextPurchaseId = 1
    var nextUserId = 1

    init {
        resetStore()
    }

    private fun resetStore() {
        window.localStorage.clear()
        products.clear()
        cart.clear()
        purchases.clear()
        users.clear()
        nextProductId = 1
        nextPurchaseId = 1
        nextUserId = 1

        val initialProducts = InitialDataPersistence.initialProducts(nextProductId)
        products.addAll(initialProducts)
        nextProductId += initialProducts.size

        val initialUsers = InitialDataPersistence.initialUsers(nextUserId)
        users.addAll(initialUsers)
        nextUserId += initialUsers.size

        persist()
    }

    fun persist() {
        runCatching {
            window.localStorage.setItem(STORAGE_KEY, buildString {
                appendLine("products=${products.joinToString(";") { "${it.id}|${it.name}|${it.category}|${it.price}|${it.stock}" }}")
                appendLine("cart=${cart.joinToString(",") { it.id.toString() }}")
                appendLine("purchases=${purchases.joinToString(";") { "${it.id}|${it.summary}|${it.total}|${it.paymentMethod}" }}")
                appendLine("users=${users.joinToString(";") { "${it.id}|${it.name}|${it.email}|${it.password}|${it.role.name}|${it.isActive}" }}")
                appendLine("nextId=$nextProductId")
                appendLine("nextPurchaseId=$nextPurchaseId")
                appendLine("nextUserId=$nextUserId")
            })
        }
    }
}