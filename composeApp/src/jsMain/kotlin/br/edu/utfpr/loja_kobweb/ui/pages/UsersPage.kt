package br.edu.utfpr.loja_kobweb.ui.pages

import androidx.compose.runtime.Composable
import br.edu.utfpr.loja_kobweb.ui.UserUi
import br.edu.utfpr.loja_kobweb.ui.components.PageShell
import br.edu.utfpr.loja_kobweb.ui.navigation.AppRoute

@Composable
fun UsersPage(onNavigate: (AppRoute) -> Unit) {
    PageShell(activeRoute = AppRoute.Users, onNavigate = onNavigate) {
        UserUi()
    }
}