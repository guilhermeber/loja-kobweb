package br.edu.utfpr.loja_kobweb.store

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import br.edu.utfpr.loja_kobweb.model.User
import br.edu.utfpr.loja_kobweb.model.UserRole
import kotlinx.browser.window
import org.jetbrains.compose.web.css.selectors.CSSSelector.PseudoClass.active
import kotlin.js.Date

object AuthStore {
    private const val STORAGE_KEY = "loja-kobweb-auth-v1"
    private const val ADMIN_EMAIL = "admin@utfpr.com"
    private const val ADMIN_PASSWORD = "admin123"

    public val users = mutableStateListOf<User>()
    var currentUser = mutableStateOf<User?>(null)
    var nextUserId = 1

    init {
        loadFromStorage()
        if (users.isEmpty()) {
            seedAdminUser()
        }
    }

    fun register(email: String, password: String): Pair<Boolean, String> {
        val emailTrimmed = email.trim()
        val passwordTrimmed = password.trim()

        // Validações
        if (emailTrimmed.isEmpty() || passwordTrimmed.isEmpty()) {
            return Pair(false, "Email e senha são obrigatórios.")
        }

        if (emailTrimmed.length < 5 || !emailTrimmed.contains("@")) {
            return Pair(false, "Email inválido.")
        }

        if (passwordTrimmed.length < 6) {
            return Pair(false, "Senha deve ter pelo menos 6 caracteres.")
        }

        if (users.any { it.email.equals(emailTrimmed, ignoreCase = true) }) {
            return Pair(false, "Email já cadastrado.")
        }

        val newUser = User(
            id = nextUserId++,
            active = false,
            email = emailTrimmed,
            password = passwordTrimmed,
            role = UserRole.CUSTOMER,
            createdAt = Date.now().toLong()
        )

        users.add(newUser)
        currentUser.value = newUser
        persist()
        return Pair(true, "Cadastro realizado com sucesso!")
    }

    fun login(email: String, password: String): Pair<Boolean, String> {
        val emailTrimmed = email.trim()
        val passwordTrimmed = password.trim()

        if (emailTrimmed.isEmpty() || passwordTrimmed.isEmpty()) {
            return Pair(false, "Email e senha são obrigatórios.")
        }

        val user = users.firstOrNull { 
            it.email.equals(emailTrimmed, ignoreCase = true) && it.password == passwordTrimmed
        }

        return if (user != null) {
            currentUser.value = user
            persist()
            Pair(true, "Login realizado com sucesso!")
        } else {
            Pair(false, "Email ou senha incorretos.")
        }
    }

    fun logout() {
        currentUser.value = null
        persist()
    }

    fun isAdmin(): Boolean = currentUser.value?.role == UserRole.ADMIN

    fun isLoggedIn(): Boolean = currentUser.value != null

    private fun seedAdminUser() {
        val adminUser = User(
            id = nextUserId++,
            active = false,
            email = ADMIN_EMAIL,
            password = ADMIN_PASSWORD,
            role = UserRole.ADMIN,
            createdAt = Date.now().toLong()
        )
        users.add(adminUser)
        persist()
    }

    fun findUserById(id: Int): User? {
        return users.find { it.id == id }
    }

    private fun persist() {
        runCatching {
            window.localStorage.setItem(STORAGE_KEY, buildString {
                appendLine("users=${users.joinToString(";") { "${it.id}|${it.email}|${it.password}|${it.role.name}|${it.createdAt}" }}")
                appendLine("currentUserId=${currentUser.value?.id ?: -1}")
                appendLine("nextUserId=$nextUserId")
            })
        }
    }

    private fun loadFromStorage() {
        runCatching {
            val data = window.localStorage.getItem(STORAGE_KEY) ?: return@runCatching
            val lines = data.split("\n")

            lines.forEach { line ->
                when {
                    line.startsWith("users=") -> {
                        val usersData = line.substringAfter("users=")
                        if (usersData.isNotEmpty()) {
                            usersData.split(";").forEach { userStr ->
                                val parts = userStr.split("|")
                                if (parts.size == 5) {
                                    try {
                                        val user = User(
                                            id = parts[0].toInt(),
                                            active = parts[1].toBoolean(),
                                            email = parts[2],
                                            password = parts[3],
                                            role = UserRole.valueOf(parts[4]),
                                            createdAt = parts[5].toLong()
                                        )
                                        users.add(user)
                                        nextUserId = maxOf(nextUserId, user.id + 1)
                                    } catch (e: Exception) {
                                        // Ignorar usuários com dados inválidos
                                    }
                                }
                            }
                        }
                    }
                    line.startsWith("currentUserId=") -> {
                        val userId = line.substringAfter("currentUserId=").toIntOrNull()
                        if (userId != null && userId > 0) {
                            currentUser.value = users.firstOrNull { it.id == userId }
                        }
                    }
                    line.startsWith("nextUserId=") -> {
                        nextUserId = line.substringAfter("nextUserId=").toIntOrNull() ?: nextUserId
                    }
                }
            }
        }
    }
}

