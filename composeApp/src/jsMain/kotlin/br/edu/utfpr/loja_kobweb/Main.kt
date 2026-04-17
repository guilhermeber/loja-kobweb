import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import br.edu.utfpr.loja_kobweb.ui.ShopApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        ShopApp()
    }
}
