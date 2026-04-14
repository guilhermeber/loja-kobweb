package br.edu.utfpr.loja_kobweb.store

import androidx.compose.runtime.mutableStateListOf
import br.edu.utfpr.loja_kobweb.model.CartLine
import br.edu.utfpr.loja_kobweb.model.Product
import br.edu.utfpr.loja_kobweb.model.Purchase
import kotlinx.browser.window

object Store {
    private const val STORAGE_KEY = "loja-kobweb-store-v3"

    val products = mutableStateListOf<Product>()
    val cart = mutableStateListOf<Product>()
    val purchases = mutableStateListOf<Purchase>()
    var nextId = 1
    var nextPurchaseId = 1

    init {
        resetStore()
    }

    private fun resetStore() {
        window.localStorage.clear()
        products.clear()
        cart.clear()
        purchases.clear()
        nextId = 1
        nextPurchaseId = 1

        seedInitialProducts()
        persist()
    }

    private fun seedInitialProducts() {
        products.addAll(
            listOf(
                Product(nextId++, "Notebook Gamer Aurora", "Informatica", 4299.90, 10),
                Product(nextId++, "Headset Pulse Pro", "Audio", 349.90, 20),
                Product(nextId++, "Mouse Optico Vortex", "Perifericos", 149.90, 30),
                Product(nextId++, "Teclado Mecanico Neo", "Perifericos", 399.90, 18),
                Product(nextId++, "Monitor UltraWide 34", "Monitores", 1899.90, 12),
                Product(nextId++, "Cadeira Ergonomica Flux", "Moveis", 1199.90, 15),
                Product(nextId++, "Webcam Crystal 4K", "Informatica", 599.90, 25),
                Product(nextId++, "Caixa de Som Orbit", "Audio", 279.90, 28),
                Product(nextId++, "SSD NVMe 1TB Velocity", "Armazenamento", 499.90, 35),
                Product(nextId++, "HD Externo 2TB Titan", "Armazenamento", 429.90, 22),
                Product(nextId++, "Roteador Wi-Fi 6 AirLink", "Redes", 689.90, 16),
                Product(nextId++, "Switch Gigabit 8 Portas", "Redes", 329.90, 18),
                Product(nextId++, "Controle Sem Fio NovaPad", "Games", 259.90, 26),
                Product(nextId++, "Mesa Gamer Elevare", "Moveis", 899.90, 9),
                Product(nextId++, "Lampada Smart RGB Lumen", "Casa Inteligente", 99.90, 40),
                Product(nextId++, "Tomada Inteligente Plug+", "Casa Inteligente", 129.90, 32),
                Product(nextId++, "Nobreak Guard 1200VA", "Energia", 849.90, 11),
                Product(nextId++, "Filtro de Linha SafePower", "Energia", 89.90, 45),
                Product(nextId++, "Impressora Multifuncional JetAll", "Impressao", 799.90, 14),
                Product(nextId++, "Pacote Papel A4 500 Folhas", "Impressao", 34.90, 60),
                Product(nextId++, "Suporte Articulado para Monitor", "Acessorios", 219.90, 21),
                Product(nextId++, "Base Refrigerada para Notebook", "Acessorios", 169.90, 24),
                Product(nextId++, "Smartphone Horizon X", "Smartphones", 2399.90, 17),
                Product(nextId++, "Smartwatch Pulse Track", "Wearables", 699.90, 19),
                Product(nextId++, "Microfone Condensador VoicePro", "Audio", 459.90, 23),
                Product(nextId++, "Placa de Video RTX Storm", "Informatica", 3599.90, 8),
                Product(nextId++, "Hub USB-C 7 em 1", "Acessorios", 249.90, 27),
                Product(nextId++, "Carregador Turbo 65W", "Acessorios", 139.90, 34),
                Product(nextId++, "Projetor Full HD Vision", "Monitores", 2199.90, 7),
                Product(nextId++, "Mini PC Office Cube", "Informatica", 2799.90, 13),
                Product(nextId++, "Cadeira Presidente Orion", "Moveis", 1499.90, 10),
                Product(nextId++, "Fone Bluetooth Wave", "Audio", 199.90, 31),
                Product(nextId++, "Pulseira Smart FitBand", "Wearables", 299.90, 22),
                Product(nextId++, "Console Retro PixelBox", "Games", 1799.90, 12)
            )
        )
    }

    fun addToCart(product: Product) {
        val currentProduct = findProductById(product.id) ?: return
        if (cart.count { it.id == currentProduct.id } >= currentProduct.stock) {
            return
        }

        cart.add(currentProduct)
        persist()
    }

    fun removeFromCart(product: Product) {
        val removed = cart.remove(product)
        if (removed) {
            persist()
        }
    }

    fun clearCart() {
        if (cart.isEmpty()) return
        cart.clear()
        persist()
    }

    fun cartLines(): List<CartLine> {
        return cart
            .groupBy { it.id }
            .mapNotNull { (_, items) -> items.firstOrNull()?.let { CartLine(it, items.size) } }
            .sortedBy { it.product.name }
    }

    fun cartTotal(): Double = cartLines().sumOf { it.subtotal }

    fun checkout(paymentMethod: String): Purchase? {
        val lines = cartLines()
        if (lines.isEmpty()) {
            return null
        }

        val outOfStockLine = lines.firstOrNull { line ->
            val currentProduct = findProductById(line.product.id)
            currentProduct == null || line.quantity > currentProduct.stock
        }
        if (outOfStockLine != null) {
            return null
        }

        lines.forEach { line ->
            val index = products.indexOfFirst { it.id == line.product.id }
            if (index >= 0) {
                val current = products[index]
                products[index] = current.copy(stock = current.stock - line.quantity)
            }
        }

        val purchase = Purchase(
            id = nextPurchaseId++,
            summary = lines.joinToString(", ") { "${it.quantity}x ${it.product.name}" },
            total = lines.sumOf { it.subtotal },
            paymentMethod = paymentMethod
        )

        purchases.add(purchase)
        cart.clear()
        persist()
        return purchase
    }

    private fun persist() {
        runCatching {
            window.localStorage.setItem(STORAGE_KEY, buildString {
                appendLine("products=${products.joinToString(";") { "${it.id}|${it.name}|${it.category}|${it.price}|${it.stock}" }}")
                appendLine("cart=${cart.joinToString(",") { it.id.toString() }}")
                appendLine("purchases=${purchases.joinToString(";") { "${it.id}|${it.summary}|${it.total}|${it.paymentMethod}" }}")
                appendLine("nextId=$nextId")
                appendLine("nextPurchaseId=$nextPurchaseId")
            })
        }
    }

    private fun findProductById(id: Int): Product? = products.firstOrNull { it.id == id }
}