package br.edu.utfpr.loja_kobweb.model

data class User(
    val id: Int,
    val email: String,
    val password: String,
    val role: UserRole,
    val createdAt: Long
)

