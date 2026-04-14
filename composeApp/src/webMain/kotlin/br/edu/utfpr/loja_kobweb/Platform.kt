package br.edu.utfpr.loja_kobweb

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform