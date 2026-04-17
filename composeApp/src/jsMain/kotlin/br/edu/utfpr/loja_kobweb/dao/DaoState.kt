package br.edu.utfpr.loja_kobweb.dao

import androidx.compose.runtime.mutableStateListOf
import br.edu.utfpr.loja_kobweb.model.Product
import br.edu.utfpr.loja_kobweb.model.Purchase
import br.edu.utfpr.loja_kobweb.model.User
import br.edu.utfpr.loja_kobweb.model.UserRole
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

        seedInitialProducts()
        seedInitialUsers()
        persist()
    }

    private fun seedInitialProducts() {
        products.addAll(
            listOf(
                Product(nextProductId++, "Notebook Gamer Aurora", "Informatica", 4299.90, 10),
                Product(nextProductId++, "Headset Pulse Pro", "Audio", 349.90, 20),
                Product(nextProductId++, "Mouse Optico Vortex", "Perifericos", 149.90, 30),
                Product(nextProductId++, "Teclado Mecanico Neo", "Perifericos", 399.90, 18),
                Product(nextProductId++, "Monitor UltraWide 34", "Monitores", 1899.90, 12),
                Product(nextProductId++, "Cadeira Ergonomica Flux", "Moveis", 1199.90, 15),
                Product(nextProductId++, "Webcam Crystal 4K", "Informatica", 599.90, 25),
                Product(nextProductId++, "Caixa de Som Orbit", "Audio", 279.90, 28),
                Product(nextProductId++, "SSD NVMe 1TB Velocity", "Armazenamento", 499.90, 35),
                Product(nextProductId++, "HD Externo 2TB Titan", "Armazenamento", 429.90, 22),
                Product(nextProductId++, "Roteador Wi-Fi 6 AirLink", "Redes", 689.90, 16),
                Product(nextProductId++, "Switch Gigabit 8 Portas", "Redes", 329.90, 18),
                Product(nextProductId++, "Controle Sem Fio NovaPad", "Games", 259.90, 26),
                Product(nextProductId++, "Mesa Gamer Elevare", "Moveis", 899.90, 9),
                Product(nextProductId++, "Lampada Smart RGB Lumen", "Casa Inteligente", 99.90, 40),
                Product(nextProductId++, "Tomada Inteligente Plug+", "Casa Inteligente", 129.90, 32),
                Product(nextProductId++, "Nobreak Guard 1200VA", "Energia", 849.90, 11),
                Product(nextProductId++, "Filtro de Linha SafePower", "Energia", 89.90, 45),
                Product(nextProductId++, "Impressora Multifuncional JetAll", "Impressao", 799.90, 14),
                Product(nextProductId++, "Pacote Papel A4 500 Folhas", "Impressao", 34.90, 60),
                Product(nextProductId++, "Suporte Articulado para Monitor", "Acessorios", 219.90, 21),
                Product(nextProductId++, "Base Refrigerada para Notebook", "Acessorios", 169.90, 24),
                Product(nextProductId++, "Smartphone Horizon X", "Smartphones", 2399.90, 17),
                Product(nextProductId++, "Smartwatch Pulse Track", "Wearables", 699.90, 19),
                Product(nextProductId++, "Microfone Condensador VoicePro", "Audio", 459.90, 23),
                Product(nextProductId++, "Placa de Video RTX Storm", "Informatica", 3599.90, 8),
                Product(nextProductId++, "Hub USB-C 7 em 1", "Acessorios", 249.90, 27),
                Product(nextProductId++, "Carregador Turbo 65W", "Acessorios", 139.90, 34),
                Product(nextProductId++, "Projetor Full HD Vision", "Monitores", 2199.90, 7),
                Product(nextProductId++, "Mini PC Office Cube", "Informatica", 2799.90, 13),
                Product(nextProductId++, "Cadeira Presidente Orion", "Moveis", 1499.90, 10),
                Product(nextProductId++, "Fone Bluetooth Wave", "Audio", 199.90, 31),
                Product(nextProductId++, "Pulseira Smart FitBand", "Wearables", 299.90, 22),
                Product(nextProductId++, "Console Retro PixelBox", "Games", 1799.90, 12)
            )
        )
    }

    private fun seedInitialUsers() {
        users.addAll(
            listOf(
                User(
                    id = nextUserId++,
                    name = "Administrador",
                    email = "admin@loja.com",
                    password = "Admin123",
                    role = UserRole.ADMIN
                ),
                User(
                    id = nextUserId++,
                    name = "Cliente Demo",
                    email = "cliente@loja.com",
                    password = "Cliente123",
                    role = UserRole.CUSTOMER
                )
            )
        )
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