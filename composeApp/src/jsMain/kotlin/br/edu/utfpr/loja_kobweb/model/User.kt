package br.edu.utfpr.loja_kobweb.model

data class User(
    val id: Int,
    val active: Boolean,
    val email: String,
    val password: String,
    var role: UserRole,
    val createdAt: Long
)

