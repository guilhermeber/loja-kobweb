package br.edu.utfpr.loja_kobweb.ui.navigation

sealed class AppRoute(val path: String) {
    data object Home : AppRoute("/")
    data object Products : AppRoute("/produtos")
    data object Cart : AppRoute("/carrinho")
    data object Purchases : AppRoute("/compras")
    data object ProductCreate : AppRoute("/admin/produtos/novo")
    data object Users : AppRoute("/admin/usuarios")
    data object NotFound : AppRoute("/404")

    data class ProductDetail(val productId: Int) : AppRoute("/produto/$productId")

    companion object {
        fun parse(rawRoute: String?): AppRoute {
            val normalizedRoute = normalize(rawRoute)

            return when {
                normalizedRoute == "/" -> Home
                normalizedRoute == Products.path -> Products
                normalizedRoute == Cart.path -> Cart
                normalizedRoute == Purchases.path -> Purchases
                normalizedRoute == ProductCreate.path -> ProductCreate
                normalizedRoute == Users.path -> Users
                normalizedRoute.startsWith("/produto/") -> {
                    normalizedRoute
                        .removePrefix("/produto/")
                        .toIntOrNull()
                        ?.let { ProductDetail(it) }
                        ?: NotFound
                }
                normalizedRoute == NotFound.path -> NotFound
                else -> NotFound
            }
        }

        fun normalize(rawRoute: String?): String {
            val withoutHash = rawRoute.orEmpty().removePrefix("#")
            return if (withoutHash.isBlank()) "/" else withoutHash.substringBefore("?")
        }
    }
}