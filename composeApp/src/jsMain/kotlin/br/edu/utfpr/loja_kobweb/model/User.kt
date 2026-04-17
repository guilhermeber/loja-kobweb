package br.edu.utfpr.loja_kobweb.model

enum class UserRole {
	ADMIN,
	CUSTOMER
}

data class User(
	val id: Int,
	val name: String,
	val email: String,
	val password: String,
	val role: UserRole,
	val isActive: Boolean = true
)
