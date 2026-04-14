package br.edu.utfpr.loja_kobweb.model

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val stock: Int
)