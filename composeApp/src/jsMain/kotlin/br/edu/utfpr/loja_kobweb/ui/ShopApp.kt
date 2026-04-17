package br.edu.utfpr.loja_kobweb.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.edu.utfpr.loja_kobweb.ui.navigation.AppRoute
import br.edu.utfpr.loja_kobweb.ui.pages.CartPage
import br.edu.utfpr.loja_kobweb.ui.pages.NotFoundPage
import br.edu.utfpr.loja_kobweb.ui.pages.ProductCreatePage
import br.edu.utfpr.loja_kobweb.ui.pages.ProductDetailPage
import br.edu.utfpr.loja_kobweb.ui.pages.ProductsPage
import br.edu.utfpr.loja_kobweb.ui.pages.PurchasesPage
import br.edu.utfpr.loja_kobweb.ui.pages.UsersPage
import kotlinx.browser.window
import org.w3c.dom.events.Event

@Composable
fun ShopApp() {
    var currentRoute by remember { mutableStateOf(AppRoute.parse(readCurrentLocation())) }

    val navigate: (AppRoute) -> Unit = { route ->
        window.location.hash = route.path
        currentRoute = route
    }

    DisposableEffect(Unit) {
        val hashChangeListener: (Event) -> Unit = {
            currentRoute = AppRoute.parse(readCurrentLocation())
        }

        window.addEventListener("hashchange", hashChangeListener)

        onDispose {
            window.removeEventListener("hashchange", hashChangeListener)
        }
    }

    when (val route = currentRoute) {
        AppRoute.Home -> App(onNavigate = navigate)
        AppRoute.Products -> ProductsPage(onNavigate = navigate)
        AppRoute.Cart -> CartPage(onNavigate = navigate)
        AppRoute.Purchases -> PurchasesPage(onNavigate = navigate)
        AppRoute.ProductCreate -> ProductCreatePage(onNavigate = navigate)
        AppRoute.Users -> UsersPage(onNavigate = navigate)
        is AppRoute.ProductDetail -> ProductDetailPage(productId = route.productId, onNavigate = navigate)
        AppRoute.NotFound -> NotFoundPage(onNavigate = navigate)
    }
}

private fun readCurrentLocation(): String {
    val hashRoute = window.location.hash
    if (hashRoute.isNotBlank()) {
        return hashRoute
    }

    return window.location.pathname
}