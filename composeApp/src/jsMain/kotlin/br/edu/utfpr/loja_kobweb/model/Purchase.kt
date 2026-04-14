package br.edu.utfpr.loja_kobweb.model

data class Purchase(
    val id: Int,
    val summary: String,
    val total: Double,
    val paymentMethod: String,
)
