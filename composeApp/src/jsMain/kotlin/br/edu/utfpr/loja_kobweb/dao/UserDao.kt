package br.edu.utfpr.loja_kobweb.dao

import br.edu.utfpr.loja_kobweb.model.User
import br.edu.utfpr.loja_kobweb.model.UserRole

object UserDao {
	val users = DaoState.users

	data class ValidationResult(
		val valid: Boolean,
		val errors: List<String> = emptyList()
	)

	data class AuthResult(
		val user: User? = null,
		val error: String? = null
	)

	fun validateRegistration(
		name: String,
		email: String,
		password: String,
		confirmPassword: String
	): ValidationResult {
		val sanitizedName = name.trim()
		val sanitizedEmail = email.trim().lowercase()
		val errors = mutableListOf<String>()

		if (sanitizedName.length < 3) {
			errors += "Nome deve ter ao menos 3 caracteres."
		}

		if (!EMAIL_REGEX.matches(sanitizedEmail)) {
			errors += "E-mail invalido."
		}

		if (password.length < 8) {
			errors += "Senha deve ter ao menos 8 caracteres."
		}
		if (password.none { it.isDigit() }) {
			errors += "Senha deve conter ao menos 1 numero."
		}
		if (password.none { it.isUpperCase() }) {
			errors += "Senha deve conter ao menos 1 letra maiuscula."
		}

		if (password != confirmPassword) {
			errors += "Confirmacao de senha nao confere."
		}

		if (users.any { it.email.equals(sanitizedEmail, ignoreCase = true) }) {
			errors += "Ja existe usuario com este e-mail."
		}

		return ValidationResult(valid = errors.isEmpty(), errors = errors)
	}

	fun registerUser(
		name: String,
		email: String,
		password: String,
		confirmPassword: String,
		role: UserRole = UserRole.CUSTOMER
	): ValidationResult {
		val validation = validateRegistration(name, email, password, confirmPassword)
		if (!validation.valid) {
			return validation
		}

		val user = User(
			id = DaoState.nextUserId++,
			name = name.trim(),
			email = email.trim().lowercase(),
			password = password,
			role = role
		)

		users.add(user)
		DaoState.persist()

		return ValidationResult(valid = true)
	}

	fun authenticate(email: String, password: String): AuthResult {
		val sanitizedEmail = email.trim().lowercase()
		val user = users.firstOrNull { it.email == sanitizedEmail }
			?: return AuthResult(error = "Usuario nao encontrado.")

		if (!user.isActive) {
			return AuthResult(error = "Usuario inativo.")
		}

		if (user.password != password) {
			return AuthResult(error = "Senha invalida.")
		}

		return AuthResult(user = user)
	}

	fun setUserActive(userId: Int, isActive: Boolean): Boolean {
		val index = users.indexOfFirst { it.id == userId }
		if (index < 0) return false

		users[index] = users[index].copy(isActive = isActive)
		DaoState.persist()
		return true
	}

	fun canAccessAdminArea(user: User?): Boolean {
		return user != null && user.isActive && user.role == UserRole.ADMIN
	}

	private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
}
