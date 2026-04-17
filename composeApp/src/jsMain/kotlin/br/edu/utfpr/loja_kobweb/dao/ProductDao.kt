package br.edu.utfpr.loja_kobweb.dao

import br.edu.utfpr.loja_kobweb.model.Product

object ProductDao {
    val products = DaoState.products

    fun findById(id: Int): Product? = products.firstOrNull { it.id == id }

    fun addProduct(name: String, category: String, price: Double, stock: Int): Product? {
        val sanitizedName = name.trim()
        val sanitizedCategory = category.trim()

        if (sanitizedName.isBlank() || sanitizedCategory.isBlank()) {
            return null
        }
        if (price <= 0.0 || stock < 0) {
            return null
        }

        val newProduct = Product(
            id = DaoState.nextProductId++,
            name = sanitizedName,
            category = sanitizedCategory,
            price = price,
            stock = stock
        )

        products.add(newProduct)
        DaoState.persist()
        return newProduct
    }

    fun reduceStock(productId: Int, quantity: Int): Boolean {
        if (quantity <= 0) return false

        val index = products.indexOfFirst { it.id == productId }
        if (index < 0) return false

        val current = products[index]
        if (quantity > current.stock) return false

        products[index] = current.copy(stock = current.stock - quantity)
        return true
    }
}